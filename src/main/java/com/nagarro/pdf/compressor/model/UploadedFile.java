package com.nagarro.pdf.compressor.model;

public class UploadedFile {

	private String file;
	private String size;
	private String message;

	public String getFile() {
		return file;
	}

	public String getSize() {
		return size;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "UploadedFile [file=" + file + ", size=" + size + ", message=" + message + "]";
	}
	
	

}
