package com.example.cart.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {

    private static final int MAX_QUANTITY = 99;

    private List<LineItem> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void addItem(LineItem newItem) {
        String productCode = newItem.getProduct().getCode();

        for (LineItem item : items) {
            if (item.getProduct().getCode().equals(productCode)) {
                item.setQuantity(Math.min(MAX_QUANTITY,
                        item.getQuantity() + newItem.getQuantity()));
                return;
            }
        }

        items.add(newItem);
    }

    public void removeItem(LineItem item) {
        String productCode = item.getProduct().getCode();
        for (int i = 0; i < items.size(); i++) {
            LineItem lineItem = items.get(i);
            if (lineItem.getProduct().getCode().equals(productCode)) {
                items.remove(i);
                return;
            }
        }
    }

    public double getTotal() {
        double total = 0;
        for (LineItem item : items) {
            total += item.getTotal();
        }
        return total;
    }

    public String getTotalCurrencyFormat() {
        java.text.NumberFormat currency = java.text.NumberFormat.getCurrencyInstance();
        return currency.format(this.getTotal());
    }

    /**
     * Quy đổi sang VNĐ (tỷ giá mặc định 1 USD = 25,000 VNĐ)
     */
    public long getTotalVND() {
        return Math.round(this.getTotal() * 25000);
    }
}
