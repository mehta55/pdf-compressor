package com.nagarro.pdf.compressor.service.impl;

import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.nagarro.pdf.compressor.model.InitiateResponse;
import com.nagarro.pdf.compressor.model.Process;
import com.nagarro.pdf.compressor.model.ProcessStatus;
import com.nagarro.pdf.compressor.model.UploadedFile;

import reactor.core.publisher.Flux;

@Service
public class PDFCompressorV3 {

	private static final Logger LOG = LoggerFactory.getLogger(PDFCompressorV3.class);

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

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("apikey", apiKey);
		map.add("inputformat", inputFormat);
		map.add("outputformat", outputFormat);

		String createProcessURI = baseURI + "/process";

		Process process = WebClient.builder().build().post()
				 .uri(createProcessURI).contentType(MediaType.MULTIPART_FORM_DATA)
				 .body(BodyInserters.fromFormData(map))
				 .retrieve()
				 .bodyToMono(Process.class)
				 .block();
		
		LOG.debug(createProcessURI);
		LOG.debug(process.toString());
		LOG.info("- PROCESS CREATED :: {}ms -", Duration.between(start, Instant.now()).toMillis());
		return process;
	}

	private InitiateResponse initiateUpload(Process process) {
		LOG.info("- INITIATING UPLOAD -");
		
		Instant start = Instant.now();

		URI initiateURI = prepareURI(process.getUrl());

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("apikey", apiKey);
		map.add("input", "upload");
		map.add("outputformat", outputFormat);
		map.add("mode", "compress");

		InitiateResponse initiateResponse = WebClient.builder().build().post()
				.uri(initiateURI).contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromFormData(map))
				.retrieve()
				.bodyToMono(InitiateResponse.class)
				.block();

		LOG.debug(initiateURI.toString());
		LOG.debug(initiateResponse.toString());
		LOG.info("- UPLOAD INITIATED :: {}ms -", Duration.between(start, Instant.now()).toMillis());

		return initiateResponse;
	}

	private void uploadFile(File file, InitiateResponse uploadResponse) {
		LOG.info("- UPLOADING FILE -");

		Instant start = Instant.now();

		URI serverUrl = prepareURI(uploadResponse.getUpload().getUrl());

		UploadedFile uploadedFile = WebClient.builder().build().post()
				.uri(serverUrl)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData("file", new FileSystemResource(file)))
				.exchange()
				.flatMap(res -> res.bodyToMono(UploadedFile.class))
				.flux().blockFirst();

		LOG.debug(serverUrl.toString());
		LOG.debug(uploadedFile.toString());
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

		Flux<DataBuffer> dataBufferFlux = WebClient.builder().build().get()
				.uri(downloadURI)
				.accept(MediaType.APPLICATION_PDF)
				.retrieve()
				.bodyToFlux(DataBuffer.class);

		Path path = FileSystems.getDefault().getPath(dirPath + "/output/" + prepareOutputFileName(orgFileName));
		DataBufferUtils.write(dataBufferFlux, path, CREATE).block();

		LOG.debug(downloadURI.toString());
		LOG.info("- DOWNLOADING COMPLETE :: {}ms -", Duration.between(start, Instant.now()).toMillis());

		return path.toFile();

	}

	private String prepareOutputFileName(String orgFileName) {
		return orgFileName.substring(0, (orgFileName.length() - inputFormat.length() - 1)) + "_compressed."
				+ outputFormat;
	}

	private URI prepareURI(String uploadURI) {
		return URI.create("https:" + uploadURI);
	}
}
