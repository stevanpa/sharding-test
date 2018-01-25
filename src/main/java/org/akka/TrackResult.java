package org.akka;

import java.util.List;

import scala.Serializable;

public class TrackResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7829834915024969699L;
	private String fileName;
	private List<Integer> pointList;
	
	public TrackResult(String fileName, List<Integer> pointList) {
		this.pointList = pointList;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public List<Integer> getPointList() {
		return this.pointList;
	}
	
	public void setPointList(List<Integer> pointList) {
		this.pointList = pointList;
	}
	
	public void addPoint(Integer point) {
		this.pointList.add(point);
	}
}
