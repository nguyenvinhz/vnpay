package com.vnpay.common;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

/**
 * File cấu hình chuẩn của VNPay cung cấp cho Java (Servlet / JSP)
 */
public final class Config {
    public static final String vnp_PayUrl = getSetting(
            "VNPAY_PAY_URL", "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
    public static final String vnp_ReturnUrl = getSetting(
            "VNPAY_RETURN_URL", getDefaultReturnUrl());
    public static final String vnp_TmnCode = getSetting("VNPAY_TMN_CODE", "2QXUI4J4");
    public static final String secretKey = getSetting(
            "VNPAY_HASH_SECRET", "RAOCTPBNKSTPXAGDDXQZPVXISJYVXXZP");
    public static final String vnp_HashSecret = secretKey;
    public static final String vnp_ApiUrl = getSetting(
            "VNPAY_API_URL", "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction");
    public static final TimeZone VNPAY_TIME_ZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");

    private static final SecureRandom RANDOM = new SecureRandom();

    private Config() {
    }

    private static String getSetting(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(name);
        }
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static String getDefaultReturnUrl() {
        String renderHostname = System.getenv("RENDER_EXTERNAL_HOSTNAME");
        if (renderHostname != null && !renderHostname.trim().isEmpty()) {
            return "https://" + renderHostname.trim() + "/vnpay_return.jsp";
        }
        return "http://localhost:8080/vnpay/vnpay_return.jsp";
    }

    public static String md5(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    public static String Sha256(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    // Băm tất cả các trường theo chuẩn HMAC-SHA512 của VNPay
    public static String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                if (!first) {
                    sb.append('&');
                }
                sb.append(fieldName);
                sb.append('=');
                sb.append(fieldValue);
                first = false;
            }
        }
        return hmacSHA512(secretKey, sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && !ipAddress.trim().isEmpty()) {
            int separator = ipAddress.indexOf(',');
            if (separator >= 0) {
                ipAddress = ipAddress.substring(0, separator);
            }
            return ipAddress.trim();
        }
        return request.getRemoteAddr();
    }

    public static String getRandomNumber(int len) {
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
