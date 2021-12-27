package com.luv2code.web.jdbc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//STEP 1: SET UP THE PRINTWRITER:
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		
		//STEP 2: GET A CONNECTION TO THE DATABASE:
		Connection myConn = null;
		java.sql.Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = dataSource.getConnection();
			
			//STEP 3: CREATE A SQL STATEMENT:
				String sql = "select * from student";
				myStmt = myConn.createStatement();
				
						
			//STEP 4: EXECUTE A SQL QUERY:
			
				myRs = myStmt.executeQuery(sql);
			
			//STEP 5: PROCESS THE RESULTS:
			while(myRs.next()) {
				String email = myRs.getString("email");
				out.println(email);
			}
			
			request.setAttribute("emailList", myRs);
			RequestDispatcher dispatcher = request.getRequestDispatcher("view-list-sql.jsp");
			dispatcher.forward(request, response);
			
		} catch (Exception e) {
			System.out.println("NÃO CONSEGUIU CONECTAR COM O BANCO!!");
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
