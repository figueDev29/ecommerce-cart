package com.esteban.ecommerce.cart.controller;

import com.esteban.ecommerce.cart.exception.CartNotFoundException;
import com.esteban.ecommerce.cart.exception.GlobalExceptionHandler;
import com.esteban.ecommerce.cart.exception.InvalidProductException;
import com.esteban.ecommerce.cart.model.Cart;
import com.esteban.ecommerce.cart.model.Product;
import com.esteban.ecommerce.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerIntegrationTest {

    private MockMvc mockMvc;
    private CartService cartService;
    private ObjectMapper mapper;

    private UUID cartId;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartService = mock(CartService.class);
        mapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
            .standaloneSetup(new CartController(cartService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        cartId = UUID.randomUUID();
        product = new Product(1L, "Test product", BigDecimal.valueOf(9.99));

        cart = new Cart(cartId);
        cart.setProducts(Collections.emptyList());
        cart.setLastAccessed(LocalDateTime.now());
    }

    @Test
    void createCart_shouldReturnNewCartWithEmptyProducts() throws Exception {
        when(cartService.createCart()).thenReturn(cart);

        mockMvc.perform(post("/api/carts")
                    .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(cartId.toString()))
               .andExpect(jsonPath("$.products").isArray())
               .andExpect(jsonPath("$.products.length()").value(0))
               .andExpect(jsonPath("$.lastAccessed").exists());

        verify(cartService, times(1)).createCart();
    }

    @Test
    void getCart_whenCartExistsWithProducts_shouldReturnCartWithThoseProducts() throws Exception {
        cart.setProducts(Collections.singletonList(product));
        when(cartService.getCart(cartId)).thenReturn(cart);

        mockMvc.perform(get("/api/carts/{id}", cartId)
                    .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(cartId.toString()))
               .andExpect(jsonPath("$.products").isArray())
               .andExpect(jsonPath("$.products.length()").value(1))
               .andExpect(jsonPath("$.products[0].id").value(product.getId()))
               .andExpect(jsonPath("$.products[0].description").value(product.getDescription()))
               .andExpect(jsonPath("$.products[0].amount").value(product.getAmount().doubleValue()))
               .andExpect(jsonPath("$.lastAccessed").exists());

        verify(cartService, times(1)).getCart(cartId);
    }

    @Test
    void getCart_whenCartDoesNotExist_shouldReturn404NotFound() throws Exception {
        when(cartService.getCart(cartId))
            .thenThrow(new CartNotFoundException("Cart not found: {}"));

        mockMvc.perform(get("/api/carts/{id}", cartId)
                    .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Cart not found: {}"));

        verify(cartService, times(1)).getCart(cartId);
    }

    @Test
    void addProduct_whenProductValid_shouldReturnAddedProduct() throws Exception {
        when(cartService.addProduct(eq(cartId), any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/carts/{id}/products", cartId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(product))
                    .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(product.getId()))
               .andExpect(jsonPath("$.description").value(product.getDescription()))
               .andExpect(jsonPath("$.amount").value(product.getAmount().doubleValue()));

        verify(cartService, times(1)).addProduct(eq(cartId), any(Product.class));
    }

    @Test
    void addProduct_whenProductAlreadyInCart_shouldReturn400BadRequest() throws Exception {
        when(cartService.addProduct(eq(cartId), any(Product.class)))
            .thenReturn(product)
            .thenThrow(new InvalidProductException("Product with id 1 is already in the cart"));

        mockMvc.perform(post("/api/carts/{id}/products", cartId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(product))
                    .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        mockMvc.perform(post("/api/carts/{id}/products", cartId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(product))
                    .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Product with id 1 is already in the cart"));

        verify(cartService, times(2)).addProduct(eq(cartId), any(Product.class));
    }

    @Test
    void deleteCart_whenCartExists_shouldReturnSuccessMessage() throws Exception {
        doNothing().when(cartService).deleteCart(cartId);

        mockMvc.perform(delete("/api/carts/{id}", cartId)
                    .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message")
                   .value("Cart with id " + cartId + " has been successfully removed."));

        verify(cartService, times(1)).deleteCart(cartId);
    }

    @Test
    void deleteCart_whenCartDoesNotExist_shouldReturn404NotFound() throws Exception {
        doThrow(new CartNotFoundException("Cart not found: {}"))
            .when(cartService).deleteCart(cartId);

        mockMvc.perform(delete("/api/carts/{id}", cartId)
                    .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Cart not found: {}"));

        verify(cartService, times(1)).deleteCart(cartId);
    }
}
