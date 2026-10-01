package com.vnpay.common;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet thực hiện API hoàn tiền giao dịch (refund) chuẩn của VNPay
 */
@WebServlet(name = "vnpayRefund", urlPatterns = {"/vnpayrefund", "/vnpayRefund"})
public class vnpayRefund extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("vnpay_refund.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String vnp_RequestId = Config.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = Config.vnp_TmnCode;
        String vnp_TransactionType = req.getParameter("trantype");
        String vnp_TxnRef = req.getParameter("order_id");
        
        long amount = 0;
        try {
            amount = Math.multiplyExact(Long.parseLong(req.getParameter("amount")), 100L);
        } catch (Exception ignored) {
            amount = 0;
        }

        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = req.getParameter("trans_no");
        if (vnp_TransactionNo == null) {
            vnp_TransactionNo = "";
        }
        String vnp_TransactionDate = req.getParameter("trans_date");
        String vnp_CreateBy = req.getParameter("user");
        if (vnp_CreateBy == null || vnp_CreateBy.isEmpty()) {
            vnp_CreateBy = "admin";
        }

        if (!("02".equals(vnp_TransactionType) || "03".equals(vnp_TransactionType))
                || vnp_TxnRef == null || !vnp_TxnRef.matches("[A-Za-z0-9_-]{1,100}")
                || amount <= 0
                || vnp_TransactionDate == null || !vnp_TransactionDate.matches("\\d{14}")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid refund request");
            return;
        }

        Calendar cld = Calendar.getInstance(Config.VNPAY_TIME_ZONE);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(Config.VNPAY_TIME_ZONE);
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = Config.getIpAddress(req);

        JsonObject vnp_Params = new JsonObject();
        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_Amount", vnp_Amount);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
        if (!vnp_TransactionNo.isEmpty()) {
            vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
        }
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, 
                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate, 
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hash_Data);
        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(Config.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setConnectTimeout(10_000);
        con.setReadTimeout(15_000);
        con.setDoOutput(true);
        try (OutputStreamWriter writer = new OutputStreamWriter(
                con.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(vnp_Params.toString());
        }

        int responseCode = con.getResponseCode();
        InputStream responseStream = responseCode >= 200 && responseCode < 300
                ? con.getInputStream() : con.getErrorStream();
        if (responseStream == null) {
            con.disconnect();
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, "VNPAY returned an empty response");
            return;
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
            String output;
            while ((output = reader.readLine()) != null) {
                response.append(output);
            }
        }
        con.disconnect();

        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(responseCode);
        resp.getWriter().write(response.toString());
    }
}
