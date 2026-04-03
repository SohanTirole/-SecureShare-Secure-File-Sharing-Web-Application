<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.secure.dao.FileDAO"%>
<%@ page import="com.secure.model.FileModel"%>
<%@ page import="java.util.List"%>

<%
// SECURTIY CHECK | IF THE CONDITION WILL NOT GET EMAIL IN SESSION THEN IT WILL REDIRECT TO THE LOG IN PAGE
String email = (String) session.getAttribute("userEmail");
if (email == null) {
	response.sendRedirect("login.jsp");
	return;
}

// AFTER RE-CONFIRMING AUTH. USER | FileDao object FETCH THE PREVIOUS UPLOADED FILES WHICH IS STORED IN THE LIST
FileDAO dao = new FileDAO();
List<FileModel> fileList = dao.getFilesByUser(email);
request.setAttribute("fileList", fileList);
%>
<!DOCTYPE html>
<html>
<head>
<title>Dashboard - SecureShare</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<!-- CSS -->
<style>
.copy-btn {
	cursor: pointer;
	color: #0d6efd;
}

.copy-btn:hover {
	color: #0a58ca;
}

.table-card {
	border-radius: 15px;
	overflow: hidden;
}
</style>
</head>
<body class="bg-light">
	<!-- Navbar -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark px-4 shadow-sm">
		<div class="container-fluid">
			<a class="navbar-brand" href="dashboard.jsp">🛡️ <span
				class="fw-bold">Secure</span>Share
			</a>
			<div class="d-flex align-items-center">
				<span class="text-light me-3">Welcome, <strong
					class="text-info"><%=session.getAttribute("userName")%></strong></span> <a
					href="LogoutServlet" class="btn btn-outline-danger btn-sm">Logout</a>
			</div>
		</div>
	</nav>

	<div class="container mt-5">
		<!-- Display Success Message -->
		<c:if test="${not empty param.success}">
			<div
				class="alert alert-success alert-dismissible fade show shadow-sm border-0">
				<i class="fas fa-check-circle me-2"></i> File encrypted and
				uploaded! <strong>Token: ${param.token}</strong>
				<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
			</div>
		</c:if>

		<!-- Display Delete Success Message -->
		<c:if test="${not empty param.deleted}">
			<div
				class="alert alert-success alert-dismissible fade show shadow-sm border-0">
				<i class="fas fa-check-circle me-2"></i> File deleted successfully!
				<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
			</div>
		</c:if>

		<!-- Display Error Message -->
		<c:if test="${not empty param.error}">
			<div
				class="alert alert-danger alert-dismissible fade show shadow-sm border-0">
				<i class="fas fa-exclamation-circle me-2"></i>
				<c:choose>
					<c:when test="${param.error == 'invalid_token'}">Invalid token provided.</c:when>
					<c:when test="${param.error == 'file_not_found'}">File not found.</c:when>
					<c:when test="${param.error == 'delete_failed'}">Failed to delete file. Please try again.</c:when>
					<c:otherwise>An error occurred.</c:otherwise>
				</c:choose>
				<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
			</div>
		</c:if>

		<div class="row g-4">
			<!-- Upload Section -->
			<div class="col-lg-4">
				<div class="card shadow border-0 overflow-hidden">
					<div class="card-header bg-primary text-white py-3">
						<h5 class="mb-0">
							<i class="fas fa-shield-alt me-2"></i> Secure Upload
						</h5>
					</div>
					<div class="card-body p-4">
						<form action="upload" method="post" enctype="multipart/form-data">
							<div class="mb-3">
								<label class="form-label fw-bold">Select File</label> <input
									type="file" name="file" class="form-control" required>
							</div>

							<!-- Recipient Email Input -->
							<div class="mb-3">
								<label class="form-label fw-bold">Recipient Email (for
									OTP)</label> <input type="email" name="recipientEmail"
									class="form-control" placeholder="peter@gmail.com" required>
								<div class="form-text">OTP will be sent to this email for
									verification.</div>
							</div>

							<div class="row">
								<!-- Add Expiry Hrs -->
								<div class="col-6 mb-3">
									<label class="form-label fw-bold">Expiry (Hrs)</label> <input
										type="number" name="expiry" class="form-control" value="24"
										min="1">
								</div>
								<!-- Max Downloads -->
								<div class="col-6 mb-3">
									<label class="form-label fw-bold">Limit (DLs)</label> <input
										type="number" name="maxDownloads" class="form-control"
										value="1" min="1">
								</div>
							</div>

							<button type="submit"
								class="btn btn-primary w-100 py-2 fw-bold shadow-sm mt-2">
								<i class="fas fa-lock me-2"></i> Encrypt & Upload

							</button>
						</form>
					</div>
				</div>
			</div>

			<!-- Enhanced Files Table -->
			<div class="col-lg-8">
				<div class="d-flex justify-content-between align-items-center mb-3">
					<h4 class="mb-0 fw-bold">My Secure Files</h4>
					<span class="badge bg-secondary">${fileList.size()} Files
						Total</span>
				</div>

				<div class="card table-card shadow border-0">
					<div class="table-responsive">
						<table class="table table-hover align-middle mb-0">
							<thead class="table-light text-muted fw-bold">
								<tr>
									<th class="ps-4">File Name</th>
									<th>Recipient</th>
									<th>Downloads</th>
									<th>Status</th>
									<th class="pe-4 text-end">Action</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="file" items="${fileList}">
									<tr>
										<td class="ps-4">
											<div class="fw-bold text-dark">${file.originalName}</div>
											<div class="text-muted small">Expires:
												${file.expiryTime}</div>
										</td>
										<td><span class="text-muted"><i
												class="fas fa-envelope me-1 small"></i>
												${file.recipientEmail}</span></td>
										<td>
											<div class="progress" style="height: 6px; width: 80px;">
												<div class="progress-bar bg-info"
													style="width: ${(file.downloadCount / file.maxDownloads) * 100}%"></div>
											</div> <small class="text-muted">${file.downloadCount} /
												${file.maxDownloads}</small>
										</td>
										<td><c:choose>
												<c:when test="${file.status == 'active'}">
													<span
														class="badge bg-success-subtle text-success border border-success px-3">Active</span>
												</c:when>
												<c:otherwise>
													<span
														class="badge bg-danger-subtle text-danger border border-danger px-3">Expired</span>
												</c:otherwise>
											</c:choose></td>
										<td class="pe-4 text-end">
											<div class="btn-group">
												<button class="btn btn-sm btn-outline-secondary"
													onclick="copyLink('${file.token}')" title="Copy Link">
													<i class="fas fa-copy"></i>
												</button>
												<a href="download?token=${file.token}" target="_blank"
													class="btn btn-sm btn-outline-primary" title="Test Link">
													<i class="fas fa-external-link-alt"></i>
												</a>
												<button class="btn btn-sm btn-outline-danger"
													onclick="deleteFile('${file.token}', '${file.originalName}')"
													title="Delete File">
													<i class="fas fa-trash"></i>
												</button>
											</div>
										</td>
									</tr>
								</c:forEach>
								<c:if test="${empty fileList}">
									<tr>
										<td colspan="5" class="text-center py-5 text-muted"><i
											class="fas fa-folder-open fa-3x mb-3 d-block opacity-25"></i>
											No files uploaded yet.</td>
									</tr>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script>
        function copyLink(token) {
            const baseUrl = window.location.origin + window.location.pathname.replace('dashboard.jsp', '');
            const fullLink = baseUrl + 'download?token=' + token;
            
            navigator.clipboard.writeText(fullLink).then(() => {
                alert('Secure Link copied to clipboard!');
            }).catch(err => {
                console.error('Failed to copy link: ', err);
            });
        }
        
        function deleteFile(token, fileName) {
            if (confirm('Are you sure you want to delete "' + fileName + '"?\n\nThis action cannot be undone.')) {
                // Send delete request to server
                window.location.href = 'DeleteFileServlet?token=' + token;
            }
        }
    </script>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
