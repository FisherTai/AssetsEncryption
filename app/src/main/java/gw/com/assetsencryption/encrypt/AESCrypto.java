package gw.com.assetsencryption.encrypt;


import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES加密工具
 */
public class AESCrypto {
    private static final String TAG = "AESCrypto";
    //PASSWORD，16位數
    public final static String PASSWORD_01 = "AAAAAAAAAAAAAAAA";

    public static String decrypt(String source, String password) {
        if (TextUtils.isEmpty(source) || password == null) {
            Log.e(TAG, "decrypt: String is null");
            return "";
        }
        try {
            byte[] raw = password.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hex2byte(source);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original);
        } catch (Exception ex) {
            Log.e(TAG, "decrypt: fail!",ex);
            return "";
        }
    }

    public static String encrypt(String source, String password) {
        if (source == null || password == null) {
            return "";
        }
        try {
            byte[] raw = password.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(source.getBytes());
            return byte2hex(encrypted).toLowerCase();
        } catch (Exception e) {
            Log.e(TAG, "encrypt: fail!",e);
            return "";
        }
    }

    private static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    private static String byte2hex(byte[] source) {
        StringBuilder sb = new StringBuilder();
        for (byte b : source) {
            String stmp = (Integer.toHexString(b & 0XFF));
            if (stmp.length() == 1) {
                sb.append("0").append(stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString().toUpperCase();
    }

}
