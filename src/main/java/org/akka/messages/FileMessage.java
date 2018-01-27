package org.akka.messages;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

public interface FileMessage {
	
	public static class FolderJob implements Serializable {
		
		private static final long serialVersionUID = 6406363107852823087L;
		private final Path path;
		
		public FolderJob(Path path) {
			this.path = path;
		}
		
		public Path getPath() {
			return path;
		}
		
	}

	public static class FileJob implements Serializable {
		
		private static final long serialVersionUID = -3412477979368064026L;
		private final File file;
		
		public FileJob(File file) {
			this.file = file;
		}
		
		public File getFile() {
			return file;
		}
	}
	
	public static class FileJobResult implements Serializable {
		
		private static final long serialVersionUID = 6186590637533094972L;
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
		
		private static final long serialVersionUID = 2059404488982554451L;
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
