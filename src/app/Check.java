package app;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import support.Course;

public class Check {

	public static void checkFromFile() throws FileNotFoundException {
		String filePath = "src/support/index.txt";
		String delimiter = ",";
		String[] lineArray;
		String semester, courseId, filter;
		ArrayList<Course> courseList = new ArrayList<Course>();
		Course course;
		ArrayList<String> filterList;

		Scanner scanner = new Scanner(new File(filePath));
		semester = scanner.nextLine();

		if (!Course.checkSemester(semester)) {
			semester = null;
			scanner = new Scanner(new File(filePath));
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
	}
	
	public static void main(String[] args) {
		JFrame win = new JFrame("Check Course");
		win.setLocation(300, 300);
		win.setSize(800, 800);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setLayout(new BorderLayout());
		
		JButton lastButton = new JButton("Last");
		JButton nextButton = new JButton("Next");
		JButton refreshButton = new JButton("Refresh");
		JLabel title = new JLabel("Title");
		
		win.add(lastButton, BorderLayout.WEST);
		win.add(nextButton, BorderLayout.EAST);
		win.add(refreshButton, BorderLayout.SOUTH);
		win.add(title, BorderLayout.NORTH);
		
		win.setVisible(true);
		
	}

}
