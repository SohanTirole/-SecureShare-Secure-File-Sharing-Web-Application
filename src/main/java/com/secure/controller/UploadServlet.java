package com.secure.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.UUID;
import java.util.Calendar;

import javax.crypto.SecretKey;

import com.secure.utils.EncryptionUtil;
import com.secure.utils.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 50, // 50MB
		maxRequestSize = 1024 * 1024 * 100) // 100MB
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userEmail") == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		// INPUT DETAIL'S FOR FILE AND USER EMAIL
		String uploaderEmail = (String) session.getAttribute("userEmail");
		Part filePart = request.getPart("file");
		String originalName = filePart.getSubmittedFileName();
		String fileType = filePart.getContentType();
		long fileSize = filePart.getSize();

		String recipientEmail = request.getParameter("recipientEmail");
		String maxDownloadsStr = request.getParameter("maxDownloads");
		int maxDownloads = (maxDownloadsStr != null && !maxDownloadsStr.isEmpty()) ? Integer.parseInt(maxDownloadsStr)
				: 1;

		String expiryStr = request.getParameter("expiry");
		int expiryHours = (expiryStr != null && !expiryStr.isEmpty()) ? Integer.parseInt(expiryStr) : 24;

		// FILE PATH SET-UP
		String storedName = UUID.randomUUID().toString() + ".enc";
		String uploadDir = getServletContext().getInitParameter("file-upload-path");

		File uploadFolder = new File(uploadDir);
		if (!uploadFolder.exists())
			uploadFolder.mkdirs();

		File tempFile = new File(uploadDir + File.separator + "temp_" + originalName);
		File encryptedFile = new File(uploadDir + File.separator + storedName);

		try {
			// SAVE THE TEMPRARY FILE AND GENERATE CHECKSUM
			filePart.write(tempFile.getAbsolutePath());
			String checksum = EncryptionUtil.calculateChecksum(tempFile);  

			// GENERATE AES KEY & Encrypt
			SecretKey fileKey = EncryptionUtil.generateKey();
			EncryptionUtil.encryptFile(fileKey, tempFile, encryptedFile);

			// META DATA
			String token = UUID.randomUUID().toString();
			String encodedKey = Base64.getEncoder().encodeToString(fileKey.getEncoded());

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, expiryHours);
			Timestamp expiryTimestamp = new Timestamp(cal.getTimeInMillis());

			// STORING FILE DETAILS WITH SECURITY KEY AND MAX DOWNLOAD
			boolean isSaved = saveToDatabase(originalName, storedName, token, encodedKey, fileSize, fileType,
					uploaderEmail, recipientEmail, expiryTimestamp, maxDownloads, checksum);

			tempFile.delete();

			if (isSaved) {
				response.sendRedirect("dashboard.jsp?success=1&token=" + token);
			} else {
				response.sendRedirect("dashboard.jsp?error=db_error");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("dashboard.jsp?error=upload_failed");
		}
	}

	// UPDATED: NOW ACCEPTS RECIPIENTS, MAXDOWNLOAD, CHECKSUM
	private boolean saveToDatabase(String originalName, String storedName, String token, String encodedKey,
			long fileSize, String fileType, String uploaderEmail, String recipientEmail, Timestamp expiryTime,
			int maxDownloads, String checksum) {

		String sql = "INSERT INTO files (stored_name, original_name, uploader_email, recipient_email, expiry_time, "
				+ "token, aes_key_encrypted, file_size, file_type, max_downloads, file_checksum, status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'active')";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, storedName);
			ps.setString(2, originalName);
			ps.setString(3, uploaderEmail);
			ps.setString(4, recipientEmail);
			ps.setTimestamp(5, expiryTime);
			ps.setString(6, token);
			ps.setString(7, encodedKey);
			ps.setLong(8, fileSize);
			ps.setString(9, fileType);
			ps.setInt(10, maxDownloads);
			ps.setString(11, checksum);

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}