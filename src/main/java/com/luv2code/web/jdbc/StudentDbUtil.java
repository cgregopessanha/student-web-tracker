package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.sql.DataSource;

public class StudentDbUtil {

	private static DataSource dataSource;

	public StudentDbUtil(DataSource theDataSource) {
		dataSource = theDataSource;
	}

	public List<Student> getStudents() throws Exception {

		List<Student> students = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;

		try {
			// GET A CONNECTION
			myConn = dataSource.getConnection();

			// CREATE SQL STATEMENT
			String sql = "select * from student order by last_name";
			myStmt = myConn.createStatement();

			// EXECUTE QUERY
			myRs = myStmt.executeQuery(sql);

			// PROCESS RESULT SET

			while (myRs.next()) {

				// RETRIEVE DATA FROM RESULT SET ROW
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");

				// CREATE A NEW STUDENT OBJECT
				Student tempStudent = new Student(id, firstName, lastName, email);

				// ADD IT TO THE LIST OF STUDENTS
				students.add(tempStudent);
			}

			return students;

		} finally {
			// CLOSE JDBC OBJECTS
			close(myConn, myStmt, myRs);
		}

	}

	private static void close(Connection myConn, Statement myStmt, ResultSet myRs) {

		try {
			if (myRs != null) {
				myRs.close();
			}

			if (myStmt != null) {
				myStmt.close();
			}

			if (myConn != null) {
				myConn.close(); // DOES NOT REALLY CLOSE. JUST PUTS BACK IN CONNECTION POOL.
			}
		} catch (Exception e) {
			System.out.println("Can not close connection!");
			e.printStackTrace();
		}
	}

	public static void addStudent(Student theStudent) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// GET DATABASE CONNECTION
			myConn = dataSource.getConnection();

			// CREATE SQL FOR INSERT
			String sql = "insert into student" + "(first_name, last_name, email)" + "values (?,?,?)";

			myStmt = myConn.prepareStatement(sql);

			// SET THE PARAM VALUES FOR THE STUDENT
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());

			// EXECUTE SQL INSERT
			myStmt.execute();

		} catch (Exception e) {
			System.out.println("Não esta executando a querry!");
			e.printStackTrace();
		}

		finally {

			// CLEAN UP JDBC OBJECTS
			close(myConn, myStmt, null);
		}
	}

	public Student getStudent(String theStudentId) throws Exception {

		Student theStudent = null;

		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int studentId;

		try {

			// CONVERT STUDENT ID TO INT
			studentId = Integer.parseInt(theStudentId);

			// GET CONNECTION TO DATABASE
			myConn = dataSource.getConnection();

			// CREATE SQL TO GET SELECTED STUDENT
			String sql = "select * from student where id=?";

			// CREATE PREPARED STATEMENT
			myStmt = myConn.prepareStatement(sql);

			// SET PARAMS
			myStmt.setInt(1, studentId);

			// EXECUTE STATEMENT
			myRs = myStmt.executeQuery();

			// RETRIEVE DATA FROM RESULT SET ROW
			if (myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");

				// USE THE STUDENTID DURING CONSTRUCTION
				theStudent = new Student(studentId, firstName, lastName, email);

			} else {
				throw new Exception("Could not find student id " + studentId);
			}

			return theStudent;
		} finally {
			// CLEAN JDBC OBJECTS
			close(myConn, myStmt, myRs);
		}

	}

	public void updateStudent(Student theStudent) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// GET DB CONNECTION
			myConn = dataSource.getConnection();

			// CREATE SQL UPDATE STATEMENT
			String sql = "update student " 
					+ "set first_name=?, last_name=?, email=? " 
					+ "where id=?";

			// PREPARE STATEMENT
			myStmt = myConn.prepareStatement(sql);

			// SET PARAMS
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			
			// EXECUTE SQL STATEMENT
			myStmt.execute();

		} finally {
			// CLEAN UP JDBC OBJECTS
			close(myConn, myStmt, null);
		}
	}

	public void deleteStudent(String theStudentId) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			//CONVERT STUDENTID TO INT
			int studentId = Integer.parseInt(theStudentId);
			
			//GET DATABASE CONNECTION
			myConn = dataSource.getConnection();
			
			//CREATE SQL STATEMENT 
			String sql = "delete from student where id=?";
			
			//PREPARE STATEMENT
			myStmt = myConn.prepareStatement(sql);
			
			//SET PARAMS
			myStmt.setInt(1, studentId);
			
			//EXECUTE SQL
			myStmt.execute();
		}
		finally {
			//CLOSE JDBC OBJECTS
			close(myConn, myStmt, null);
		}
		
		
		
		
		
	}
}
