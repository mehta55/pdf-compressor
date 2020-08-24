package com.nagarro.pdf.compressor.model;

public class InitiateResponse {

	private String id;
	private String url;
	private String expire;
	private String percent;
	private String message;
	private String step;
	private String starttime;
	private Output output;
	private Input input;
	private Upload upload;

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getExpire() {
		return expire;
	}

	public String getPercent() {
		return percent;
	}

	public String getMessage() {
		return message;
	}

	public String getStep() {
		return step;
	}

	public String getStarttime() {
		return starttime;
	}

	public Output getOutput() {
		return output;
	}

	public Input getInput() {
		return input;
	}

	public Upload getUpload() {
		return upload;
	}

	public class Output {
		private String url;

		public String getUrl() {
			return url;
		}

		@Override
		public String toString() {
			return "Output [url=" + url + "]";
		}

	}

	public class Input {
		private String type;

		public String getType() {
			return type;
		}

		@Override
		public String toString() {
			return "Input [type=" + type + "]";
		}

	}

	public class Upload {
		private String url;

		public String getUrl() {
			return url;
		}

		@Override
		public String toString() {
			return "Upload [url=" + url + "]";
		}

	}

	@Override
	public String toString() {
		return "InitiateResponse [id=" + id + ", url=" + url + ", expire=" + expire + ", percent=" + percent
				+ ", message=" + message + ", step=" + step + ", starttime=" + starttime + ", output=" + output
				+ ", input=" + input + ", upload=" + upload + "]";
	}

}
