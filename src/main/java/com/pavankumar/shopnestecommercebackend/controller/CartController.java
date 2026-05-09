package com.pavankumar.shopnestecommercebackend.controller;

import com.pavankumar.shopnestecommercebackend.dto.AddToCartRequest;
import com.pavankumar.shopnestecommercebackend.dto.ApiResponse;
import com.pavankumar.shopnestecommercebackend.dto.CartResponse;
import com.pavankumar.shopnestecommercebackend.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Cart management APIs")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> get(){
        CartResponse response=cartService.getCart();
        return ResponseEntity.ok(ApiResponse.success
                (response,"Cart fetched successfully"));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart
            (@Valid @RequestBody AddToCartRequest request){
        CartResponse response=cartService.addToCart(request);
        return ResponseEntity.ok(ApiResponse.
                success(response,"Cart added cart item successfully"));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<ApiResponse<CartResponse>> removefromCart
            (@PathVariable Long id){
        CartResponse response=cartService.removefromCart(id);
        return ResponseEntity.ok(ApiResponse
                .success(response,"removed cart item"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(){
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }


}
