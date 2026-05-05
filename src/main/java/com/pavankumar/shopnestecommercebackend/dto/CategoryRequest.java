package com.pavankumar.shopnestecommercebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max=50,message = "Category name cannot exceed 50 characters")
    private String name;


    @Size(max = 1000,message = "Description cannot exceed 1000 characters")
    private String description;
}
