package com.secure.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
	// PASSWORD HASH CODE ME CONVERT KRREGA, THEN WE WILL SOTORE IS IN DATABASE BCrypt
	public static String hashPassword(String plainTextPassword) {
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}

	// CHECK THE PASSWORD IS VALID OR NOT WHEN WE TRY TO LOGIN
	public static boolean checkPassword(String plainTextPassword, String storedHash) {
		return BCrypt.checkpw(plainTextPassword, storedHash);
	}
}