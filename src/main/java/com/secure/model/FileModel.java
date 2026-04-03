package com.secure.model;

import java.sql.Timestamp;

public class FileModel {
	private String originalName;
	private String storedName;
	private String token;
	private String aesKey;
	private String status;
	private Timestamp expiryTime;

	// FIELDS FOR PROTECT OUR FILE, WITH AES SECURITY
	private String recipientEmail;
	private int maxDownloads;
	private int downloadCount;
	private String fileChecksum;
	
	private long fileSize;
    private String fileType;

	private String uploaderEmail;

	public String getUploaderEmail() {
		return uploaderEmail;
	}

	public void setUploaderEmail(String uploaderEmail) {
		this.uploaderEmail = uploaderEmail;
	}

	public FileModel() {
	}

	// Standard Getters and Setters

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getStoredName() {
		return storedName;
	}

	public void setStoredName(String storedName) {
		this.storedName = storedName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAesKey() {
		return aesKey;
	}

	public void setAesKey(String aesKey) {
		this.aesKey = aesKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Timestamp expiryTime) {
		this.expiryTime = expiryTime;
	}

	// New Feature Getters and SetterS

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public int getMaxDownloads() {
		return maxDownloads;
	}

	public void setMaxDownloads(int maxDownloads) {
		this.maxDownloads = maxDownloads;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public String getFileChecksum() {
		return fileChecksum;
	}

	public void setFileChecksum(String fileChecksum) {
		this.fileChecksum = fileChecksum;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}