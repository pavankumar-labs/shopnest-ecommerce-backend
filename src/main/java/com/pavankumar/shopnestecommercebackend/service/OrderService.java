package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.repository.*;
import com.pavankumar.shopnestecommercebackend.util.AuthUtil;
import com.pavankumar.shopnestecommercebackend.dto.OrderItemResponse;
import com.pavankumar.shopnestecommercebackend.dto.OrderResponse;
import com.pavankumar.shopnestecommercebackend.dto.PlaceOrderRequest;
import com.pavankumar.shopnestecommercebackend.exception.BadRequestException;
import com.pavankumar.shopnestecommercebackend.exception.ResourceNotFoundException;
import com.pavankumar.shopnestecommercebackend.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final AuthUtil util;
    private final AddressRepository addressRepository;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        User user = util.getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Cart not found: " + user.getId()));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException
                    ("Cannot place order with empty cart");
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException
                        ("Stock is Unavailable: " + product.getName());
            }
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase((product.getPrice()))
                    .build();
            BigDecimal subTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subTotal);
            product.setStock((product.getStock()) - cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        UserAddress address=addressRepository.findById(request.getAddressId())
                .orElseThrow(()->new ResourceNotFoundException("Address not found"));
        Order order = Order.builder()
                .user(user)
                .items(orderItems)
                .userAddress(address)

                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        Order savedOrder = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);
        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders() {
        User user = util.getCurrentUser();
        List<Order> orders = orderRepository.findByUserIdWithItems(user.getId());
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId) {
        User user = util.getCurrentUser();
        Order order = orderRepository.findByIdAndUserIdWithItems(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Order Not Found: " + orderId));
        return mapToOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        User user = util.getCurrentUser();
        Order order = orderRepository.findByIdAndUserIdWithItems(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Order Not Found: " + orderId));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only PENDING orders can be cancelled");
        }
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        emailService.sendOrderCancellation(user.getEmail(), user.getName(), orderId);
        return mapToOrderResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(newStatus);
        return mapToOrderResponse(orderRepository.save(order));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponseList = order.getItems()
                .stream().map(orderItem -> OrderItemResponse.builder()
                        .productId(orderItem.getProduct().getId())
                        .productName(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .priceAtPurchase(orderItem.getPriceAtPurchase())
                        .subTotal(orderItem.getPriceAtPurchase()
                                .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                        .build()).collect(Collectors.toList());
        return OrderResponse.builder()
                .status(order.getStatus().name())
                .items(itemResponseList)
                .address(order.getUserAddress().getAddressLine1())
                .pincode(order.getUserAddress().getPincode())
                .totalAmount(order.getTotalAmount())
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
