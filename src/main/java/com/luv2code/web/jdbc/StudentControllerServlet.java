package com.luv2code.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDbUtil studentDbUtil;
	@Resource(name = "jdbc/web_student_tracker") // name of the database
	private DataSource dataSource;

	@Override // THIS METHOD CALL BY THE JAVA EE SERVER BY TOMCAT WHEN THE SERVER IS FIRST
				// LOAD OR INICIALIZE;
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		// CREATE OUR STUDENT DB UTIL AND PASS IN THE CONNECTION POOL / DATASOURCE
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		try {
			// READ THE "COMMAND" PARAMETER
			String theCommand = request.getParameter("command");

			// IF THE COMMAND IS MISSING, THEN DEFAULT TO LISTING STUDENTS
			if (theCommand == null) {
				theCommand = "LIST";
			}

			// ROUTE TO THE APPROPRIATE METHOD
			switch (theCommand) {
			case "LIST":
				// LIST THE STUDENTS IN MVC FASHION
				listStudents(request, response);
				break;

			case "LOAD":
				loadStudent(request, response);
				break;

			case "UPDATE":
				updateStudent(request, response);
				break;

			case "DELETE":
				deleteStudent(request, response);
				break;

			default:
				listStudents(request, response);
			}

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			// READ THE "COMMAND" PARAMETER
			String theCommand = request.getParameter("command");

			// ROUTE TO THE APPROPRIATE METHOD
			switch (theCommand) {

			case "ADD":
				addStudent(request, response);
				break;

			default:
				listStudents(request, response);
			}

		}

		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// READ STUDENT ID FROM FORM DATA
		String theStudentId = request.getParameter("studentId");

		// DELETE STUDENT FROM DATABASE
		studentDbUtil.deleteStudent(theStudentId);

		// SENT THEM BACK TO "LIST STUDENTS" PAGE
		listStudents(request, response);
	}

	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// READ STUDENT INFO FROM FORM DATA
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");

		// CREATE A NEW STUDENT OBJECT
		Student theStudent = new Student(id, firstName, lastName, email);

		// PERFORM UPDATE ON DATABASE
		studentDbUtil.updateStudent(theStudent);

		// SET THEM TO THE LIST STUDENTS PAGE
		listStudents(request, response);

	}

	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// READ STUDENT ID FROM FORM DATA
		String theStudentId = request.getParameter("studentId");

		// GET STUDENT FROM DATABASE (DB UTIL)
		Student theStudent = studentDbUtil.getStudent(theStudentId);

		// PLACE STUDENT IN THE REQUEST ATTRIBUTE
		request.setAttribute("THE_STUDENT", theStudent);

		// SENDO TO JSP PAGE: UPDATE-STUDENT-FORM.JSP
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
	}

	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// READ STUDENT FROM DATA
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");

		// CREATE A NEW STUDENT OBJECT
		Student theStudent = new Student(firstName, lastName, email);

		// ADD THE STUDENT TO THE DATABASE
		studentDbUtil.addStudent(theStudent);

		// SEND BACK TO MAIN PAGE (THE STUDENT LIST)
		//SEND AS REDIRECT TO AVOID MULTIPLE--BROWSER RELOAD ISSUE
		response.sendRedirect(request.getContextPath() + "/StudentControllerServlet?command=LIST");
	}

	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// GET STUDENT FROM DB UTIL
		List<Student> students = studentDbUtil.getStudents();

		// ADD STUDENTS TO THE REQUEST
		request.setAttribute("STUDENT_LIST", students); // STUDENT_LIST = NAME | students = VALUE

		// SEND TO JSP PAGE (VIEW)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}
}
