package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.dto.PageResponse;
import com.pavankumar.shopnestecommercebackend.dto.ProductRequest;
import com.pavankumar.shopnestecommercebackend.dto.ProductResponse;
import com.pavankumar.shopnestecommercebackend.exception.ResourceNotFoundException;
import com.pavankumar.shopnestecommercebackend.model.Category;
import com.pavankumar.shopnestecommercebackend.model.Product;
import com.pavankumar.shopnestecommercebackend.repository.CategoryRepository;
import com.pavankumar.shopnestecommercebackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse createProduct(ProductRequest request){
        Category category=categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()->new ResourceNotFoundException
                        ("Category not found: "+request.getCategoryId() ));
        Product product=Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(category)
                .build();
        Product savedProduct=productRepository.save(product);
        return mapToResponse(savedProduct); }

    public List<ProductResponse> getAllProducts(){
        return productRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id){
        Product product=productRepository
                .findById(id).orElseThrow(()->new ResourceNotFoundException
                        ("Product not found: "+id));
        return mapToResponse(product);
    }

    public  ProductResponse updateProduct(Long id,ProductRequest request){
        Product product=productRepository
                .findById(id).orElseThrow(()->new ResourceNotFoundException
                        ("Product not found: "+id));
        Category category=categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(()->new ResourceNotFoundException
                        ("Category not found: " +request.getCategoryId()));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        return mapToResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id){
        Product product=productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException
                        ("Product not found: " + id));
        productRepository.delete(product);
    }

    public List<ProductResponse> getProductByCategory(Long categoryId){
        if(!(categoryRepository.existsById(categoryId))){
            throw new ResourceNotFoundException
                    ("Category not found: " + categoryId);
        }
        return productRepository.findByCategory_Id(categoryId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> searchProducts(String keyword){
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public PageResponse<ProductResponse> searchProductsPaginated
            (String keyword, int page,int size){
        Pageable pageable= PageRequest.of(page,size);
        Page<Product> productPage=productRepository
                .findByNameContainingIgnoreCase(keyword,pageable);
        return maptoPageResponse(productPage);
    }

    public  PageResponse<ProductResponse> getAllProductsPaginated(int page,int size){
        Pageable pageable=PageRequest.of(page,size);
        Page<Product> productPage=productRepository.findAll(pageable);
        return maptoPageResponse(productPage);
    }

    public PageResponse<ProductResponse> paginatedProductsByCategoryId
            (Long categoryId,int page,int size){
        Pageable pageable=PageRequest.of(page,size);
        Page<Product> productPage=productRepository.findByCategory_Id(categoryId,pageable);
        return maptoPageResponse(productPage);

    }

    private ProductResponse mapToResponse(Product product){
        return ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .categoryName(product.getCategory().getName())
                .categoryId(product.getCategory().getId())
                .createdAt(product.getCreatedAt())
                .stock(product.getStock())
                .build();
    }

    private  PageResponse<ProductResponse> maptoPageResponse(Page<Product> productPage){
        List<ProductResponse> content=productPage.getContent()
                .stream().map(this::mapToResponse).toList();
        return PageResponse.<ProductResponse>builder()
                .content(content)
                .size(productPage.getSize())
                .firstPage(productPage.isFirst())
                .lastPage(productPage.isLast())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .page(productPage.getNumber())
                .build();
    }

}
