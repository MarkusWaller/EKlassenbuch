package de.sap_project.e_klassenbuch.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Security Utility.
 * <p/>
 * Created by Markus on 03.05.2015.
 */

public class SecureUtil {
    // Static object of this class -> singleton pattern.
    private static final SecureUtil SECURE_UTIL = new SecureUtil();

    private static final char[] hexadecimal = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Private constructor -> singleton pattern.
     */
    private SecureUtil() {
    }

    /**
     * Gets the instance of this class -> singleton pattern.
     *
     * @return The SecureUtil instance
     */
    public static SecureUtil getInstance() {
        return SECURE_UTIL;
    }

    /**
     * Calculates the MD5 Hash-Code for the given password string.
     *
     * @param password The password string
     * @return The MD5 Hash-Code
     */
    public String getPasswordHash(String password) {
        String passwordHash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            passwordHash = encode(md.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordHash;
    }

    /**
     * Encodes the 128 bit (16 bytes) MD5 into a 32 character String.
     *
     * @param binaryData Array containing the digest
     * @return Encoded MD5, or null if encoding failed
     */
    public String encode(byte[] binaryData) {

        if (binaryData.length != 16)
            return null;

        char[] buffer = new char[32];

        for (int i = 0; i < 16; i++) {
            int low = binaryData[i] & 0x0f;
            int high = (binaryData[i] & 0xf0) >> 4;
            buffer[i * 2] = hexadecimal[high];
            buffer[i * 2 + 1] = hexadecimal[low];
        }

        return new String(buffer);
    }
}
