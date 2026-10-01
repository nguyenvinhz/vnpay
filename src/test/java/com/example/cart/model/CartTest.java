package com.example.cart.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartTest {

    @Test
    void addingSameProductMergesTheLineAndCapsQuantity() {
        Product product = new Product("pf01", "Paddlefoot", 12.95);
        Cart cart = new Cart();

        cart.addItem(new LineItem(product, 60));
        cart.addItem(new LineItem(product, 60));

        assertEquals(1, cart.getItems().size());
        assertEquals(99, cart.getItems().get(0).getQuantity());
    }

    @Test
    void convertsCartTotalToVnd() {
        Cart cart = new Cart();
        cart.addItem(new LineItem(new Product("pf01", "Paddlefoot", 12.95), 2));

        assertEquals(647_500L, cart.getTotalVND());
    }
}
