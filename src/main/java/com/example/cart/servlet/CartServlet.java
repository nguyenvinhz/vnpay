package com.example.cart.servlet;

import com.example.cart.model.Cart;
import com.example.cart.model.LineItem;
import com.example.cart.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private static final int MAX_QUANTITY = 99;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "cart";
        }

        String url = "/cart.jsp";
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }

        if (action.equals("shop")) {
            url = "/index.html";
        } else if (action.equals("cart")) {
            String productCode = request.getParameter("productCode");
            String quantityString = request.getParameter("quantity");

            if (productCode != null && !productCode.isEmpty()) {
                Product product = findProduct(productCode);

                LineItem lineItem = null;
                for (LineItem item : cart.getItems()) {
                    if (item.getProduct().getCode().equals(productCode)) {
                        lineItem = item;
                        break;
                    }
                }

                if (quantityString != null) {
                    // Cập nhật số lượng trực tiếp từ cart.jsp
                    int quantity = 1;
                    try {
                        quantity = Integer.parseInt(quantityString);
                    } catch (NumberFormatException e) {
                        quantity = 1;
                    }
                    quantity = Math.min(quantity, MAX_QUANTITY);

                    if (lineItem != null) {
                        if (quantity > 0) {
                            lineItem.setQuantity(quantity);
                        } else {
                            cart.removeItem(lineItem);
                        }
                    } else if (quantity > 0 && product != null) {
                        lineItem = new LineItem(product, quantity);
                        cart.addItem(lineItem);
                    }
                } else {
                    // Thêm sản phẩm từ index.html (tăng số lượng thêm 1)
                    if (product != null) {
                        LineItem newItem = new LineItem(product, 1);
                        cart.addItem(newItem);
                    }
                }
            }
        } else if (action.equals("remove")) {
            String productCode = request.getParameter("productCode");
            if (productCode != null && !productCode.isEmpty()) {
                LineItem lineItem = null;
                for (LineItem item : cart.getItems()) {
                    if (item.getProduct().getCode().equals(productCode)) {
                        lineItem = item;
                        break;
                    }
                }
                if (lineItem != null) {
                    cart.removeItem(lineItem);
                }
            }
        }

        session.setAttribute("cart", cart);
        request.getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    private Product findProduct(String code) {
        if ("8601".equals(code)) {
            return new Product("8601", "86 (the band) - True Life Songs and Pictures", 14.95);
        }
        if ("pf01".equals(code)) {
            return new Product("pf01", "Paddlefoot - The first CD", 12.95);
        }
        if ("pf02".equals(code)) {
            return new Product("pf02", "Paddlefoot - The second CD", 14.95);
        }
        if ("jr01".equals(code)) {
            return new Product("jr01", "Joe Rut - Genuine Wood Grained Finish", 14.95);
        }
        return null;
    }
}
