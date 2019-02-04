package support;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Section extends Course {
	private String sectionId;
	private String instructor;
	private int openSeat;
	private int totalSeat;
	private int waitlist;
	private int holdfile;
	private Boolean valid = true;
	
	public Section(Course course, String sectionId, Document sectionInfo) {
		super(course);
		this.sectionId = sectionId;
		
		getAllInfo(sectionInfo);
		
		if (course.getInstructorFilter() != null) {
			valid = course.getInstructorFilter().contains(instructor);
		} 
		if (course.getSectionFilter() != null) {
			valid = course.getSectionFilter().contains(sectionId);
		}
	}
	
	private void getAllInfo(Document sectionInfo) {
		instructor = sectionInfo.getElementsByClass("section-instructor").text();
		totalSeat = Integer.parseInt(sectionInfo.getElementsByClass("total-seats-count").text());
		openSeat = Integer.parseInt(sectionInfo.getElementsByClass("open-seats-count").text());
		
		Elements elements = sectionInfo.getElementsByClass("waitlist-count");
		if (elements.size() == 2) {
			waitlist = Integer.parseInt(elements.get(0).text());
			holdfile = Integer.parseInt(elements.get(1).text());
		} else {
			waitlist = Integer.parseInt(elements.text());
			holdfile = 0;
		}
	}
	
	public boolean equals(Object obj) { 
		if (obj == this) { 
			return true; 
		} 
		if (obj == null || getClass() != obj.getClass()) { 
			return false; 
		}
		
		Section section = (Section) obj;
		
		return sectionId.equals(section.sectionId);
	}
	
	public String[] getInfo() {
		String[] info = new String[] {sectionId, instructor, seatsAvailable(), Integer.toString(waitlist)};
		
		return info;
	}
	
	public Boolean isAvailable() {
		return openSeat > 0;
	}
	
	public String toString() {
		return sectionId + ", " + instructor + ", " + getSeatCounts();
	}
	
	private String seatsAvailable() {
		return openSeat + "/" + totalSeat;
	}
	
	public String getSeatCounts() {
		return openSeat + "/" + totalSeat + ", " + waitlist + " " + holdfile;
	}

	public String getSectionId() {
		return sectionId;
	}

	public String getInstructor() {
		return instructor;
	}

	public int getOpenSeat() {
		return openSeat;
	}

	public int getTotalSeat() {
		return totalSeat;
	}

	public int getWaitlist() {
		return waitlist;
	}

	public int getHoldfile() {
		return holdfile;
	}
	
	public Boolean isValid() {
		return valid;
	}
}
