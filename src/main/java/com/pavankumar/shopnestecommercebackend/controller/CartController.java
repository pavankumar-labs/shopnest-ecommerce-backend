package com.pavankumar.shopnestecommercebackend.controller;

import com.pavankumar.shopnestecommercebackend.dto.AddToCartRequest;
import com.pavankumar.shopnestecommercebackend.dto.CartResponse;
import com.pavankumar.shopnestecommercebackend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(){
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request){
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartResponse> removefromCart(@PathVariable Long cartItemId){
        return ResponseEntity.ok(cartService.removefromCart(cartItemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(){
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }


}
