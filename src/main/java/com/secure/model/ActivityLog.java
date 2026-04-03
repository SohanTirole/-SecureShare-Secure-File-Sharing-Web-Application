package com.secure.model;

import java.sql.Timestamp;

public class ActivityLog {
	public int fileId;
	public String email, action, ipAddress, status;
	public Timestamp timestamp;
}