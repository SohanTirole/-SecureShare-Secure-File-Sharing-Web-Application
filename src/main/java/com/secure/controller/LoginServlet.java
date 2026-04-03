package com.secure.controller;

import java.io.IOException;

import com.secure.dao.UserDAO;
import com.secure.model.User;
import com.secure.utils.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String pass = request.getParameter("password");
		
		
		UserDAO dao = new UserDAO();
		User user = dao.getUserByEmail(email);//	GETTING THE REGISTERDE USER FROM THE DATABASE
		
		//	IF THE CREDANTIAL IS MATCH THEN THE SESSION START
		if (user != null && PasswordUtil.checkPassword(pass, user.getPasswordHash())) {
			HttpSession session = request.getSession();
			session.setAttribute("userEmail", email);
			session.setAttribute("userName", user.getName());
			response.sendRedirect("dashboard.jsp"); // FROM HERE USER GOSE TO DASHBOARD
		} else {
			request.setAttribute("error", "Invalid email or password");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}
}