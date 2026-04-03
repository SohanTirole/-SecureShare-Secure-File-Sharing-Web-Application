package com.secure.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.secure.model.FileModel;
import com.secure.utils.DBConnection;

public class FileDAO {

	public void incrementDownloadCount(String token) {
		String sql = "UPDATE files SET download_count = download_count + 1 WHERE token = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, token);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateFileStatus(String token, String status) {
		String sql = "UPDATE files SET status = ? WHERE token = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setString(2, token);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean saveFileMetadata(FileModel file) {
		try (Connection conn = DBConnection.getConnection()) {
			String sql = "INSERT INTO files (stored_name, original_name, token, aes_key_encrypted, expiry_time, status) VALUES (?,?,?,?,?, 'active')";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, file.getStoredName());
			ps.setString(2, file.getOriginalName());
			ps.setString(3, file.getToken());
			ps.setString(4, file.getAesKey());
			ps.setTimestamp(5, file.getExpiryTime());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteFile(String token) {
		String query = "DELETE FROM files WHERE token = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, token);
			int rowsAffected = pstmt.executeUpdate();

			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

 

	public FileModel getFileByToken(String token) {
		String sql = "SELECT * FROM files WHERE token = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, token);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				FileModel file = new FileModel();
				file.setToken(rs.getString("token"));
				file.setStoredName(rs.getString("stored_name"));
				file.setOriginalName(rs.getString("original_name"));
				file.setUploaderEmail(rs.getString("uploader_email"));
				file.setRecipientEmail(rs.getString("recipient_email"));
				file.setAesKey(rs.getString("aes_key_encrypted"));
				file.setStatus(rs.getString("status"));
				file.setExpiryTime(rs.getTimestamp("expiry_time"));
				file.setMaxDownloads(rs.getInt("max_downloads"));
				file.setDownloadCount(rs.getInt("download_count"));

				// --- ADD THESE LINES ---
				file.setFileSize(rs.getLong("file_size"));
				file.setFileType(rs.getString("file_type"));
				// -----------------------

				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<FileModel> getFilesByUser(String email) {
		String sql = "SELECT * FROM files WHERE uploader_email = ? ORDER BY upload_time DESC";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			List<FileModel> files = new ArrayList<>();
			while (rs.next()) {
				FileModel f = new FileModel();
				f.setOriginalName(rs.getString("original_name"));
				f.setExpiryTime(rs.getTimestamp("expiry_time"));
				f.setStatus(rs.getString("status"));
				f.setToken(rs.getString("token"));
				f.setRecipientEmail(rs.getString("recipient_email"));
				f.setMaxDownloads(rs.getInt("max_downloads"));
				f.setDownloadCount(rs.getInt("download_count"));
				files.add(f);
			}
			return files;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}