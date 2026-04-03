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

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		UserDAO dao = new UserDAO();
		
		if (dao.getUserByEmail(email) != null) {
			//	SHOW THE DUPLICATE EMAIL MESSAGE IF EMAIL IS PRESENT IN DATABASE
			response.sendRedirect("register.jsp?msg=duplicate");
			return;
		}

		// IF EMAIL IS NOT PRESENT IN DATABASE THEN WE CREATE THE OBJECT OF INDIVIDUAL USER
		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPasswordHash(PasswordUtil.hashPassword(password));// STORING THE PASSWORD IN HASH CODE 

		//	THEN IT WILL SEND TO registerUser() IF THE USER REGISTERDED SUCESSFULLY THEN PAGE REDIRCT TO LOGIN
		try {
			if (dao.registerUser(user)) {
				response.sendRedirect("login.jsp?msg=success");
			} else {
				response.sendRedirect("register.jsp?msg=error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("register.jsp?msg=error");
		}
	}
}