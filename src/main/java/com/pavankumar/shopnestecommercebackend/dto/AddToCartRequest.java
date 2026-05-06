package com.pavankumar.shopnestecommercebackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;
    @Positive(message = "Quantity must at least 1")
    @NotNull(message = "Quantity is required")
    private Integer quantity;
}
