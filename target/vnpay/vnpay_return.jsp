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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <meta name="description" content="">
        <meta name="author" content="">
        <title>KẾT QUẢ THANH TOÁN VNPAY</title>
        <!-- Bootstrap core CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <style>
            body {
                padding-top: 40px;
                padding-bottom: 40px;
                background-color: #f8fafc;
            }
            .container {
                max-width: 680px;
                background: #ffffff;
                border-radius: 12px;
                padding: 30px;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
            }
            .header-title {
                color: #005baa;
                font-weight: 700;
                margin-bottom: 25px;
                text-align: center;
                border-bottom: 2px solid #e2e8f0;
                padding-bottom: 15px;
            }
            .status-success {
                color: #10b981;
                font-weight: bold;
            }
            .status-fail {
                color: #ef4444;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <%
            // Lấy toàn bộ tham số từ VNPay trả về
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
            // Tính toán lại mã băm kiểm tra bằng hàm chính thức của VNPay Config.hashAllFields
            String signValue = Config.hashAllFields(fields);
            boolean checkSignature = signValue.equalsIgnoreCase(vnp_SecureHash);
            String pendingTxnRef = (String) session.getAttribute("pendingVnpayTxnRef");
            Long pendingAmount = (Long) session.getAttribute("pendingVnpayAmount");
            boolean hasPendingCartOrder = pendingTxnRef != null && pendingAmount != null;
            boolean matchesPendingCartOrder = !hasPendingCartOrder
                    || (pendingTxnRef.equals(request.getParameter("vnp_TxnRef"))
                    && String.valueOf(pendingAmount).equals(request.getParameter("vnp_Amount")));
            boolean isSuccess = checkSignature && matchesPendingCartOrder
                    && "00".equals(request.getParameter("vnp_ResponseCode"));

            if (isSuccess && hasPendingCartOrder) {
                // Xóa giỏ hàng khi thanh toán thành công
                session.removeAttribute("cart");
                session.removeAttribute("pendingVnpayTxnRef");
                session.removeAttribute("pendingVnpayAmount");
            }

            long displayAmount = 0;
            try {
                displayAmount = Long.parseLong(request.getParameter("vnp_Amount")) / 100;
            } catch (Exception ignored) {}
        %>
        <!--Begin display -->
        <div class="container">
            <h3 class="header-title">VNPAY RESPONSE - KẾT QUẢ THANH TOÁN</h3>
            <div class="table-responsive">
                <table class="table table-bordered">
                    <tr>
                        <th style="width: 40%;">Mã đơn hàng:</th>
                        <td><%=Config.escapeHtml(request.getParameter("vnp_TxnRef"))%></td>
                    </tr>
                    <tr>
                        <th>Số tiền:</th>
                        <td><strong><%= String.format("%,d", displayAmount) %> VNĐ</strong></td>
                    </tr>
                    <tr>
                        <th>Mô tả đơn hàng:</th>
                        <td><%=Config.escapeHtml(request.getParameter("vnp_OrderInfo"))%></td>
                    </tr>
                    <tr>
                        <th>Mã lỗi phản hồi (Response Code):</th>
                        <td><%=Config.escapeHtml(request.getParameter("vnp_ResponseCode"))%></td>
                    </tr>
                    <tr>
                        <th>Mã GD Tại VNPAY:</th>
                        <td><%=Config.escapeHtml(request.getParameter("vnp_TransactionNo"))%></td>
                    </tr>
                    <tr>
                        <th>Mã Ngân hàng:</th>
                        <td><%=Config.escapeHtml(request.getParameter("vnp_BankCode"))%></td>
                    </tr>
                    <tr>
                        <th>Thời gian thanh toán:</th>
                        <td><%=Config.escapeHtml(request.getParameter("vnp_PayDate"))%></td>
                    </tr>
                    <tr>
                        <th>Tình trạng giao dịch:</th>
                        <td>
                            <%
                                if (!checkSignature) {
                                    out.print("<span class='status-fail'>Chữ ký không hợp lệ!</span>");
                                } else if (!matchesPendingCartOrder) {
                                    out.print("<span class='status-fail'>Giao dịch không khớp với đơn hàng đang chờ!</span>");
                                } else {
                                    if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                                        out.print("<span class='status-success'>Thành công (Giao dịch đã được ghi nhận)</span>");
                                    } else {
                                        out.print("<span class='status-fail'>Không thành công (Mã lỗi: "
                                                + Config.escapeHtml(request.getParameter("vnp_ResponseCode")) + ")</span>");
                                    }
                                }
                            %>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="text-center mt-4">
                <a href="index.html" class="btn btn-primary mr-2">Tiếp tục mua hàng (CD List)</a>
                <a href="cart" class="btn btn-secondary">Xem giỏ hàng (Cart)</a>
            </div>
            <footer class="footer text-center mt-4 text-muted">
                <p>&copy; VNPAY 2026</p>
            </footer>
        </div>
    </body>
</html>
