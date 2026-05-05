package com.pavankumar.shopnestecommercebackend.controller;

import com.pavankumar.shopnestecommercebackend.dto.CategoryRequest;
import com.pavankumar.shopnestecommercebackend.dto.CategoryResponse;
import com.pavankumar.shopnestecommercebackend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private  final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request){
        return  ResponseEntity.status(201).body(categoryService.createCategory(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.getByCategoryId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,@Valid @RequestBody CategoryRequest request){
        return ResponseEntity.ok(categoryService.updateCategory(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

}
