package com.pavankumar.shopnestecommercebackend.controller;

import com.pavankumar.shopnestecommercebackend.dto.ApiResponse;
import com.pavankumar.shopnestecommercebackend.dto.PaymentOrderResponse;
import com.pavankumar.shopnestecommercebackend.dto.PaymentVerifyRequest;
import com.pavankumar.shopnestecommercebackend.service.PaymentService;
import com.razorpay.RazorpayException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "Payments",description = "Payments management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Create payment order")
    @PostMapping("/create/{id}")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> create
            (@PathVariable Long id ) throws RazorpayException{
        PaymentOrderResponse response=paymentService.createPaymentOrder(id);
        return ResponseEntity.status(201).body(ApiResponse
                .success(response,"Payment order created"));
    }

    @Operation(summary = "Verify Razorpay payment signature and confirm order")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify
            (@Valid @RequestBody PaymentVerifyRequest request) throws RazorpayException{
        String response=paymentService.verifyPayment(request);
        return ResponseEntity.ok(ApiResponse
                .success(response,"verified payment "));
    }


}
