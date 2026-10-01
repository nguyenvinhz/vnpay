<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="com.example.cart.model.Cart" %>
        <%@ page import="com.example.cart.model.LineItem" %>
            <%@ page import="java.text.NumberFormat" %>
                <%@ page import="java.util.Locale" %>

                    <!DOCTYPE html>
                    <html>

                    <head>
                        <meta charset="UTF-8">
                        <title>Your cart</title>
                        <style>
                            body {
                                font-family: Arial, Helvetica, sans-serif;
                                margin: 30px auto;
                                max-width: 800px;
                                padding: 0 15px;
                                color: #333;
                            }

                            h2 {
                                color: #0e777b;
                                font-size: 24px;
                                margin-bottom: 20px;
                                border-bottom: 2px solid #0e777b;
                                padding-bottom: 8px;
                            }

                            table {
                                width: 100%;
                                border-collapse: collapse;
                                border: 1px solid #777;
                                margin-bottom: 15px;
                            }

                            th,
                            td {
                                border: 1px solid #777;
                                padding: 8px 12px;
                                font-size: 14px;
                            }

                            th {
                                background-color: #f2f7f7;
                                font-weight: bold;
                                text-align: left;
                            }

                            th.price,
                            td.price,
                            th.amount,
                            td.amount {
                                text-align: right;
                            }

                            form {
                                margin: 0;
                                display: inline;
                            }

                            input[type="number"] {
                                width: 35px;
                                text-align: center;
                                padding: 4px;
                                border: 1px solid #ccc;
                                border-radius: 3px;
                            }

                            input[type="submit"],
                            button {
                                font-family: Arial, Helvetica, sans-serif;
                                font-size: 13px;
                                padding: 5px 10px;
                                cursor: pointer;
                                border-radius: 4px;
                                border: 1px solid #888;
                                background: #f0f0f0;
                                transition: all 0.2s;
                            }

                            input[type="submit"]:hover,
                            button:hover {
                                background: #e0e0e0;
                            }

                            .btn-update {
                                background: #e2e8f0;
                                border-color: #cbd5e1;
                            }

                            .btn-remove {
                                background: #fee2e2;
                                color: #b91c1c;
                                border-color: #fca5a5;
                            }

                            .btn-remove:hover {
                                background: #fecaca;
                            }

                            .instruction {
                                margin-top: 15px;
                                margin-bottom: 20px;
                                font-size: 13px;
                                color: #666;
                            }

                            .total-row {
                                background-color: #f8fafc;
                                font-size: 15px;
                            }

                            .total-label {
                                text-align: right;
                                font-weight: bold;
                            }

                            .total-value {
                                text-align: right;
                                font-weight: bold;
                                color: #0e777b;
                                font-size: 16px;
                            }

                            .total-vnd {
                                font-size: 13px;
                                color: #ed1c24;
                                display: block;
                                margin-top: 2px;
                            }

                            .checkout-box {
                                background: #f8fafc;
                                border: 1px solid #cbd5e1;
                                border-radius: 8px;
                                padding: 20px;
                                margin-top: 25px;
                            }

                            .checkout-title {
                                font-size: 16px;
                                font-weight: bold;
                                color: #1e293b;
                                margin-bottom: 12px;
                            }

                            .bank-select-group {
                                margin-bottom: 15px;
                            }

                            .bank-select-group label {
                                margin-right: 15px;
                                font-size: 14px;
                                cursor: pointer;
                            }

                            .btn-group {
                                display: flex;
                                gap: 12px;
                                align-items: center;
                                margin-top: 10px;
                            }

                            .btn-continue {
                                padding: 10px 18px;
                                background: #fff;
                                color: #333;
                                border: 1px solid #94a3b8;
                                font-size: 14px;
                                font-weight: 500;
                            }

                            .btn-vnpay-checkout {
                                padding: 10px 22px;
                                background: linear-gradient(135deg, #ed1c24 0%, #c41219 100%);
                                color: #fff;
                                border: none;
                                font-size: 15px;
                                font-weight: bold;
                                box-shadow: 0 2px 6px rgba(237, 28, 36, 0.3);
                                display: inline-flex;
                                align-items: center;
                                gap: 6px;
                            }

                            .btn-vnpay-checkout:hover {
                                background: linear-gradient(135deg, #d31820 0%, #a70c12 100%);
                            }

                            .empty-cart-msg {
                                text-align: center;
                                padding: 40px;
                                color: #64748b;
                                font-size: 16px;
                            }

                            .test-card-box {
                                margin-top: 25px;
                                background: #eff6ff;
                                border: 1px dashed #60a5fa;
                                border-radius: 6px;
                                padding: 12px 16px;
                                font-size: 12px;
                                color: #1e3a8a;
                            }

                            .test-card-box code {
                                font-weight: bold;
                                color: #1d4ed8;
                                background: #dbeafe;
                                padding: 2px 5px;
                                border-radius: 3px;
                            }
                        </style>
                    </head>

                    <body>

                        <h2>Your cart</h2>

                        <% Cart cart=(Cart) session.getAttribute("cart"); boolean hasItems=(cart !=null &&
                            cart.getItems() !=null && !cart.getItems().isEmpty()); double totalUSD=hasItems ?
                            cart.getTotal() : 0.0; long totalVND=hasItems ? cart.getTotalVND() : 0; NumberFormat
                            currencyFormat=NumberFormat.getCurrencyInstance(Locale.US); NumberFormat
                            vndFormat=NumberFormat.getInstance(new Locale("vi", "VN" )); %>

                            <% if (hasItems) { %>
                                <table>
                                    <thead>
                                        <tr>
                                            <th style="width: 120px;">Quantity</th>
                                            <th>Description</th>
                                            <th class="price" style="width: 100px;">Price</th>
                                            <th class="amount" style="width: 130px;">Amount</th>
                                            <th style="width: 110px;"></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (LineItem item : cart.getItems()) { %>
                                            <tr>
                                                <td>
                                                    <form action="cart" method="post">
                                                        <input type="hidden" name="action" value="cart">
                                                        <input type="hidden" name="productCode"
                                                            value="<%= item.getProduct().getCode() %>">
                                                        <input type="number" name="quantity" min="0" max="99"
                                                            value="<%= item.getQuantity() %>" required>
                                                        <input type="submit" value="Update" class="btn-update">
                                                    </form>
                                                </td>
                                                <td>
                                                    <%= item.getProduct().getName() %>
                                                </td>
                                                <td class="price">
                                                    <%= currencyFormat.format(item.getProduct().getPrice()) %>
                                                </td>
                                                <td class="amount">
                                                    <%= currencyFormat.format(item.getTotal()) %>
                                                </td>
                                                <td style="text-align: center;">
                                                    <form action="cart" method="post">
                                                        <input type="hidden" name="action" value="remove">
                                                        <input type="hidden" name="productCode"
                                                            value="<%= item.getProduct().getCode() %>">
                                                        <input type="submit" value="Remove Item" class="btn-remove">
                                                    </form>
                                                </td>
                                            </tr>
                                            <% } %>

                                                <!-- Tổng tiền giỏ hàng -->
                                                <tr class="total-row">
                                                    <td colspan="3" class="total-label">Total Order:</td>
                                                    <td class="total-value" colspan="2">
                                                        <%= currencyFormat.format(totalUSD) %>
                                                            <span class="total-vnd">(~ <%= vndFormat.format(totalVND) %>
                                                                    VNĐ)</span>
                                                    </td>
                                                </tr>
                                    </tbody>
                                </table>

                                <p class="instruction">
                                    <b>To change the quantity</b>, enter the new quantity and click on the <b>Update</b>
                                    button.
                                </p>

                                <!-- Khung Thanh toán tích hợp VNPay -->
                                <div class="checkout-box">
                                    <div class="checkout-title">💳 Chọn phương thức thanh toán VNPay:</div>

                                    <form action="checkout" method="post">
                                        <div class="bank-select-group">
                                            <label>
                                                <input type="radio" name="bankCode" value="" checked> Cổng thanh toán
                                                VNPay (Tự chọn ngân hàng)
                                            </label>
                                            <label>
                                                <input type="radio" name="bankCode" value="VNBANK"> Thẻ ATM / Tài khoản
                                                ngân hàng nội địa
                                            </label>
                                            <label>
                                                <input type="radio" name="bankCode" value="VNPAYQR"> Quét mã VNPAY-QR
                                            </label>
                                            <label>
                                                <input type="radio" name="bankCode" value="INTCARD"> Thẻ quốc tế
                                                (Visa/Master/JCB)
                                            </label>
                                        </div>

                                        <div class="btn-group">
                                            <a href="index.html" style="text-decoration: none;">
                                                <button type="button" class="btn-continue">Continue Shopping</button>
                                            </a>
                                            <button type="submit" class="btn-vnpay-checkout">
                                                Thanh toán qua VNPAY (Checkout) &rarr;
                                            </button>
                                        </div>
                                    </form>
                                </div>

                                <div class="test-card-box">
                                    <b>📌 Thông tin thẻ test Sandbox (NCB):</b> Số thẻ: <code>9704198526191432198</code>
                                    | Tên: <code>NGUYEN VAN A</code> | Ngày: <code>07/15</code> | OTP:
                                    <code>123456</code>
                                </div>

                                <% } else { %>
                                    <div class="empty-cart-msg">
                                        <p>🛒 Giỏ hàng của bạn đang trống (Your cart is empty).</p>
                                        <div style="margin-top: 15px;">
                                            <a href="index.html">
                                                <button type="button" class="btn-continue">Quay lại mua hàng (Shop
                                                    Now)</button>
                                            </a>
                                        </div>
                                    </div>
                                    <% } %>

                    </body>

                    </html>
