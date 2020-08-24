package com.nagarro.pdf.compressor.model;

public class Process {

	private String url;
	private String host;
	private String id;
	private String expires;
	private String maxsize;
	private String maxtime;
	private String concurrent;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getMaxsize() {
		return maxsize;
	}

	public void setMaxsize(String maxsize) {
		this.maxsize = maxsize;
	}

	public String getMaxtime() {
		return maxtime;
	}

	public void setMaxtime(String maxtime) {
		this.maxtime = maxtime;
	}

	public String getConcurrent() {
		return concurrent;
	}

	public void setConcurrent(String concurrent) {
		this.concurrent = concurrent;
	}

	@Override
	public String toString() {
		return "Process [url=" + url + ", host=" + host + ", id=" + id + ", expires=" + expires + ", maxsize=" + maxsize
				+ ", maxtime=" + maxtime + ", concurrent=" + concurrent + "]";
	}
	
	

}
