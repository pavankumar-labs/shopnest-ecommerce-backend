package com.pavankumar.shopnestecommercebackend.controller;

import com.pavankumar.shopnestecommercebackend.dto.PageResponse;
import com.pavankumar.shopnestecommercebackend.dto.ProductRequest;
import com.pavankumar.shopnestecommercebackend.dto.ProductResponse;
import com.pavankumar.shopnestecommercebackend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request){
        return ResponseEntity.status(201).body(productService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ProductResponse> update(@PathVariable Long id,@RequestBody @Valid ProductRequest request){
        return ResponseEntity.ok(productService.updateProduct(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getByCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok(productService.getProductByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam String keyword){
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    @GetMapping("/search/paginated")
    public ResponseEntity<PageResponse<ProductResponse>> searchProductsPaginated(
                      @RequestParam String keyword,
                      @RequestParam(defaultValue = "0") int page ,
                      @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(productService.searchProductsPaginated(keyword,page,size));
    }

    @GetMapping("/paginated")
    public  ResponseEntity<PageResponse<ProductResponse>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(productService.getAllProductsPaginated(page,size));
    }

    @GetMapping("/category/paginated/{categoryId}")
    public ResponseEntity<PageResponse<ProductResponse>> paginatedProductsByCategoryId(
            @PathVariable  Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(productService.paginatedProductsByCategoryId(categoryId,page,size));
    }
}
