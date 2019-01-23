package app;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import support.Course;

public class Background {

	public static void main(String[] args) {
		ArrayList<Course> courseList = null;
		try {
			courseList = Check.checkFromFile();
		} catch (FileNotFoundException e) {
			FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
		    dialog.setMode(FileDialog.LOAD);
		    dialog.setFile("*.txt");
		    dialog.setVisible(true);
		    File file = new File(dialog.getDirectory() + dialog.getFile());
		    try {
				courseList = Check.checkFromFile(file, false);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} finally {
			backgroundCheck(courseList);
		}
	}

	public static void backgroundCheck(ArrayList<Course> courseList) {
		ArrayList<Course> removeCourse = new ArrayList<Course>();
		while (!courseList.isEmpty()) {
			for (Course course:courseList) {
				if (course.available()) {
					new GUI(course);
					removeCourse.add(course);
				} else {
					course.update();
				}

			}
			
			if (!removeCourse.isEmpty()) {
				for (Course course:removeCourse) {
					courseList.remove(course);
				}
				removeCourse.clear();
			}
			
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
