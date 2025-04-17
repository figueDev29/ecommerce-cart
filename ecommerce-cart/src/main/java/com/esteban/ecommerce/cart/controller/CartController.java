package com.esteban.ecommerce.cart.controller;

import com.esteban.ecommerce.cart.model.Cart;
import com.esteban.ecommerce.cart.model.Product;
import com.esteban.ecommerce.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<Cart> createCart() {
        Cart cart = cartService.createCart();
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable("id") UUID id) {
        Cart cart = cartService.getCart(id);
        return ResponseEntity.ok(cart);
    }

    @PostMapping(value = "/{id}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> addProduct(@PathVariable("id") UUID id,
                                              @Valid @RequestBody Product product) {
        Product addedProduct = cartService.addProduct(id, product);
        return ResponseEntity
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(addedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCart(@PathVariable("id") UUID id) {
        cartService.deleteCart(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart with id " + id + " has been successfully removed.");
        return ResponseEntity.ok(response);
    }
}

