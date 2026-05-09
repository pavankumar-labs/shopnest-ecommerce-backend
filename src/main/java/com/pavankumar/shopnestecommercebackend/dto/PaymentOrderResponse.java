package com.pavankumar.shopnestecommercebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponse {
    private String razorpayOrderId;
    private String keyID;
    private String currency;
    private BigDecimal amount;
}
