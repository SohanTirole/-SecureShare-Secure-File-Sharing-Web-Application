package com.secure.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
    // CBC is secure, PKCS5Padding handles different file sizes
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public static void encryptFile(SecretKey key, File inputFile, File outputFile) throws Exception {
        // 1. Generate a random 16-byte Initialization Vector (IV)
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            
            // 2. Write the IV to the start of the file (unencrypted)
            fos.write(iv);
            
            // 3. Write Encrypted Data
            try (CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, read);
                }
            }
        }
    }

    public static void decryptStream(String encodedKey, InputStream encryptedStream, OutputStream out) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey key = new SecretKeySpec(decodedKey, KEY_ALGORITHM);

        // 1. Read the IV from the start of the stream
        byte[] iv = new byte[16];
        int ivBytesRead = encryptedStream.read(iv);
        
        if (ivBytesRead < 16) {
            throw new IOException("Invalid file format: Missing IV");
        }
        
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        // 2. Decrypt the rest of the stream
        try (CipherOutputStream cos = new CipherOutputStream(out, cipher)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = encryptedStream.read(buffer)) != -1) {
                cos.write(buffer, 0, read);
            }
        }
    }

    public static String calculateChecksum(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[8192];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

//public class EncryptionUtil {
//	private static final String ALGORITHM = "AES";
//
//	// GENERATE A ENCREPTED AES KEY BY WHICH WE CAN KEEP OUR FILE SECURE
//	public static SecretKey generateKey() throws Exception {
//		KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
//		keyGen.init(256);
//		return keyGen.generateKey();
//	}
//
//	public static void encryptFile(SecretKey key, File inputFile, File outputFile) throws Exception {
//		Cipher cipher = Cipher.getInstance(ALGORITHM);
//		cipher.init(Cipher.ENCRYPT_MODE, key);
//
//		try (FileInputStream fis = new FileInputStream(inputFile);
//				FileOutputStream fos = new FileOutputStream(outputFile);
//				CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
//			byte[] buffer = new byte[8192];
//			int read;
//			while ((read = fis.read(buffer)) != -1) {
//				cos.write(buffer, 0, read);
//			}
//		}
//	}
//
//	public static void decryptStream(String encodedKey, InputStream encryptedStream, OutputStream out)
//			throws Exception {
//		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
//		SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
//
//		Cipher cipher = Cipher.getInstance(ALGORITHM);
//		cipher.init(Cipher.DECRYPT_MODE, key);
//
//		byte[] buffer = new byte[8192];
//		int read;
//		// Using 
//		try (CipherOutputStream cos = new CipherOutputStream(out, cipher)) {
//			while ((read = encryptedStream.read(buffer)) != -1) {
//				cos.write(buffer, 0, read);
//			}
//		}
//	}
//	public static String calculateChecksum(File file) throws Exception {
//	    MessageDigest digest = MessageDigest.getInstance("SHA-256");
//	    try (FileInputStream fis = new FileInputStream(file)) {
//	        byte[] byteArray = new byte[8192];
//	        int bytesCount;
//	        while ((bytesCount = fis.read(byteArray)) != -1) {
//	            digest.update(byteArray, 0, bytesCount);
//	        }
//	    }
//	    byte[] bytes = digest.digest();
//	    StringBuilder sb = new StringBuilder();
//	    for (byte b : bytes) {
//	        sb.append(String.format("%02x", b));
//	    }
//	    return sb.toString();
//	}
//}