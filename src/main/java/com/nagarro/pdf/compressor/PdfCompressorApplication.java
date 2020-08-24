package com.nagarro.pdf.compressor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.nagarro.pdf.compressor.clients.CompressClient;

import feign.Feign;
import feign.Logger.Level;
import feign.Target;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

@SpringBootApplication
public class PdfCompressorApplication {

	public static void main(String[] args) {
		ApplicationContext cntxt = SpringApplication.run(PdfCompressorApplication.class, args);
	}
	
	@Bean
	public CompressClient getCompressClient() {
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.logger(new Slf4jLogger(CompressClient.class))
				.logLevel(Level.FULL)
				.target(Target.EmptyTarget.create(CompressClient.class));
	}

}
