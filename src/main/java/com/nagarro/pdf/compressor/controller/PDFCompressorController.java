package com.nagarro.pdf.compressor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.pdf.compressor.service.impl.PDFCompressorV3;
import com.nagarro.pdf.compressor.service.impl.PDFCompressorV1;
import com.nagarro.pdf.compressor.service.impl.PDFCompressorV2;
import com.nagarro.pdf.compressor.service.impl.PDFCompressorV4;

@RestController
@RequestMapping("/compress")
public class PDFCompressorController {

	@Autowired
	private PDFCompressorV1 compressorV1;

	@Autowired
	private PDFCompressorV2 compressorV2;

	@Autowired
	private PDFCompressorV3 compressorV3;

	@Autowired
	private PDFCompressorV4 compressorV4;
	
	@GetMapping("/pdf/preprocessed")
	public ResponseEntity<String> compress2() {
		compressorV2.compress();
		return ResponseEntity.ok("compression completed");
	}

	@GetMapping("/pdf/rest")
	public ResponseEntity<String> compress() {
		compressorV1.compress();
		return ResponseEntity.ok("compression completed");
	}
	
	@GetMapping("/pdf/webclient")
	public ResponseEntity<String> compress3() {
		compressorV3.compress();
		return ResponseEntity.ok("compression completed");
	}

	@GetMapping("/pdf/feign")
	public ResponseEntity<String> compressV4() {
		compressorV4.compress();
		return ResponseEntity.ok("compression completed");
	}
	
}
