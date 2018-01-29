package org.akka.messages;

import scala.Serializable;

public interface FileMessage {
	
	public static class FolderJob implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private final String path;
		
		public FolderJob(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
		
	}
	
	public static class FolderJobResult implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private final int result;
		
		public FolderJobResult(int result) {
			this.result = result;
		}
		
		public int getResult() {
			return result;
		}
		
		@Override
		public String toString() {
			return String.valueOf(result);
		}
		
	}

	public static class FileJob implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private final String file;
		
		public FileJob(String file) {
			this.file = file;
		}
		
		public String getFile() {
			return file;
		}
	}
	
	public static class FileJobResult implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private final String fileName;
		
		public FileJobResult(String fileName) {
			this.fileName = fileName;
		}
		
		public String getFileName() {
			return fileName;
		}
		
		@Override
		public String toString() {
			return "File name: " + fileName;
		}
	}
	
	public static class FileJobFailed implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private final String reason;
		
		public FileJobFailed(String reason) {
			this.reason = reason;
		}
		
		public String getReason() {
			return reason;
		}
		
		@Override
		public String toString() {
			return "FileJobFailed(" + reason + ")";
		}
	}
}
