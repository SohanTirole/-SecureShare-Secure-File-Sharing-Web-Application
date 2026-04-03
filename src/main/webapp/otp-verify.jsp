<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <title>Verify Identity - SecureShare</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            height: 100vh;
            display: flex;
            align-items: center;
        }
        .otp-card {
            border: none;
            border-radius: 20px;
        }
        .otp-input {
            letter-spacing: 12px;
            font-size: 2rem;
            font-weight: bold;
            border-radius: 12px;
            border: 2px solid #dee2e6;
        }
        .otp-input:focus {
            border-color: #0d6efd;
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.1);
        }
        .icon-badge {
            width: 70px;
            height: 70px;
            background: #e7f1ff;
            color: #0d6efd;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            margin: 0 auto 20px;
            font-size: 1.8rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-5 col-lg-4 text-center">
                
                <!-- Brand Identity -->
                <div class="mb-4">
                    <h3 class="fw-bold text-dark">🛡️ Secure<span class="text-primary">Share</span></h3>
                </div>

                <div class="card otp-card shadow-lg">
                    <div class="card-body p-4 p-md-5">
                        
                        <!-- Security Icon -->
                        <div class="icon-badge">
                            <i class="fas fa-user-shield"></i>
                        </div>

                        <h4 class="fw-bold mb-2">Verify Identity</h4>
                        <p class="text-muted small mb-4">
                            We've sent a 6-digit verification code to your email. 
                            Please enter it below to unlock your file.
                        </p>

                        <!-- Error Feedback from VerifyOTPServlet -->
                        <c:if test="${not empty param.error}">
                            <div class="alert alert-danger py-2 small border-0 mb-4">
                                <i class="fas fa-times-circle me-1"></i> 
                                <c:choose>
                                    <c:when test="${param.error == 'InvalidOTP'}">Incorrect code. Please try again.</c:when>
                                    <c:otherwise>Verification failed. Try again.</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <form action="VerifyOTPServlet" method="post">
                            <!-- Token passed from DownloadServlet -->
                            <%-- <input type="hidden" name="token" value="${token}"> --%>
                            <input type="hidden" name="token" value="${not empty token ? token : param.token}">

                            <div class="mb-4">
                                <input type="text" name="otp" 
                                       class="form-control otp-input text-center" 
                                       placeholder="000000" 
                                       maxlength="6" 
                                       pattern="\d{6}"
                                       inputmode="numeric"
                                       autocomplete="one-time-code"
                                       required autofocus>
                            </div>

                            <button type="submit" class="btn btn-primary w-100 py-3 fw-bold shadow-sm mb-3">
                                <i class="fas fa-unlock-alt me-2"></i> Verify & Download
                            </button>
                        </form>

                        <div class="text-center mt-3">
                            <p class="text-muted small mb-0">Didn't receive the email?</p>
                            <a href="" class="text-decoration-none fw-bold small text-primary">Check Spam folder or Resend</a>
                        </div>
                    </div>
                </div>

                <p class="mt-4 text-muted small">
                    <i class="fas fa-info-circle me-1"></i> 
                    For security, this code will expire shortly.
                </p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>