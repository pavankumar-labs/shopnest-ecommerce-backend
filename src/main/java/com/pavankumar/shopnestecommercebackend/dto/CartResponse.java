package com.pavankumar.shopnestecommercebackend.dto;

import com.pavankumar.shopnestecommercebackend.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private long id;
    private List<CartItem> cartItems;
    private BigDecimal totalAmount;
    private Integer totalItems;
}
