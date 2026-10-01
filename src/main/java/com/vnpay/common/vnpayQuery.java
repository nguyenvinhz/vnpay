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
 * Servlet thực hiện API truy vấn giao dịch (querydr) chuẩn của VNPay
 */
@WebServlet(name = "vnpayQuery", urlPatterns = {"/vnpayquery", "/vnpayQuery"})
public class vnpayQuery extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("vnpay_query.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // Command: querydr
        String vnp_RequestId = Config.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TmnCode = Config.vnp_TmnCode;
        String vnp_TxnRef = req.getParameter("order_id");
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
        String vnp_TransDate = req.getParameter("trans_date");

        if (vnp_TxnRef == null || !vnp_TxnRef.matches("[A-Za-z0-9_-]{1,100}")
                || vnp_TransDate == null || !vnp_TransDate.matches("\\d{14}")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID or transaction date");
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
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransDate);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, 
                vnp_TxnRef, vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
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
