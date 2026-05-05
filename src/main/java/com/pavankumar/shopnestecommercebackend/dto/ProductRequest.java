package com.pavankumar.shopnestecommercebackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @Size(max = 100,message = "Product name cannot exceed 100 characters")
    @NotBlank(message = "Product name is required")
    private String name;

    @Size(max = 1000,message = "Description cannot exceed 1000 characters")
    private String description;

    @DecimalMin(value = "0.01",message = "Price must be greater than 0")
    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    private String imageUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;





}
