package com.secure.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import com.secure.dao.FileDAO;
import com.secure.model.FileModel;
import com.secure.utils.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        if (token == null || token.isEmpty()) {
            response.sendRedirect("dashboard.jsp?error=invalid_token");
            return;
        }

        FileDAO dao = new FileDAO();
        FileModel file = dao.getFileByToken(token);

        // 1. Check if file exists
        if (file == null) {
            response.getWriter().println("<h1>Error: File not found.</h1>");
            return;
        }

        // 2. Strict Expiry Check (Timestamp + Status)
        Timestamp now = new Timestamp(new Date().getTime());
        if ("expired".equals(file.getStatus()) || (file.getExpiryTime() != null && file.getExpiryTime().before(now))) {
            // Lazy update: Set to expired in DB if it isn't already
            if (!"expired".equals(file.getStatus())) {
                dao.updateFileStatus(token, "expired");
            }
            response.getWriter().println("<h1>Error: This link has expired.</h1>");
            return;
        }

        // 3. Generate OTP
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        request.getSession().setAttribute("currentOTP", otp);
        
        // IMPORTANT: Store token in session to verify against in the next step
        request.getSession().setAttribute("fileToken", token);

        // 4. Send Email
        String recipientEmail = file.getRecipientEmail();
        // Fallback to uploader if no recipient specified
        if (recipientEmail == null || recipientEmail.isEmpty()) {
             recipientEmail = file.getUploaderEmail(); 
        }
        
        if (recipientEmail != null) {
            EmailUtil.sendOTP(recipientEmail, otp);
        }

        // 5. Forward
        request.setAttribute("token", token);
        request.getRequestDispatcher("otp-verify.jsp").forward(request, response);
    }
}