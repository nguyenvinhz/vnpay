package com.example.cart.model;

import java.io.Serializable;

public class LineItem implements Serializable {

    private Product product;
    private int quantity;

    public LineItem() {
    }

    public LineItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return product.getPrice() * quantity;
    }

    public String getTotalCurrencyFormat() {
        java.text.NumberFormat currency = java.text.NumberFormat.getCurrencyInstance();
        return currency.format(this.getTotal());
    }
}
