package client.model;

import java.awt.Image;

public class LibraryEntry {

	private String title;
	private String runTime;
	private String size;
	private String location;
	private String length;
	private String period;
	private String type;
	private Image thumbnail;
	
	
	public LibraryEntry(String title, String runTime, String size, String location, String length, String period, String type, Image thumbnail){
		this.title = title;
		this.runTime = runTime;
		this.size = size;
		this.location = location;
		this.length = length;
		this.period = period;
		this.type = type;
		this.thumbnail = thumbnail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRunTime() {
		return runTime;
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Image getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
	}

}
