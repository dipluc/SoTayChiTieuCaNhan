package com.example.personalexpensemanager.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtils {
    public static String newSalt() {
        byte[] b = new byte[16];
        new SecureRandom().nextBytes(b);
        return toHex(b);
    }

    public static String hash(String rawPassword, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return toHex(md.digest((salt + rawPassword).getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte x : bytes) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString();
    }
}