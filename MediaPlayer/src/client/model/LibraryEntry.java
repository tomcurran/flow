package client.model;

public class LibraryEntry {

	private String title;
	private String runTime;
	private String size;
	
	public LibraryEntry(String title, String runTime, String size){
		this.title = title;
		this.runTime = runTime;
		this.size = size;
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
}
