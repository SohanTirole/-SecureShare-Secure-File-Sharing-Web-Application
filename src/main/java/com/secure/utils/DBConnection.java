package com.secure.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {
	
    public static Connection getConnection() {
    	//createDatabaseIfNotExists();
    	Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con =  DriverManager.getConnection("jdbc:mysql://localhost:3306/secure_share", "root", "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
    

	public static void createDatabaseIfNotExists() {
		String DRIVER = "com.mysql.cj.jdbc.Driver";
		String URL = "jdbc:mysql://localhost:3306/";
		String USER = "root";
		String PASS = "root";
		String DATABASE = "secure_share";
		try {
			Class.forName(DRIVER);
			Connection con = DriverManager.getConnection(URL, USER, PASS);
			if(con!=null) {
				String query = "CREATE DATABASE IF NOT EXISTS "+DATABASE;
				Statement stmt = con.createStatement();
				stmt.execute(query);
				//System.out.println("Database Created Sucessfully!!");
				
				Statement stmt0 = con.createStatement();
				stmt0.execute("USE "+DATABASE);
				
				String query1 = "CREATE TABLE IF NOT EXISTS users( id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100), email VARCHAR(100) UNIQUE NOT NULL, password_hash VARCHAR(255) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
				Statement stmt1 = con.createStatement();
				stmt1.execute(query1);
				
				String query2 ="CREATE TABLE IF NOT EXISTS files(file_id INT PRIMARY KEY AUTO_INCREMENT, stored_name VARCHAR(255), original_name VARCHAR(255), uploader_email VARCHAR(100), upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, expiry_time TIMESTAMP, token VARCHAR(100) UNIQUE, aes_key_encrypted TEXT, file_size LONG, file_type VARCHAR(255), status ENUM('active', 'expired', 'deleted') DEFAULT 'active', recipient_email VARCHAR(100), max_downloads INT DEFAULT 1, download_count INT DEFAULT 0, file_checksum VARCHAR(64))";
				Statement stmt2 = con.createStatement();
				stmt2.execute(query2);
				
				String query3 ="CREATE TABLE IF NOT EXISTS otp_verification( otp_id INT PRIMARY KEY AUTO_INCREMENT, email VARCHAR(100), otp_hash VARCHAR(255), token VARCHAR(100), expiry_time TIMESTAMP, attempts INT DEFAULT 0)";
				Statement stmt3 = con.createStatement();
				stmt3.execute(query3);
				
				String query4 ="CREATE TABLE IF NOT EXISTS activity_logs (log_id INT PRIMARY KEY AUTO_INCREMENT, file_id INT,email VARCHAR(100),action VARCHAR(50),timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,ip_address VARCHAR(45),status VARCHAR(20))";
				Statement stmt4 = con.createStatement();
				stmt4.execute(query4);
				
				System.out.println("Tables Created Successfully!!");
			}else {
				System.out.println("Something Went Wrong..!!");
			}
		}catch(Exception e) {
			System.out.println("Exception : "+e);
		}
	}
}