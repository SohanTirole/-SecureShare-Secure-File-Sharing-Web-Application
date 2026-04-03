package com.secure.controller;

import java.io.File;
import java.io.IOException;

import com.secure.dao.FileDAO;
import com.secure.model.FileModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/DeleteFileServlet")
public class DeleteFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 1. Session Validation
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userEmail") == null) {
			response.sendRedirect("login.jsp");
			return;
		}
		String currentUserEmail = (String) session.getAttribute("userEmail");

		// 2. Token Validation
		String token = request.getParameter("token");

		if (token == null || token.trim().isEmpty()) {
			response.sendRedirect("dashboard.jsp?error=invalid_token");
			return;
		}

		FileDAO fileDAO = new FileDAO();

		try {
			// 3. Fetch File Details from Database
			FileModel fileModel = fileDAO.getFileByToken(token);

			if (fileModel == null) {
				response.sendRedirect("dashboard.jsp?error=file_not_found");
				return;
			}

			// 4. Ownership Verification (Security Check)
			// Ensure the user trying to delete is the one who uploaded it
			String uploaderEmail = fileModel.getUploaderEmail();

			if (uploaderEmail == null || !uploaderEmail.equals(currentUserEmail)) {
			    response.sendRedirect("dashboard.jsp?error=unauthorized");
			    return;
			}

			// 5. Delete Physical File
			// IMPORTANT: Must match the path used in UploadServlet
			String uploadDir = getServletContext().getInitParameter("file-upload-path");
			File physicalFile = new File(uploadDir + File.separator + fileModel.getStoredName());

			if (physicalFile.exists()) {
				boolean isDeleted = physicalFile.delete();
				if (!isDeleted) {
					System.err.println("Failed to delete physical file: " + physicalFile.getAbsolutePath());
					// We continue to delete from DB even if physical file fails,
					// or you can handle this as a specific error.
				}
			}

			// 6. Delete Record from Database
			boolean dbDeleted = fileDAO.deleteFile(token);

			if (dbDeleted) {
				response.sendRedirect("dashboard.jsp?deleted=success");
			} else {
				response.sendRedirect("dashboard.jsp?error=db_delete_failed");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("dashboard.jsp?error=delete_failed");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}