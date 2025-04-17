package com.esteban.ecommerce.cart.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class Product {

    @NotNull(message = "Product id cannot be null")
    @Positive(message = "Product id must be a positive value")
    private Long id;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "The amount is mandatory")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    public Product() {}

    public Product(Long id, String description, BigDecimal amount) {
        this.id = id;
        this.description = description;
        this.amount = amount;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}