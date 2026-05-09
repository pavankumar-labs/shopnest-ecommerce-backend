package com.pavankumar.shopnestecommercebackend.controller;

import com.pavankumar.shopnestecommercebackend.dto.AddressRequest;
import com.pavankumar.shopnestecommercebackend.dto.AddressResponse;
import com.pavankumar.shopnestecommercebackend.dto.ApiResponse;
import com.pavankumar.shopnestecommercebackend.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "Address management APIs")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create
            (@RequestBody @Valid AddressRequest request){
        AddressResponse response=addressService.createAddress(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(response,"Address added successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getId
            (@PathVariable Long id){
        AddressResponse response=addressService.getAddressById(id);
        return  ResponseEntity.ok(
                ApiResponse.success(response,"Address fetched successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAll( ){
        List<AddressResponse> responses=addressService.getAllAddresses();
        return ResponseEntity.ok(ApiResponse
                .success(responses,"Addresses fetched successfully"));
    }
    @PutMapping("/{id}")
    public  ResponseEntity<ApiResponse<AddressResponse>> updateAddress
            (@PathVariable Long id,@RequestBody @Valid AddressRequest request){
        AddressResponse response=addressService.updateAddress(id,request);
        return ResponseEntity.ok(ApiResponse.success(response,"Address updated "));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/default")
    public ResponseEntity<ApiResponse<AddressResponse>> getDefaultAddress(){
        AddressResponse response=addressService.getDefaultAddress();
        return ResponseEntity.ok(ApiResponse
                .success(response,"Default Address successfully"));
    }
    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> makeDefault
            (@PathVariable Long id){
        AddressResponse response=addressService.makeDefault(id);
        return ResponseEntity.ok(ApiResponse.success(response," Address as default success"));
    }

}
