package org.akka;

import java.util.List;

import scala.Serializable;

public class TrackResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7829834915024969699L;
	private final String fileName;
	private final List<Integer> pointList;
	
	public TrackResult(String fileName, List<Integer> pointList) {
		this.fileName = fileName;
		this.pointList = pointList;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public List<Integer> getPointList() {
		return this.pointList;
	}
	
	public void addPoint(Integer point) {
		this.pointList.add(point);
	}
}
