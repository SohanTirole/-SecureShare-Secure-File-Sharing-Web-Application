# -SecureShare-Secure-File-Sharing-Web-Application
SecureShare is a Java web app for encrypted file sharing. Files are AES-256 encrypted on upload, shared via unique tokens, and protected by email OTP verification before download. Features include expiry time, download limits, checksum integri# 🛡️ SecureShare — Secure File Sharing Web Application

A Java-based web application for securely uploading, sharing, and downloading files with end-to-end AES encryption, OTP-based download verification, expiry controls, and download limits.

---

## 📌 Features

- **User Authentication** — Register and log in with bcrypt-hashed passwords
- **AES-256 File Encryption** — Every uploaded file is encrypted at rest using a unique AES key per file
- **Token-Based Sharing** — Each file gets a unique, shareable download token (UUID)
- **OTP Verification** — Recipients must verify a one-time password sent via email before downloading
- **Expiry Control** — Files can be set to expire after a configurable number of hours
- **Download Limits** — Restrict how many times a file can be downloaded; status auto-expires after the limit is hit
- **File Integrity Check** — SHA-256 checksum is computed on upload to detect tampering
- **Ownership-Enforced Deletion** — Only the uploader can delete their own files
- **Automatic Cleanup** — Expired files are cleaned up by a background listener
- **Responsive Dashboard** — View, copy share links, and manage all uploaded files from one place

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Web Framework | Jakarta EE (Servlets + JSP) |
| Frontend | Bootstrap 5, Font Awesome |
| Database | MySQL 8 |
| Encryption | AES (javax.crypto) |
| Email | JavaMail API (Gmail SMTP) |
| Server | Apache Tomcat 10+ |
| Build | Manual / Maven (WAR deployment) |

---

## 📁 Project Structure

```
SecureShare/
├── src/
│   └── com/secure/
│       ├── controller/
│       │   ├── LoginServlet.java
│       │   ├── RegisterServlet.java
│       │   ├── LogoutServlet.java
│       │   ├── UploadServlet.java
│       │   ├── DownloadServlet.java
│       │   ├── VerifyOTPServlet.java
│       │   └── DeleteFileServlet.java
│       ├── dao/
│       │   ├── UserDAO.java
│       │   └── FileDAO.java
│       ├── model/
│       │   ├── User.java
│       │   └── FileModel.java
│       └── utils/
│           ├── DBConnection.java
│           ├── EncryptionUtil.java
│           ├── PasswordUtil.java
│           ├── EmailUtil.java
│           └── ActivityLog.java
├── WebContent/
│   ├── login.jsp
│   ├── register.jsp
│   ├── dashboard.jsp
│   ├── otp-verify.jsp
│   └── WEB-INF/
│       └── web.xml
└── README.md
```

---

## 🗄️ Database Schema

The application uses a MySQL database named `secure_share`. The schema is auto-created on first run via `DBConnection.createDatabaseIfNotExists()`.

### `users`
| Column | Type | Notes |
|---|---|---|
| id | INT (PK, AI) | |
| name | VARCHAR(100) | |
| email | VARCHAR(100) | UNIQUE |
| password_hash | VARCHAR(255) | bcrypt hashed |
| created_at | TIMESTAMP | |

### `files`
| Column | Type | Notes |
|---|---|---|
| file_id | INT (PK, AI) | |
| stored_name | VARCHAR(255) | UUID-based `.enc` filename |
| original_name | VARCHAR(255) | |
| uploader_email | VARCHAR(100) | FK → users |
| recipient_email | VARCHAR(100) | OTP is sent here |
| upload_time | TIMESTAMP | |
| expiry_time | TIMESTAMP | |
| token | VARCHAR(100) | UNIQUE share token |
| aes_key_encrypted | TEXT | Base64-encoded AES key |
| file_size | LONG | |
| file_type | VARCHAR(255) | MIME type |
| status | ENUM | `active`, `expired`, `deleted` |
| max_downloads | INT | Default: 1 |
| download_count | INT | Default: 0 |
| file_checksum | VARCHAR(64) | SHA-256 |

