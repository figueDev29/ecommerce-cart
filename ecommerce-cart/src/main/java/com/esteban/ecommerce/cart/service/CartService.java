package com.esteban.ecommerce.cart.service;

import com.esteban.ecommerce.cart.model.Cart;
import com.esteban.ecommerce.cart.model.Product;
import java.util.UUID;

public interface CartService {
    Cart createCart();
    
    Cart getCart(UUID id);

    Product addProduct(UUID cartId, Product product);

    void deleteCart(UUID cartId);

    void cleanupInactiveCarts();
}