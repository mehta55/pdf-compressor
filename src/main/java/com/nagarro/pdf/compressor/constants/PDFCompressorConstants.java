package com.nagarro.pdf.compressor.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PDFCompressorConstants {

	
	public static String baseURI = "https://api.cloudconvert.com";

	public static String dirPath = "src/main/resources";

	private String apiKey = "api-key";


	public static int waitTime = 100;

	public static String inputFormat = "pdf";

	public static String outputFormat = "pdf";
	
	public static HttpEntity<MultiValueMap<String, String>> createProcessReqBody;
	
	public static HttpEntity<MultiValueMap<String, Object>> initUploadReqBody;
	
	public static String inputDir = dirPath + "/input";
	
	public static String createProcessURI = baseURI + "/process";
	
	public static HttpHeaders headers;
	
	public static Map<String, String> createProcessBody;
	public static Map<String, String> initUploadBody;
	
	static {
		prepare();
//		prepareheaders();
//		preprocessCreateProcessReqBody();
//		prepareInitUploadReqBody();
	}
	
	private static void preprocessCreateProcessReqBody() {
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("apikey", apiKey);
		map.add("inputformat", inputFormat);
		map.add("outputformat", outputFormat);
		
		createProcessReqBody = new HttpEntity<>(map, headers);
		
	}
	
	private static void prepare() {
		createProcessBody = new HashMap<>();
		createProcessBody.put("apikey", apiKey);
		createProcessBody.put("inputformat", inputFormat);
		createProcessBody.put("outputformat", outputFormat);
		
		initUploadBody = new HashMap<>();
		initUploadBody.put("apikey", apiKey);
		initUploadBody.put("input", "upload");
		initUploadBody.put("outputformat", outputFormat);
		initUploadBody.put("mode", "compress");
		
	}

	private static void prepareheaders() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	}

	private static void prepareInitUploadReqBody() {
	
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("apikey", apiKey);
		map.add("input", "upload");
		map.add("outputformat", outputFormat);
		map.add("mode", "compress");

		initUploadReqBody = new HttpEntity<>(map, headers);
	}
}
