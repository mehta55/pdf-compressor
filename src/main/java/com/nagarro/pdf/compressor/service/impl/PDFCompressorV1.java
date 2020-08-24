package com.nagarro.pdf.compressor.service.impl;

import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import com.nagarro.pdf.compressor.model.InitiateResponse;
import com.nagarro.pdf.compressor.model.Process;
import com.nagarro.pdf.compressor.model.ProcessStatus;
import com.nagarro.pdf.compressor.model.UploadedFile;

@Service
public class PDFCompressorV1 {
	
	private static final Logger LOG = LoggerFactory.getLogger(PDFCompressorV1.class);

	private String baseURI = "https://api.cloudconvert.com";

	private String dirPath = "src/main/resources";

	private String apiKey = "api-key";

	private int waitTime = 100;

	private String inputFormat = "pdf";

	private String outputFormat = "pdf";

	private RestTemplate restTemplate = new RestTemplate();

	public void compress() {

		File directory = new File(dirPath + "/input");
		List<File> files = Arrays.asList(directory.listFiles());
		files.forEach(this::compress);
	}

	private void compress(File file) {
		Instant start = Instant.now();
		LOG.info("- COMPRESSION STARTED -");
		LOG.info("compressing file: " + file.getName());
		LOG.info("size before compression: " + (file.length() / 1000) + "Kb");

		Process process = createProcess();
		InitiateResponse initialResponse = initiateUpload(process);
		uploadFile(file, initialResponse);
		waitForCompression(process);
		File compressedFile = downloadFile(initialResponse.getOutput().getUrl(), file.getName());

		LOG.info("compressed file: " + compressedFile.getName());
		LOG.info("size after compression: " + (compressedFile.length() / 1000) + "Kb");
		LOG.info("- COMPRESSION COMPLETE :: {}ms - \n \n", Duration.between(start, Instant.now()).toMillis());
	}

	private Process createProcess() {

		LOG.info("- CREATING PROCESS -");

		Instant start = Instant.now();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("apikey", apiKey);
		map.add("inputformat", inputFormat);
		map.add("outputformat", outputFormat);

		HttpEntity<MultiValueMap<String, String>> requestBody = new HttpEntity<>(map, headers);
		String createProcessURI = baseURI + "/process";

		ResponseEntity<com.nagarro.pdf.compressor.model.Process> response = restTemplate.exchange(createProcessURI,
				HttpMethod.POST, requestBody, com.nagarro.pdf.compressor.model.Process.class);

		Process resp = response.getBody();
		LOG.debug(createProcessURI);
		LOG.debug(resp.toString());
		LOG.info("- PROCESS CREATED :: {}ms -", Duration.between(start, Instant.now()).toMillis());
		return resp;
	}

	private InitiateResponse initiateUpload(Process process) {
		LOG.info("- INITIATING UPLOAD -");

		Instant start = Instant.now();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("apikey", apiKey);
		map.add("input", "upload");
		map.add("outputformat", outputFormat);
		map.add("mode", "compress");

		URI initiateURI = prepareURI(process.getUrl());
		HttpEntity<MultiValueMap<String, Object>> requestBody = new HttpEntity<>(map, headers);

		ResponseEntity<InitiateResponse> response = restTemplate.exchange(initiateURI, HttpMethod.POST, requestBody,
				InitiateResponse.class);
		InitiateResponse initiateResponse = response.getBody();

		LOG.debug(initiateURI.toString());
		LOG.debug(initiateResponse.toString());
		LOG.info("- UPLOAD INITIATED :: {}ms -", Duration.between(start, Instant.now()).toMillis());

		return initiateResponse;
	}

	private void uploadFile(File file, InitiateResponse uploadResponse) {
		LOG.info("- UPLOADING FILE -");
		
		Instant start = Instant.now();

		URI serverUrl = prepareURI(uploadResponse.getUpload().getUrl());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(file));
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<UploadedFile> resp = restTemplate.postForEntity(serverUrl, requestEntity, UploadedFile.class);

		LOG.debug(serverUrl.toString());
		LOG.debug(resp.getBody().toString());
		LOG.info("- FILE UPLOADED :: {}ms -", Duration.between(start, Instant.now()).toMillis());
		
	}
	
	private void waitForCompression(Process process) {
		while (!isProcessStatusFinished(process)) {
			try {
				LOG.info(format("waiting for compression to finish. retry after: %d ms", waitTime));
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isProcessStatusFinished(Process process) {
		ResponseEntity<ProcessStatus> response = restTemplate.getForEntity(baseURI + "/process/" + process.getId(),
				ProcessStatus.class);
		
		LOG.info("compression status: " + response.getBody().getStep());
		return response.getBody().getStep().equals("finished");
	}

	private File downloadFile(String downloadURL, String orgFileName) {
		LOG.info("- DOWNLOADING FILE -");
		Instant start = Instant.now();
		
		URI downloadURI = prepareURI(downloadURL);
		File file = restTemplate.execute(downloadURI, HttpMethod.GET, null, clientHttpResponse -> {
			File ret = new File(dirPath + "/output/" + prepareOutputFileName(orgFileName));
			StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
			return ret;
		});

		LOG.debug(downloadURI.toString());
		LOG.info("- DOWNLOADING COMPLETE :: {}ms -", Duration.between(start, Instant.now()).toMillis());

		return file;

	}

	private String prepareOutputFileName(String orgFileName) {
		return orgFileName.substring(0, (orgFileName.length() - inputFormat.length() - 1)) + "_compressed."
				+ outputFormat;
	}

	private URI prepareURI(String uploadURI) {
		return URI.create("https:" + uploadURI);
	}
}
