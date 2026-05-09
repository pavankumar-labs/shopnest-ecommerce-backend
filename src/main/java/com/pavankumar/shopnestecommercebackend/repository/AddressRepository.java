package com.pavankumar.shopnestecommercebackend.repository;

import com.pavankumar.shopnestecommercebackend.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUserId(Long userId);

    Optional<UserAddress> findByIdAndUserId(Long id,Long userId);

    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);
    
}
