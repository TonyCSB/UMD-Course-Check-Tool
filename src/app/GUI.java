package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import support.Course;

public class GUI {
	private Course courseDisplaying;
	private ArrayList<Course> courseList;
	
	public GUI(Course course) {
		ArrayList<Course> courseList = new ArrayList<Course>();
		courseList.add(course);
		new GUI(courseList);
	}

	public GUI(ArrayList<Course> courseList) {
		// ArrayList of all JButton objects and all JLabel objects containing course info.
		// Used to change format altogether easily.
		ArrayList<JButton> allButton = new ArrayList<JButton>();
		this.courseList = courseList;

		// Main window
		JFrame win = new JFrame("Check Course");
		win.setLocation(300, 300);
		win.setSize(800, 800);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setLayout(new BorderLayout(40, 0));

		// Dimensions for most elements in the GUI
		Dimension labelPreferredSize = new Dimension(160, 120);
		Dimension buttonPreferredSize = new Dimension(160, 60);
		Dimension buttonsPreferredSize = new Dimension(500, 130);

		// Buttons and their dedicated JPanel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
		buttonPanel.setPreferredSize(buttonsPreferredSize);

		JButton lastButton = new JButton("Last");
		JButton nextButton = new JButton("Next");
		JButton refreshButton = new JButton("Refresh");

		allButton.add(lastButton);
		allButton.add(nextButton);
		allButton.add(refreshButton);

		lastButton.setEnabled(false);

		for (JButton button:allButton) {
			buttonPanel.add(button);
			button.setPreferredSize(buttonPreferredSize);
			button.setFont(new Font("Microsoft Yahei UI", Font.PLAIN, 20));
		}

		// Main title of the window
		JLabel title = new JLabel("Course Name");
		title.setFont(new Font("Microsoft Yahei UI", Font.PLAIN, 40));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setPreferredSize(labelPreferredSize);

		// Displays all course related information
		JPanel coursePanel = new JPanel(new BorderLayout(0, 20));

		JLabel courseInfo = new JLabel();
		String courseInfoText = MessageFormat.format("<html>Credits: {0}<br/>GenEd: "
				+ "{1}<br/>Grading Methods: {2}</html>", 3, "N/A", "Reg");
		courseInfo.setText(courseInfoText);
		coursePanel.add(courseInfo, BorderLayout.NORTH);

		courseInfo.setFont(new Font("Microsoft Yahei UI", Font.PLAIN, 20));

		String[] columnNames = {"Section ID", "Instructor", "Seats Available", "Waitlist"};
		@SuppressWarnings("serial")
		DefaultTableModel model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable sectionInfoTable = new JTable(model);
		sectionInfoTable.setFont(new Font("Microsoft Yahei UI", Font.PLAIN, 18));
		sectionInfoTable.setRowHeight(25);

		JTableHeader header = sectionInfoTable.getTableHeader();
		header.setFont(new Font("Microsoft Yahei UI", Font.BOLD, 20));

		for (String columnName:columnNames) {
			model.addColumn(columnName);
		}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(sectionInfoTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		coursePanel.add(scrollPane, BorderLayout.CENTER);


		// Adds all elements to the main window
		win.add(title, BorderLayout.NORTH);
		win.add(buttonPanel, BorderLayout.SOUTH);
		win.add(coursePanel, BorderLayout.CENTER);
		win.add(new JPanel(), BorderLayout.WEST);
		win.add(new JPanel(), BorderLayout.EAST);
		win.setVisible(true);
		
		Course course = courseList.get(0);
		displayInfo(course, title, courseInfo, model);
		
		if (courseList.size() == 1) {
			nextButton.setEnabled(false);
		}
		
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lastButton.setEnabled(true);
				displayInfo(getNextCourse(), title, courseInfo, model);
				if (courseList.indexOf(courseDisplaying) == courseList.size() - 1) {
					nextButton.setEnabled(false);
				}
			}
		});
		
		lastButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayInfo(getLastCourse(), title, courseInfo, model);
				nextButton.setEnabled(true);
				if (courseList.indexOf(courseDisplaying) == 0) {
					lastButton.setEnabled(false);
				}
			}
		});
		
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				courseDisplaying.update();
				displayInfo(courseDisplaying, title, courseInfo, model);
			}
		});
	}
	
	private Course getLastCourse() {
		int index = courseList.indexOf(courseDisplaying);
		return courseList.get(index - 1);
	}
	
	private Course getNextCourse() {
		int index = courseList.indexOf(courseDisplaying);
		return courseList.get(index + 1);
	}

	public static void main(String[] args) {
		ArrayList<Course> courseList;
		try {
			courseList = Check.checkFromFile();
			new GUI(courseList);
		} catch (FileNotFoundException e) {
			FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
		    dialog.setMode(FileDialog.LOAD);
		    dialog.setFile("*.txt");
		    dialog.setVisible(true);
		    File file = new File(dialog.getDirectory() + dialog.getFile());
		    try {
				courseList = Check.checkFromFile(file, false);
				new GUI(courseList);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void displayInfo(Course course, JLabel title,
			JLabel courseInfo, DefaultTableModel model) {
		title.setText(course.getCourseId());

		int credits = course.getCredits();
		String genEd = course.getGenEd();
		String gradingMethod = course.getGradingMethod();

		if (genEd.equals("")) {
			genEd = "N/A";
		}

		courseInfo.setText(MessageFormat.format("<html>Credits: {0}<br/>GenEd: "
				+ "{1}<br/>Grading Methods: {2}</html>", credits, genEd, gradingMethod));

		String[][] sectionsInfo = course.getSectionsInfo();

		model.setRowCount(0);
		for (String[] sectionInfo:sectionsInfo) {
			model.addRow(sectionInfo);
		}
		
		courseDisplaying = course;
	}
}
