package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import support.Course;

public class Check {

	public static ArrayList<Course> checkFromFile() throws FileNotFoundException {
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
		
		return courseList;
	}
	
	public static void detailInfoGUI(ArrayList<Course> courseList) {
		ArrayList<JButton> allButton = new ArrayList<JButton>();
		
		JFrame win = new JFrame("Check Course");
		win.setLocation(300, 300);
		win.setSize(800, 800);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setLayout(new BorderLayout());
		
		Dimension labelPreferredSize = new Dimension(160, 120);
		Dimension buttonPreferredSize = new Dimension(160, 60);
		Dimension buttonsPreferredSize = new Dimension(500, 130);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
		buttonPanel.setPreferredSize(buttonsPreferredSize);
		
		JButton lastButton = new JButton("Last");
		JButton nextButton = new JButton("Next");
		JButton refreshButton = new JButton("Refresh");
		
		JLabel title = new JLabel("Course Name");
		title.setFont(new Font("Microsoft Yahei UI", Font.PLAIN, 40));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setPreferredSize(labelPreferredSize);
		
		allButton.add(lastButton);
		allButton.add(nextButton);
		allButton.add(refreshButton);
		
		lastButton.setEnabled(false);
		
		for (JButton button:allButton) {
			buttonPanel.add(button);
			button.setPreferredSize(buttonPreferredSize);
			button.setFont(new Font("Microsoft Yahei UI", Font.PLAIN, 20));
		}
		
		win.add(title, BorderLayout.NORTH);
		win.add(buttonPanel, BorderLayout.SOUTH);
		
		win.setVisible(true);
	}
	
	public static void main(String[] args) {
		ArrayList<Course> emptyList = new ArrayList<Course>();
		detailInfoGUI(emptyList);
	}

}
