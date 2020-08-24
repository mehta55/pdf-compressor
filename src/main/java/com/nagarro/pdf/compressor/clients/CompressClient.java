package com.nagarro.pdf.compressor.clients;

import java.net.URI;
import java.util.Map;

import com.nagarro.pdf.compressor.model.InitiateResponse;
import com.nagarro.pdf.compressor.model.Process;

import feign.Headers;
import feign.RequestLine;

public interface CompressClient {

	@RequestLine(value = "POST")
	@Headers("Content-Type: application/json")
	Process createProcess(URI uri, Map<String, String> body);

	@RequestLine(value = "POST")
	@Headers("Content-Type: application/json")
	InitiateResponse initUpload(URI uri, Map<String, String> body);
	
	
}
