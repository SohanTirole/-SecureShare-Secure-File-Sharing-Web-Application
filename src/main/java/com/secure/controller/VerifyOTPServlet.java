package com.secure.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.secure.dao.FileDAO;
import com.secure.model.FileModel;
import com.secure.utils.EncryptionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/VerifyOTPServlet")
public class VerifyOTPServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String enteredOtp = request.getParameter("otp");
        String token = request.getParameter("token");
        
        String sessionOtp = (String) request.getSession().getAttribute("currentOTP");
        String sessionToken = (String) request.getSession().getAttribute("fileToken");

        // 1. Validate Token and OTP
        // We check if the token in the form matches the token in the session to prevent cross-tab errors
        if (sessionOtp == null || !sessionOtp.equals(enteredOtp) || !token.equals(sessionToken)) {
            response.sendRedirect("otp-verify.jsp?token=" + token + "&error=InvalidOTP");
            return;
        }

        FileDAO dao = new FileDAO();
        FileModel file = dao.getFileByToken(token);

        if (file == null) {
            response.sendRedirect("dashboard.jsp?error=file_not_found");
            return;
        }

        // 2. Logic: Increment Count & Check Max Downloads
        dao.incrementDownloadCount(token);
        if ((file.getDownloadCount() + 1) >= file.getMaxDownloads()) {
            dao.updateFileStatus(token, "expired");
        }

        // 3. Prepare File
        String uploadDir = getServletContext().getInitParameter("file-upload-path");
        File encryptedFile = new File(uploadDir + File.separator + file.getStoredName());

        if (!encryptedFile.exists()) {
            response.getWriter().println("Error: Physical file missing.");
            return;
        }

        // 4. Set Response Headers
        response.setContentType(file.getFileType() != null ? file.getFileType() : "application/octet-stream");
        // IMPORTANT: Tell browser the size of the DECRYPTED file (stored in DB)
        // If we don't do this, the browser won't show a progress bar ending or estimated time.
        response.setContentLengthLong(file.getFileSize()); 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getOriginalName() + "\"");

        // 5. Decrypt and Stream
        try (InputStream is = new FileInputStream(encryptedFile)) {
            // This calls our updated EncryptionUtil (which reads the IV first)
            EncryptionUtil.decryptStream(file.getAesKey(), is, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error decrypting file.");
        } finally {
            // Clean up session
            request.getSession().removeAttribute("currentOTP");
            request.getSession().removeAttribute("fileToken");
        }
    }
}


//@WebServlet("/VerifyOTPServlet")
//public class VerifyOTPServlet extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        
//        String enteredOtp = request.getParameter("otp");
//        String sessionOtp = (String) request.getSession().getAttribute("currentOTP");
//        String token = request.getParameter("token");
//
//        // OTP VERIFY
//        if (sessionOtp != null && sessionOtp.equals(enteredOtp)) {
//            FileDAO dao = new FileDAO();
//            FileModel file = dao.getFileByToken(token);
//
//            if (file == null) {
//                response.sendRedirect("otp-verify.jsp?token=" + token + "&error=FileNotFound");
//                return;
//            }
//
//            // INCREAMENT DOWNLOAD COUNT
//            dao.incrementDownloadCount(token);
//            
//            // UPDATE THE FILE IS DOWNLOADED OR NOT IN GIVEN LIMITS
//            // Note: file.getDownloadCount() is the count BEFORE this current download
//            if ((file.getDownloadCount() + 1) >= file.getMaxDownloads()) {
//                dao.updateFileStatus(token, "expired");
//            }
//
//            String uploadDir = getServletContext().getInitParameter("file-upload-path");
//            File encryptedFile = new File(uploadDir + File.separator + file.getStoredName());
//
//            if (!encryptedFile.exists()) {
//                response.sendRedirect("otp-verify.jsp?token=" + token + "&error=PhysicalFileNotFound");
//                return;
//            }
//
//            // RESPONSE HEADER
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getOriginalName() + "\"");
//
//            // 5. Decrypt and Stream (ONLY ONCE)
//            try (InputStream is = new FileInputStream(encryptedFile)) {
//                EncryptionUtil.decryptStream(file.getAesKey(), is, response.getOutputStream());
//                
//                // CLEAN THE CURRENT SESSION OTP
//                request.getSession().removeAttribute("currentOTP");
//                request.getSession().removeAttribute("fileToken");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            // OTP mismatch
//            response.sendRedirect("otp-verify.jsp?token=" + token + "&error=InvalidOTP");
//        }
//    }
//}