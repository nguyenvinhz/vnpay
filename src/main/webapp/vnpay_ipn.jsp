<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="com.vnpay.common.Config"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // IPN URL: Được VNPay gọi ngầm từ máy chủ sang máy chủ (Server-to-Server)
    try {
        Map<String, String> fields = new HashMap<String, String>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String rawFieldName = params.nextElement();
            String fieldName = URLEncoder.encode(rawFieldName, StandardCharsets.US_ASCII.toString());
            String rawFieldValue = request.getParameter(rawFieldName);
            String fieldValue = rawFieldValue == null ? null
                    : URLEncoder.encode(rawFieldValue, StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = Config.hashAllFields(fields);
        if (signValue.equalsIgnoreCase(vnp_SecureHash)) {
            boolean checkOrderId = true; // TODO: Kiểm tra OrderId có tồn tại trong CSDL không
            boolean checkAmount = true;  // TODO: Kiểm tra số tiền có khớp đơn hàng không
            boolean checkOrderStatus = false; // TODO: true nếu đơn hàng đã được cập nhật trước đó

            if (checkOrderId) {
                if (checkAmount) {
                    if (!checkOrderStatus) {
                        if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                            // Giao dịch thành công
                            // TODO: Cập nhật Database trạng thái ĐÃ THANH TOÁN
                        } else {
                            // Giao dịch thất bại
                            // TODO: Cập nhật Database trạng thái GIAO DỊCH THẤT BẠI
                        }
                        out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                    } else {
                        out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                    }
                } else {
                    out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                }
            } else {
                out.print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
            }
        } else {
            out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
        }
    } catch (Exception e) {
        out.print("{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}");
    }
%>
