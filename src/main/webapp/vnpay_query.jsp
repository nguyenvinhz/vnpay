<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>VNPAY - TRUY VẤN KẾT QUẢ GIAO DỊCH (QUERYDR)</title>
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
            <h3 class="header-title">VNPAY - TRUY VẤN GIAO DỊCH (QUERYDR)</h3>
            <form action="vnpayquery" id="frmQuery" method="post">        
                <div class="form-group">
                    <label for="order_id">Mã đơn hàng cần kiểm tra (vnp_TxnRef):</label>
                    <input class="form-control" id="order_id" name="order_id" type="text" maxlength="100" pattern="[A-Za-z0-9_-]+" placeholder="Ví dụ: 84729184" required />
                </div>
                <div class="form-group">
                    <label for="trans_date">Thời gian khởi tạo GD (yyyyMMddHHmmss):</label>
                    <input class="form-control" id="trans_date" name="trans_date" type="text" inputmode="numeric" pattern="[0-9]{14}" maxlength="14" placeholder="Ví dụ: 20261001103000" required />
                    <small class="form-text text-muted">Thời gian thanh toán lúc tạo đơn hàng (định dạng 14 chữ số)</small>
                </div>
                <button type="submit" class="btn btn-primary btn-block">Tra cứu trạng thái giao dịch</button>
                <div class="text-center mt-3">
                    <a href="index.html">← Danh sách CD</a> | <a href="vnpay_pay.jsp">Tạo đơn VNPAY</a> | <a href="vnpay_refund.jsp">Hoàn tiền</a>
                </div>
            </form>
            <footer class="footer text-center mt-4 text-muted">
                <p>&copy; VNPAY 2026</p>
            </footer>
        </div>
    </body>
</html>
