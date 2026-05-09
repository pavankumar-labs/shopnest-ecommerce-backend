package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.dto.CategoryRequest;
import com.pavankumar.shopnestecommercebackend.dto.CategoryResponse;
import com.pavankumar.shopnestecommercebackend.exception.ResourceAlreadyExistsException;
import com.pavankumar.shopnestecommercebackend.exception.ResourceNotFoundException;
import com.pavankumar.shopnestecommercebackend.model.Category;
import com.pavankumar.shopnestecommercebackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.existsByName(request.getName())){
            throw new ResourceAlreadyExistsException
                    ("Category already exist: "  + request.getName());
        }
        Category category=Category.builder().
                name(request.getName())
                .description(request.getDescription())
                .build();
        Category saved=categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public CategoryResponse getByCategoryId(Long id){
        Category category=categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException
                        ("Category not found: " + id));
        return mapToResponse(category);
    }

    public CategoryResponse updateCategory(Long id,CategoryRequest request){

        Category category=categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException
                        ("Category not found: " + id));
        if(!(category.getName().equals(request.getName()))
                && categoryRepository.existsByName(request.getName())){
            throw new ResourceAlreadyExistsException
                    ( "Category already exists: "+category.getName());
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return mapToResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id){
        Category category=categoryRepository.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException
                                ("Category not found: " + id));
        categoryRepository.delete(category);
    }
    
    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    private CategoryResponse mapToResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
