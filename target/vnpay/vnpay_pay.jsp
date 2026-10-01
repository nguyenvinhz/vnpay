<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Tạo mới đơn hàng thanh toán VNPAY</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <style>
            body {
                padding-top: 30px;
                padding-bottom: 30px;
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
        </style>
    </head>
    <body>
        <div class="container">
            <h3 class="header-title">VNPAY DEMO - TẠO ĐƠN HÀNG THANH TOÁN</h3>
            <form action="vnpayajax" id="frmCreateOrder" method="post">        
                <div class="form-group">
                    <label for="amount">Số tiền (VNĐ)</label>
                    <input class="form-control" id="amount" name="amount" type="number" min="1" max="9999999999" value="10000" required />
                </div>
                <h4>Chọn phương thức thanh toán</h4>
                <div class="form-group">
                    <h5>Cách 1: Chuyển hướng sang Cổng VNPAY chọn phương thức thanh toán</h5>
                    <input type="radio" checked id="bank-default" name="bankCode" value="">
                    <label for="bank-default">Cổng thanh toán VNPAYQR</label><br>
                   
                    <h5>Cách 2: Tách phương thức tại site của Merchant</h5>
                    <input type="radio" id="bank-qr" name="bankCode" value="VNPAYQR">
                    <label for="bank-qr">Thanh toán bằng ứng dụng hỗ trợ VNPAYQR</label><br>
                    
                    <input type="radio" id="bank-domestic" name="bankCode" value="VNBANK">
                    <label for="bank-domestic">Thanh toán qua thẻ ATM/Tài khoản nội địa</label><br>
                    
                    <input type="radio" id="bank-international" name="bankCode" value="INTCARD">
                    <label for="bank-international">Thanh toán qua thẻ quốc tế</label><br>
                </div>
                <div class="form-group">
                    <h5>Chọn ngôn ngữ giao diện thanh toán:</h5>
                    <input type="radio" id="language-vn" checked name="language" value="vn">
                    <label for="language-vn">Tiếng việt</label><br>
                    <input type="radio" id="language-en" name="language" value="en">
                    <label for="language-en">Tiếng anh</label><br>
                </div>
                <button type="submit" class="btn btn-primary btn-block">Thanh toán qua VNPAY</button>
                <div class="text-center mt-3">
                    <a href="index.html">← Quay lại danh sách CD</a> | <a href="cart">Xem giỏ hàng</a>
                </div>
            </form>
            <footer class="footer text-center mt-4 text-muted">
                <p>&copy; VNPAY 2026</p>
            </footer>
        </div>
    </body>
</html>
