package com.pavankumar.shopnestecommercebackend.repository;

import com.pavankumar.shopnestecommercebackend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByRazorpayOrderId(String razorpayId);

}
