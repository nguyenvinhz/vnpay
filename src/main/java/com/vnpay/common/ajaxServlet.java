package com.vnpay.common;

import com.example.cart.model.Cart;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Servlet xử lý tạo URL thanh toán chuẩn của VNPay cung cấp
 * Được ánh xạ vào các đường dẫn /vnpayajax, /checkout, /create-payment
 */
@WebServlet(name = "ajaxServlet", urlPatterns = {"/vnpayajax", "/checkout", "/create-payment"})
public class ajaxServlet extends HttpServlet {

    private static final long MAX_AMOUNT_VND = 9_999_999_999L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = req.getParameter("orderType");
        if (orderType == null || orderType.isEmpty()) {
            orderType = "other";
        }

        // Lấy số tiền từ request hoặc tính toán từ Giỏ hàng (Cart) trong session
        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        boolean cartCheckout = "/checkout".equals(req.getServletPath());
        long amount = 0;
        try {
            if (cartCheckout && cart != null && !cart.getItems().isEmpty()) {
                amount = Math.multiplyExact(cart.getTotalVND(), 100L);
            } else if (!cartCheckout) {
                long amountVnd = Long.parseLong(req.getParameter("amount"));
                if (amountVnd > 0 && amountVnd <= MAX_AMOUNT_VND) {
                    amount = Math.multiplyExact(amountVnd, 100L);
                }
            }
        } catch (NumberFormatException | ArithmeticException | NullPointerException ignored) {
            amount = 0;
        }

        // Nếu không có số tiền hợp lệ, quay lại giỏ hàng
        if (amount <= 0) {
            if (cartCheckout) {
                resp.sendRedirect(req.getContextPath() + "/cart");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Amount must be a positive VND value");
            }
            return;
        }

        String bankCode = req.getParameter("bankCode");
        if (bankCode != null && !bankCode.isEmpty()
                && !Arrays.asList("VNPAYQR", "VNBANK", "INTCARD").contains(bankCode)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported bank code");
            return;
        }
        String vnp_TxnRef = Config.getRandomNumber(8);
        if (cartCheckout) {
            session.setAttribute("pendingVnpayTxnRef", vnp_TxnRef);
            session.setAttribute("pendingVnpayAmount", amount);
        } else {
            session.removeAttribute("pendingVnpayTxnRef");
            session.removeAttribute("pendingVnpayAmount");
        }
        String vnp_IpAddr = Config.getIpAddress(req);
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        
        String orderInfo = req.getParameter("orderInfo");
        if (orderInfo == null || orderInfo.isEmpty()) {
            orderInfo = "Thanh toan don hang:" + vnp_TxnRef;
        }
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = req.getParameter("language");
        if ("en".equals(locate)) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }

        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(Config.VNPAY_TIME_ZONE);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(Config.VNPAY_TIME_ZONE);
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                if (!first) {
                    query.append('&');
                    hashData.append('&');
                }
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                first = false;
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        // Nếu request gửi từ AJAX
        String isAjax = req.getParameter("isAjax");
        if ("true".equalsIgnoreCase(isAjax)) {
            JsonObject job = new JsonObject();
            job.addProperty("code", "00");
            job.addProperty("message", "success");
            job.addProperty("data", paymentUrl);
            Gson gson = new Gson();
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write(gson.toJson(job));
        } else {
            // Chuyển hướng trực tiếp sang VNPay
            resp.sendRedirect(paymentUrl);
        }
    }
}
