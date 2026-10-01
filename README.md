# Dự Án Web Bán Đĩa CD (Cart) Tích Hợp VNPAY Chuẩn Official

Dự án Java Servlet & JSP tích hợp cổng thanh toán trực tuyến **VNPAY** chuẩn chính thức theo đúng tài liệu SDK của VNPAY.

---

## Chạy Dự Án

Yêu cầu: JDK 8 trở lên và Maven 3.8 trở lên.

```powershell
mvn clean test package
mvn tomcat7:run
```

Sau đó mở `http://localhost:8080/vnpay/`.

Các giá trị sandbox mặc định vẫn có thể dùng ngay. Khi triển khai môi trường khác, cấu hình bằng biến môi trường thay vì sửa mã nguồn:

| Biến | Ý nghĩa |
|---|---|
| `VNPAY_TMN_CODE` | Mã website do VNPAY cấp |
| `VNPAY_HASH_SECRET` | Khóa bí mật dùng để ký request |
| `VNPAY_RETURN_URL` | URL VNPAY chuyển người dùng về sau thanh toán |
| `VNPAY_PAY_URL` | Endpoint tạo thanh toán |
| `VNPAY_API_URL` | Endpoint QueryDR/Refund |

---

## 📁 Cấu Trúc Dự Án Đã Tối Ưu (Chỉ Giữ Các File Cần Thiết)

```
d:/Nam3/WEB/vnpay/
│── pom.xml                                      # Quản lý thư viện Maven (Servlet API 4.0, Gson, tomcat7-maven-plugin)
│── src/main/
│   ├── java/
│   │   ├── com/example/cart/
│   │   │   ├── model/
│   │   │   │   ├── Product.java                 # Đối tượng sản phẩm CD
│   │   │   │   ├── LineItem.java                # Mục hàng trong giỏ
│   │   │   │   └── Cart.java                    # Quản lý giỏ hàng (tính tổng USD và quy đổi VNĐ)
│   │   │   └── servlet/
│   │   │       └── CartServlet.java             # Servlet xử lý giỏ hàng (/cart)
│   │   └── com/vnpay/common/
│   │       ├── Config.java                      # File cấu hình & hàm băm chính thức của VNPAY
│   │       ├── ajaxServlet.java                 # Servlet tạo đơn và chuyển hướng thanh toán (/vnpayajax, /checkout)
│   │       ├── vnpayQuery.java                  # Servlet gọi API querydr tra cứu trạng thái đơn hàng (/vnpayquery)
│   │       └── vnpayRefund.java                 # Servlet gọi API refund hoàn tiền giao dịch (/vnpayrefund)
│   └── webapp/
│       ├── WEB-INF/
│       │   └── web.xml                          # Khai báo cấu hình ứng dụng web
│       ├── index.html                           # Trang danh sách sản phẩm CD
│       ├── cart.jsp                             # Trang giỏ hàng tích hợp nút Thanh toán VNPAY
│       ├── vnpay_pay.jsp                        # Trang form tạo thanh toán VNPAY mẫu
│       ├── vnpay_return.jsp                     # Trang nhận kết quả thanh toán từ VNPAY
│       ├── vnpay_query.jsp                      # Trang form tra cứu giao dịch (querydr)
│       ├── vnpay_refund.jsp                     # Trang form yêu cầu hoàn tiền (refund)
│       └── vnpay_ipn.jsp                        # Webhook Server-to-Server nhận kết quả ngầm
└── README.md
```

---

## 🌐 Các Đường Dẫn Kiểm Thử

| Chức năng | Đường dẫn URL |
|---|---|
| Mua đĩa CD (Trang chủ) | `http://localhost:8080/vnpay/` |
| Giỏ hàng & Thanh toán | `http://localhost:8080/vnpay/cart` |
| Form thanh toán mẫu VNPAY | `http://localhost:8080/vnpay/vnpay_pay.jsp` |
| Tra cứu giao dịch (QueryDR) | `http://localhost:8080/vnpay/vnpay_query.jsp` |
| Hoàn tiền (Refund) | `http://localhost:8080/vnpay/vnpay_refund.jsp` |
| Kết quả thanh toán (Return) | `http://localhost:8080/vnpay/vnpay_return.jsp` |

---

## 💳 Thông Tin Thẻ Test Sandbox (Ngân Hàng NCB)

- **Ngân hàng:** `NCB`
- **Số thẻ:** `9704198526191432198`
- **Tên chủ thẻ:** `NGUYEN VAN A`
- **Ngày phát hành:** `07/15`
- **Mã OTP xác thực:** `123456`
