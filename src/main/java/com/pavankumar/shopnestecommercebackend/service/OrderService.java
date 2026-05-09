package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.util.AuthUtil;
import com.pavankumar.shopnestecommercebackend.dto.OrderItemResponse;
import com.pavankumar.shopnestecommercebackend.dto.OrderResponse;
import com.pavankumar.shopnestecommercebackend.dto.PlaceOrderRequest;
import com.pavankumar.shopnestecommercebackend.exception.BadRequestException;
import com.pavankumar.shopnestecommercebackend.exception.ResourceNotFoundException;
import com.pavankumar.shopnestecommercebackend.model.*;
import com.pavankumar.shopnestecommercebackend.repository.CartRepository;
import com.pavankumar.shopnestecommercebackend.repository.OrderRepository;
import com.pavankumar.shopnestecommercebackend.repository.ProductRepository;
import com.pavankumar.shopnestecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final AuthUtil util;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        User user = util.getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Cart not found" + user.getId()));
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
            productRepository.save(product);
            orderItems.add(orderItem);
        }
        Order order = Order.builder()
                .user(user)
                .items(orderItems)
                .shippingAddress(request.getShippingAddress())
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
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId) {
        User user = util.getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Order Not Found: " + orderId));
        return mapToOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        User user = util.getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Order Not Found: " + orderId));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only PENDING orders can be cancelled");
        }
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQuantity());
            productRepository.save(product);
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        emailService.sendOrderCancellation(user.getEmail(), user.getName(), orderId);
        return mapToOrderResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
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
                .shippingAddress(order.getShippingAddress())
                .totalAmount(order.getTotalAmount())
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
