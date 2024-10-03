package com.fzq.xiaopotato.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtils {

    public static String encryptPassword(String password, String salt) {
        try {
            // Create SHA-256 instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // combine pwd and salt
            String saltedPassword = salt + password;
            byte[] encodedhash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            // Convert result to Base64 String
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error occurred during password encryption", e);
        }
    }
}