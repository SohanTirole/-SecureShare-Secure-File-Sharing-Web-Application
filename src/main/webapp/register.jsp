<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<title>Create Account - SecureShare</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<style>
body {
	background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
	height: 100vh;
	display: flex;
	align-items: center;
}

.register-card {
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
			<div class="col-md-6 col-lg-5">

				<!-- Brand Identity -->
				<div class="text-center mb-4">
					<div class="brand-logo mb-1">🛡️</div>
					<h2 class="fw-bold text-dark">
						Secure<span class="text-primary">Share</span>
					</h2>
					 <p class="text-muted small">Join the community for secure,
						encrypted file sharing.</p> 
				</div>

				<div class="card register-card shadow-lg">
					<div class="card-body p-4 p-md-5">
						<h4 class="text-center mb-4 fw-bold">Create Your Account</h4>


						<c:choose>
							<c:when test="${param.msg == 'duplicate'}">
								<div class="alert alert-warning py-2 small border-0">
									<i class="fas fa-user-tag me-1"></i> This email is already
									registered. <a href="login.jsp">Login here</a>.
								</div>
							</c:when>
							<c:when test="${param.msg == 'error'}">
								<div class="alert alert-danger py-2 small border-0">
									<i class="fas fa-exclamation-circle me-1"></i> Registration
									failed. Please try again.
								</div>
							</c:when>
						</c:choose>


						<form action="RegisterServlet" method="post">
							<!-- Name Field -->
							<div class="mb-3">
								<label class="form-label small fw-bold">Full Name</label>
								<div class="input-group">
									<span class="input-group-text text-muted"><i
										class="fas fa-user"></i></span> <input type="text" name="name"
										class="form-control" placeholder="John Doe" required>
								</div>
							</div>

							<!-- Email Field -->
							<div class="mb-3">
								<label class="form-label small fw-bold">Email Address</label>
								<div class="input-group">
									<span class="input-group-text text-muted"><i
										class="fas fa-envelope"></i></span> <input type="email" name="email"
										class="form-control" placeholder="john@example.com" required
										pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
										title="Enter a valid email like john@example.com">
								</div>
							</div>

							<!-- Password Field -->
							<div class="mb-4">
								<label class="form-label small fw-bold">Password</label>
								<div class="input-group">
									<span class="input-group-text text-muted"><i
										class="fas fa-lock"></i></span> <input type="password"
										name="password" class="form-control" placeholder="••••••••"
										required pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$"
										title="Password must be at least 8 characters long, with letters and numbers">

								</div>
								  <div class="form-text small">Use a strong password to
									protect your files.</div> 
							</div>

							<button type="submit"
								class="btn btn-primary w-100 py-2 fw-bold shadow-sm">
								Create Account <i class="fas fa-user-plus ms-2"></i>
							</button>
						</form>

						<div class="mt-4 text-center">
							 <p class="small text-muted mb-0">Already have an account?</p>  
							<a href="login.jsp" class="text-decoration-none fw-bold">Login
								Here</a>
						</div>
					</div>
				</div>

				  <p class="text-center mt-4 text-muted small">By registering, you
					agree to our 256-bit encryption protocols.</p> 
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>