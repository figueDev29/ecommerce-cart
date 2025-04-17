package com.esteban.ecommerce.cart.service.impl;

import com.esteban.ecommerce.cart.exception.CartNotFoundException;
import com.esteban.ecommerce.cart.exception.InvalidProductException;
import com.esteban.ecommerce.cart.model.Cart;
import com.esteban.ecommerce.cart.model.Product;
import com.esteban.ecommerce.cart.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    private final Map<UUID, Cart> cartStorage = new ConcurrentHashMap<>();
    private final Duration timeout = Duration.ofMinutes(10);

    public CartServiceImpl() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
            this::cleanupInactiveCarts, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public Cart createCart() {
        UUID id = UUID.randomUUID();
        Cart cart = new Cart(id);
        cartStorage.put(id, cart);
        logger.info("Cart created with id: {}", id);
        return cart;
    }

    @Override
    public Cart getCart(UUID id) {
        Cart cart = Optional.ofNullable(cartStorage.get(id))
            .orElseThrow(() -> new CartNotFoundException("Cart with id " + id + " does not exist"));
    
        logger.info("Cart retrieved with id: {}", id);
        cart.setLastAccessed(LocalDateTime.now());
        return cart;
    }

    @Override
    public Product addProduct(UUID cartId, Product product) {
        Cart cart = Optional.ofNullable(cartStorage.get(cartId))
            .orElseThrow(() -> new CartNotFoundException("Cannot add product. Cart with id " + cartId + " does not exist"));
    
        boolean productAlreadyInCart = cart.getProducts().stream()
            .anyMatch(existingProduct -> existingProduct.getId().equals(product.getId()));
        
        if (productAlreadyInCart) {
            throw new InvalidProductException("Product with id " + product.getId() + " is already in the cart");
        }

        cart.getProducts().add(product);
        cart.setLastAccessed(LocalDateTime.now());
        logger.info("Product {} added to cart {}", product.getId(), cartId);
        return product;
    }

    @Override
    public void deleteCart(UUID cartId) {
        if (!cartStorage.containsKey(cartId)) {
            throw new CartNotFoundException("Cannot be deleted. The cart with id " + cartId + " does not exist");
        }
    
        cartStorage.remove(cartId);
        logger.info("Cart deleted: {}", cartId);
    }

    @Override
    public void cleanupInactiveCarts() {
        LocalDateTime now = LocalDateTime.now();
        cartStorage.entrySet().removeIf(entry -> {
            boolean expired = Duration.between(entry.getValue().getLastAccessed(), now).compareTo(timeout) > 0;
            if (expired) {
                logger.info("Deleting inactive cart: {}", entry.getKey());
            }
            return expired;
        });
    }
}