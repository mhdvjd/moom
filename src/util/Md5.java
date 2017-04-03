/*
 * This is ModemYar project developed by MV_MRP.
 * Copyright (c) 2017. All rights reserved.
 */
package util;

import java.security.MessageDigest;
import java.util.Base64;

/**
 *
 * @author mv
 */
public class Md5 {
    public static final String basicAuthorization(String user, String pass) {
        return Base64.getEncoder().encodeToString((user+":"+pass).getBytes());
    }
    
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
