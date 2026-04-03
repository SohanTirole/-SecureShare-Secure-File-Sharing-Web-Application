package com.secure.dao;

import com.secure.model.User;
import com.secure.utils.DBConnection;
import java.sql.*;

public class UserDAO {
	public boolean registerUser(User user) {
	    String query = "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)";
	    try (Connection conn = DBConnection.getConnection(); 
	         PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setString(1, user.getName());
	        ps.setString(2, user.getEmail());
	        ps.setString(3, user.getPasswordHash());
	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        if (e.getErrorCode() == 1062) { // MySQL code for Duplicate Entry
	            System.out.println("User already exists!");
	        } else {
	            e.printStackTrace();
	        }
	        return false;
	    }
	}

	public User getUserByEmail(String email) {
		String sql = "SELECT * FROM users WHERE email = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				User user = new User();
				user.setEmail(rs.getString("email"));
				user.setPasswordHash(rs.getString("password_hash"));
				user.setName(rs.getString("name"));
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}