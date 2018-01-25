package org.akka;

import java.io.File;
import scala.Serializable;

public class FileMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6287126993634228330L;
	private final File file;
	
	public FileMessage(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return this.file;
	}
}
