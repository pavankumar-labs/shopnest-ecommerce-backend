package com.pavankumar.shopnestecommercebackend.repository;

import com.pavankumar.shopnestecommercebackend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByStockGreaterThan(int stockIsGreaterThan);

    Page<Product> findAll(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword,Pageable pageable);
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
}
