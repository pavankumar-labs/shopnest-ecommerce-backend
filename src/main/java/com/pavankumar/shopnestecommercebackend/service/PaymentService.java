package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.util.AuthUtil;
import com.pavankumar.shopnestecommercebackend.dto.PaymentOrderResponse;
import com.pavankumar.shopnestecommercebackend.dto.PaymentVerifyRequest;
import com.pavankumar.shopnestecommercebackend.exception.ResourceNotFoundException;
import com.pavankumar.shopnestecommercebackend.exception.SignatureVerificationException;
import com.pavankumar.shopnestecommercebackend.model.*;
import com.pavankumar.shopnestecommercebackend.repository.OrderRepository;
import com.pavankumar.shopnestecommercebackend.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    private final AuthUtil util;
    private final OrderService orderService;


    @Value("${razorpay.key.id}")
    private String key;

    @Value("${razorpay.key.secret}")
    private String secretKey;

    @Value("${razorpay.currency}")
    private String currency;


    @Transactional
    public PaymentOrderResponse createPaymentOrder(Long orderId ) throws RazorpayException {
        User user=util.getCurrentUser();
        Order order=orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(()->new ResourceNotFoundException("Order not found"));
        int amountInPaise=order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100)).intValue();
        RazorpayClient client=new RazorpayClient(key,secretKey);
        JSONObject orderRequest=new JSONObject();
        orderRequest.put("amount",amountInPaise);
        orderRequest.put("currency",currency);
        com.razorpay.Order razorpayOrder=client.orders.create(orderRequest);
        Payment payment=Payment.builder()
                .razorpayOrderId(razorpayOrder.get("id"))
                .amount(order.getTotalAmount())
                .status(PaymentStatus.CREATED)
                .order(order)
                .build();
        paymentRepository.save(payment);
        return PaymentOrderResponse.builder()
                .keyID(key)
                .razorpayOrderId(razorpayOrder.get("id"))
                .currency(currency)
                .amount(order.getTotalAmount())
                .build();
    }

    @Transactional
    public  String verifyPayment(PaymentVerifyRequest paymentRequest) throws RazorpayException{
        JSONObject attributes=new JSONObject();
        attributes.put("razorpay_payment_id",paymentRequest.getRazorpayPaymentId());
        attributes.put("razorpay_order_id",paymentRequest.getRazorpayOrderId());
        attributes.put("razorpay_signature",paymentRequest.getSignature());
        boolean isValid= Utils.verifyPaymentSignature(attributes,secretKey);
        Payment payment=paymentRepository
                .findByRazorpayOrderIdWithLock(paymentRequest.getRazorpayOrderId())
                .orElseThrow(()->new ResourceNotFoundException("Payment not found"));
        if(!(isValid)){
                orderService.handleFailedPayment(payment);
            throw new SignatureVerificationException("Payment Signature verification failed");
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return "Payment already verified";
        }

        payment.setRazorpayPaymentId(paymentRequest.getRazorpayPaymentId());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        Order order=orderRepository.findById(payment.getOrder().getId())
                .orElseThrow(()->new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        emailService.sendOrderConfirmation
                (order.getUser().getEmail(),order.getUser().getName()
                        , order.getId(),order.getTotalAmount() );
        return "Payment verified.  Order Confirmed.";
    }

}