---

## ⚙️ Setup & Installation

### Prerequisites

- Java 17+
- Apache Tomcat 10+
- MySQL 8+
- JavaMail API JAR
- An IDE such as Eclipse (with Dynamic Web Project support)

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/SecureShare.git
cd SecureShare
```

### 2. Configure the Database

Create the MySQL database and user:

```sql
CREATE DATABASE secure_share;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON secure_share.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

> The tables will be created automatically on first startup. Alternatively, you can manually trigger `DBConnection.createDatabaseIfNotExists()`.

Update the credentials in `DBConnection.java` if you use a different username or password:

```java
con = DriverManager.getConnection("jdbc:mysql://localhost:3306/secure_share", "root", "root");
```

### 3. Configure File Storage Path

In `web.xml`, set the file upload directory:

```xml
<context-param>
    <param-name>file-upload-path</param-name>
    <param-value>/path/to/your/secure-uploads</param-value>
</context-param>
```

Make sure this directory exists and is writable by Tomcat, and that it is **outside** the web root so uploaded files are not directly accessible via URL.

### 4. Configure Email (Gmail SMTP)

Open `EmailUtil.java` and replace the credentials with your own:

```java
final String username = "your-email@gmail.com";
final String password = "your-app-password"; // Gmail App Password, not your account password
```

> ⚠️ **Important:** Never commit real credentials to a public repository. Use environment variables or a config file listed in `.gitignore` instead.

To generate a Gmail App Password: Google Account → Security → 2-Step Verification → App Passwords.

### 5. Deploy to Tomcat

- Export the project as a `.war` file from your IDE.
- Drop the `.war` into Tomcat's `webapps/` directory.
- Start Tomcat: `./bin/startup.sh` (Linux/macOS) or `startup.bat` (Windows).
- Visit: `http://localhost:8080/SecureShare/login.jsp`

---

## 🔄 Application Flow

```
Register / Login
      │
      ▼
  Dashboard
      │
  ┌───┴────────────────────┐
  │                        │
Upload File            View My Files
  │                        │
  ├─ AES key generated     ├─ Copy share link
  ├─ File encrypted        ├─ Delete file
  ├─ Checksum computed     └─ See download stats
  ├─ Token + metadata saved
  └─ Redirect to dashboard
      
Share Token with Recipient
      │
      ▼
Recipient opens download link → OTP sent to email
      │
      ▼
Recipient enters OTP → File decrypted & streamed
      │
      ▼
Download count incremented → Auto-expire if limit reached
```

---

## 🔐 Security Design

| Threat | Mitigation |
|---|---|
| Brute-force login | bcrypt password hashing via `PasswordUtil` |
| Unauthorized file access | Session validation on every protected servlet |
| Insecure direct object reference | Files accessed by random UUID token, never by filename or ID |
| Unauthorized deletion | Ownership check — uploader email must match session email |
| File tampering | SHA-256 checksum computed on upload |
| Plaintext file storage | AES-256 encryption; original file is deleted after encryption |
| Link sharing without identity | OTP sent to recipient email before any download is served |
| Stale link reuse | Expiry timestamp + max download count; lazy status update on access |
| Cross-tab OTP misuse | Session stores both `currentOTP` and `fileToken`; both are validated together |

---

## 📸 Screenshots

> Add screenshots of your Login, Register, Dashboard, and OTP verification pages here.

---

## 🚧 Known Limitations & Future Improvements

- Email credentials are currently hardcoded — should be moved to environment variables or a `config.properties` file
- No rate limiting on login or OTP attempts
- OTP has no server-side expiry timer (only the link expiry is enforced)
- No HTTPS enforcement (should be handled at the Tomcat/reverse-proxy level in production)
- File preview is not supported — files are always streamed as downloads

---

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).ty checks, and ownership-based deletion.
