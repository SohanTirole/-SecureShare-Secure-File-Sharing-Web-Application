<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login - SecureShare</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            height: 100vh;
            display: flex;
            align-items: center;
        }
        .login-card {
            border: none;
            border-radius: 15px;
        }
        .brand-logo {
            font-size: 2.5rem;
            color: #212529;
            font-weight: 800;
        }
        .input-group-text {
            background-color: transparent;
            border-right: none;
        }
        .form-control {
            border-left: none;
        }
        .form-control:focus {
            box-shadow: none;
            border-color: #dee2e6;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-5 col-lg-4">
                
                <!-- Brand Identity -->
                <div class="text-center mb-4">
                    <div class="brand-logo mb-2">🛡️</div>
                    <h2 class="fw-bold text-dark">Secure<span class="text-primary">Share</span></h2>
                   	<p class="text-muted">Encrypted. Private. Expiring.</p>
                </div>

                <div class="card login-card shadow-lg">
                    <div class="card-body p-4">
                        <h4 class="text-center mb-4 fw-bold">Login to Vault</h4>

                       <%--  <!-- Success message from registration -->
                        <c:if test="${param.msg == 'success'}">
                            <div class="alert alert-success py-2 small">
                                <i class="fas fa-check-circle me-1"></i> Registration successful! Please login.
                            </div>
                        </c:if> --%>

                        <!-- Error Handling -->
                        <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger py-2 small border-0">
                                <i class="fas fa-exclamation-triangle me-1"></i> <%= request.getAttribute("error") %>
                            </div>
                        <% } %>

                        <form action="LoginServlet" method="post">
                            <!-- Email Field -->
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Email Address</label>
                                <div class="input-group">
                                    <span class="input-group-text text-muted"><i class="fas fa-envelope"></i></span>
                                    <input type="email" name="email" class="form-control" placeholder="name@company.com" required>
                                </div>
                            </div>

                            <!-- Password Field -->
                            <div class="mb-4">
                                <label class="form-label small fw-bold">Password</label>
                                <div class="input-group">
                                    <span class="input-group-text text-muted"><i class="fas fa-lock"></i></span>
                                    <input type="password" name="password" class="form-control" placeholder="••••••••" required>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary w-100 py-2 fw-bold shadow-sm">
                                Sign In <i class="fas fa-sign-in-alt ms-2"></i>
                            </button>
                        </form>

                        <div class="mt-4 text-center">
                            <p class="small text-muted mb-0">Don't have an account?</p>
                            <a href="register.jsp" class="text-decoration-none fw-bold">Create Secure Account</a>
                        </div>
                    </div>
                </div>
                
 				<p class="text-center mt-4 text-muted small">
                    &copy; 2026 SecureShare. Military-grade AES-256 Encryption.
                </p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>