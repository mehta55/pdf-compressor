package com.nagarro.pdf.compressor.model;

public class ProcessStatus {

	private String apikey;
	private String starttime;
	private String endtime;
	private String host;
	private String expires;
	private String maxtime;
	private String maxsize;
	private String memory;
	private String cpushares;
	private String step;
	private String scope;

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getMaxtime() {
		return maxtime;
	}

	public void setMaxtime(String maxtime) {
		this.maxtime = maxtime;
	}

	public String getMaxsize() {
		return maxsize;
	}

	public void setMaxsize(String maxsize) {
		this.maxsize = maxsize;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getCpushares() {
		return cpushares;
	}

	public void setCpushares(String cpushares) {
		this.cpushares = cpushares;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		return "ProcessStatus [apikey=" + apikey + ", starttime=" + starttime + ", endtime=" + endtime + ", host="
				+ host + ", expires=" + expires + ", maxtime=" + maxtime + ", maxsize=" + maxsize + ", memory=" + memory
				+ ", cpushares=" + cpushares + ", step=" + step + ", scope=" + scope + "]";
	}
	
	

}
