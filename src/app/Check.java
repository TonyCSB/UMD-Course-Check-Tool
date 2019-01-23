package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import support.Course;

public class Check {
	
	public static ArrayList<Course> checkFromFile() throws FileNotFoundException {
		String filePath = "index.txt";
		File inFile = new File(filePath);
		return checkFromFile(inFile, false);
	}
	
	public static ArrayList<Course> checkFromFile(File inFile, Boolean print) throws FileNotFoundException {
		String delimiter = ",";
		String[] lineArray;
		String semester, courseId, filter;
		ArrayList<Course> courseList = new ArrayList<Course>();
		Course course;
		ArrayList<String> filterList;

		Scanner scanner = new Scanner(inFile);
		semester = scanner.nextLine();

		if (!Course.checkSemester(semester)) {
			semester = null;
			scanner.close();
			scanner = new Scanner(inFile);
		}

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			lineArray = line.split(delimiter);

			courseId = lineArray[0];
			course = new Course(courseId, semester, false);
			courseList.add(course);

			if (lineArray.length == 3) {
				if (lineArray[1].trim().toLowerCase().equals("prof")) {
					filter = lineArray[2].trim();
					filterList = new ArrayList<String>(Arrays.asList(filter.split("/")));
					course.setInstructorFilter(filterList);
				} else if (lineArray[1].trim().toLowerCase().equals("period")) {
					filter = lineArray[2].trim();
					filterList = new ArrayList<String>(Arrays.asList(filter.split("/")));
					course.setSectionFilter(filterList);
				}
			}
			
			course.update();

		}

		scanner.close();
		
		if (print) {
			for (Course c:courseList) {
				System.out.println(c);
			}
		}
		
		return courseList;
	}

}
