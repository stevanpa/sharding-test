package org.akka.messages;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;


public interface FileMessage {
	
	public static class FolderJob implements Serializable {
		
		private static final long serialVersionUID = 6406363107852823088L;
		private final Path path;
		
		public FolderJob(Path path) {
			this.path = path;
		}
		
		public Path getPath() {
			return path;
		}
		
	}

	public static class FileJob {
		
		private final File file;
		
		public FileJob(File file) {
			this.file = file;
		}
		
		public File getFile() {
			return file;
		}
	}
	
	public static class FileJobResult {
		
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
	
	public static class FileJobFailed {
		
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
