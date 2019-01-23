package support;

import java.util.ArrayList;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Course {
	private String courseId;
	private String semester;
	private int credits;
	private String genEd;
	private ArrayList<Section> sections = new ArrayList<Section>();
	private String courseName;
	private String deptId;
	private String department;
	private String gradingMethod;
	private ArrayList<String> instructorFilter = null;
	private ArrayList<String> sectionFilter = null;

	public Course(String courseId, String semester, Boolean update) {
		if (semester == null) {
			semester = defaultSemester();
		}
		
		if (!checkCouseId(courseId)) {
			throw new IllegalArgumentException("Invalid Course ID!");
		}

		if (!checkSemester(semester)) {
			throw new IllegalArgumentException("Invalid Semester!");
		}

		this.courseId = courseId.toUpperCase();
		this.semester = semester;
		this.deptId = courseId.substring(0, 4);

		if (update == true) {
			try {
				getAllInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setInstructorFilter(ArrayList<String> instructorFilter) {
		this.instructorFilter = instructorFilter;
		update();
	}

	public void setSectionFilter(ArrayList<String> sectionFilter) {
		this.sectionFilter = sectionFilter;
		update();
	}

	public Course(String courseId, String semester) {
		this(courseId, semester, true);
	}

	public Course(String courseId, int semester) {
		this(courseId, Integer.toString(semester));
	}
	
	public Course(String courseId) {
		this(courseId, defaultSemester());
	}
	
	private static String defaultSemester() {
		String semester = "";
		
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		
		if (month >= 1 && month < 5) {
			month = 1;
		} else if (month < 7) {
			month = 5;
		} else if (month < 12) {
			month = 8;
		}
		
		if (month < 10) {
			semester = year + "0" + month;
		} else {
			semester = year + "" + month;
		}
		
		return semester;
	}

	public String toString() {
		update();
		return courseId + " Credits: " + credits + "\n" + "GenEd: " + genEd + "\n"
				+ "Grading Methods: " + gradingMethod + "\n" + sections + "\n";
	}

	public String getCourseId() {
		return courseId;
	}

	public String getSemester() {
		return semester;
	}

	public int getCredits() {
		return credits;
	}

	public String getGenEd() {
		return genEd;
	}

	private static Boolean checkCouseId(String courseId) {
		if (courseId == null || courseId.length() < 7) {
			return false;
		}

		if (!courseId.substring(0, 4).chars().allMatch(Character::isLetter)) {
			return false;
		}

		try {
			Integer.parseInt(courseId.substring(4, 7));
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	public static Boolean checkSemester(String semester) {
		if (semester.length() != 6) {
			return false;
		}

		int year, sem;

		try {
			year = Integer.parseInt(semester.substring(0, 4));
			sem = Integer.parseInt(semester.substring(4));
		} catch (NumberFormatException e) {
			return false;
		}

		if (year < 2000 || year > 2999) {
			return false;
		}

		if (sem != 8 && sem != 12 && sem != 1 && sem != 5) {
			return false;
		}

		return true;
	}

	private void getAllInfo() throws Exception{
		String url = "https://app.testudo.umd.edu/soc/search?courseId=" + courseId + "&sectionId=&termId=" + semester + "&_openSectionsOnly=on&creditCompare=&credits=&courseLevelFilter=ALL&instructor=&_facetoface=on&_blended=on&_online=on&courseStartCompare=&courseStartHour=&courseStartMin=&courseStartAM=&courseEndHour=&courseEndMin=&courseEndAM=&teachingCenter=ALL&_classDay1=on&_classDay2=on&_classDay3=on&_classDay4=on&_classDay5=on";
		Document doc = Jsoup.connect(url).get();

		department = doc.getElementsByClass("course-prefix-name").text();

		Element courseInfo = doc.getElementById(courseId);
		Document course = Jsoup.parseBodyFragment(courseInfo.html());

		courseName = course.getElementsByClass("course-title").text();
		credits = Integer.parseInt(course.getElementsByClass("course-min-credits").text());
		gradingMethod = course.getElementsByClass("grading-method").text();
		genEd = course.getElementsByClass("course-subcategory").text();

		Elements sections = course.getElementsByClass("section-info-container");

		for (Element section:sections) {
			Document sectionInfo = Jsoup.parseBodyFragment(section.html());
			String sectionId = sectionInfo.getElementsByClass("section-id").text();

			Section sect = new Section(this, sectionId, sectionInfo);
			if (sect.isValid()) {
				this.sections.add(sect);
			}
		}
	}

	public void update() {
		sections.clear();

		try {
			getAllInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Section> getSections() {
		return sections;
	}

	public String getCourseName() {
		return courseName;
	}

	public String getDeptId() {
		return deptId;
	}

	public String getDepartment() {
		return department;
	}

	public String getGradingMethod() {
		return gradingMethod;
	}

	public ArrayList<String> getInstructorFilter() {
		return instructorFilter;
	}

	public ArrayList<String> getSectionFilter() {
		return sectionFilter;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		Course course = (Course) obj;

		Boolean checkCourseId = courseId.equals(course.courseId);
		Boolean checkSemester = semester.equals(course.semester);

		return checkCourseId && checkSemester;
	}
	
	public String[][] getSectionsInfo() {
		String[][] sectionsInfo = new String[sections.size()][4];
		
		for (int i = 0; i < sections.size(); i++) {
			sectionsInfo[i] = sections.get(i).getInfo();
		}
		
		return sectionsInfo;
	}

}
