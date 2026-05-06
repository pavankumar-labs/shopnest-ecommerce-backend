package com.pavankumar.shopnestecommercebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequest {
    @NotBlank(message = "shippingAddress ids required")
    private String shippingAddress;
}
