package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.dto.AddToCartRequest;
import com.pavankumar.shopnestecommercebackend.dto.CartItemResponse;
import com.pavankumar.shopnestecommercebackend.dto.CartResponse;
import com.pavankumar.shopnestecommercebackend.model.Cart;
import com.pavankumar.shopnestecommercebackend.model.CartItem;
import com.pavankumar.shopnestecommercebackend.model.Product;
import com.pavankumar.shopnestecommercebackend.model.User;
import com.pavankumar.shopnestecommercebackend.repository.CartItemRepository;
import com.pavankumar.shopnestecommercebackend.repository.CartRepository;
import com.pavankumar.shopnestecommercebackend.repository.ProductRepository;
import com.pavankumar.shopnestecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private  final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartResponse getCart(){
        User user=getCurrentUser();
        Cart cart=cartRepository.findByUserId(user.getId())
                .orElseGet(()->createCart(user));
        return mapToResponse(cart);
    }

    public CartResponse addToCart(AddToCartRequest request){
        User user=getCurrentUser();
        Product product=productRepository.findById(request.getProductId())
                .orElseThrow(()-> new RuntimeException("Product Not Found"));
        if(product.getStock()< request.getQuantity()){
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }
        Cart cart=cartRepository.findByUserId(user.getId())
                .orElseGet(()->createCart(user));
        Optional<CartItem> item=cartItemRepository
                .findByCartIdAndProductId(cart.getId(),product.getId());
        if(item.isPresent()){
            CartItem  cartItem=item.get();
            cartItem.setQuantity(cartItem.getQuantity()+request.getQuantity());
            cartItemRepository.save(cartItem);
        }
        else {
            CartItem cartItem=CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(cartItem);
        }
        Cart savedCart=cartRepository.save(cart);
       return mapToResponse(savedCart);
    }

    public CartResponse removefromCart(Long cartItemId){
        User user=getCurrentUser();
        Cart cart=cartRepository.findByUserId(user.getId())
                .orElseThrow(()->new RuntimeException("Cart is Not Found"));
        CartItem cartItem=cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new RuntimeException("CartItem Is not Found"));
        if(!(cartItem.getCart().getId().equals(cart.getId()))){
            throw new RuntimeException("Unauthorised");
        }
        cartItemRepository.delete(cartItem);
        return mapToResponse(cartRepository.findById(cart.getId()).get());
    }
    public CartResponse clearCart(){
        User user=getCurrentUser();
        Cart cart=cartRepository.findByUserId(user.getId())
                .orElseThrow(()->new RuntimeException("Cart Not found"));
        cart.getItems().clear();
        return mapToResponse(cart);
    }


    public Cart createCart(User user){
          Cart cart= Cart.builder()
                  .user(user)
                  .items(new ArrayList<>())
                  .build();
          return  cartRepository.save(cart);
    }

    private CartResponse mapToResponse(Cart cart){
        List<CartItemResponse> cartResponseList=cart.getItems()
                .stream()
                .map(item-> CartItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productPrice(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .subTotal(item.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build()).collect(Collectors.toList());
        BigDecimal total=cartResponseList.stream()
                .map(CartItemResponse::getSubTotal).
                reduce(BigDecimal.ZERO,BigDecimal::add);
        return CartResponse.builder()
                .id(cart.getId())
                .cartItems(cartResponseList)
                .totalAmount(total)
                .totalItems(cartResponseList.size())
                .build();
    }
    private User getCurrentUser(){
        String email= SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User Not Found"));
    }
}
