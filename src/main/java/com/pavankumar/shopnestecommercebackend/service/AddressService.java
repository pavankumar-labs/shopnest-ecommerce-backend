package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.util.AuthUtil;
import com.pavankumar.shopnestecommercebackend.dto.AddressRequest;
import com.pavankumar.shopnestecommercebackend.dto.AddressResponse;
import com.pavankumar.shopnestecommercebackend.exception.BadRequestException;
import com.pavankumar.shopnestecommercebackend.exception.ResourceNotFoundException;
import com.pavankumar.shopnestecommercebackend.model.User;
import com.pavankumar.shopnestecommercebackend.model.UserAddress;
import com.pavankumar.shopnestecommercebackend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final AuthUtil util;


    public AddressResponse createAddress(AddressRequest request){
        User user=util.getCurrentUser();
        UserAddress address= UserAddress.builder()
                .phoneNumber(request.getPhoneNumber())
                .user(user)
                .fullName(request.getFullName())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry())
                .build();
        UserAddress savedAddress=addressRepository.save(address);
        return mapToAddress(savedAddress);
    }
    public AddressResponse getAddressById(Long id){
        User user=util.getCurrentUser();
        UserAddress address=addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(()->new ResourceNotFoundException("Address not found"));
        return mapToAddress(address);
    }
    public List<AddressResponse> getAllAddresses(){
        User user=util.getCurrentUser();
        return addressRepository
                .findByUserId(user.getId()).stream()
                .map(this::mapToAddress)
                .toList();
    }
    public AddressResponse updateAddress(Long id,AddressRequest request){
        User user=util.getCurrentUser();
        UserAddress userAddress=addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(()->new ResourceNotFoundException("Address not found"));
        userAddress.setFullName(request.getFullName());
        userAddress.setPhoneNumber(request.getPhoneNumber());
        userAddress.setAddressLine1(request.getAddressLine1());
        userAddress.setAddressLine2(request.getAddressLine2());
        userAddress.setPincode(request.getPincode());
        userAddress.setCity(request.getCity());
        userAddress.setState(request.getState());
        userAddress.setCountry(request.getCountry());
        return mapToAddress(addressRepository.save(userAddress));
    }


    public void deleteAddress(Long id){
        User user=util.getCurrentUser();
        UserAddress userAddress=addressRepository.findByIdAndUserId(id,user.getId())
                .orElseThrow(()->new ResourceNotFoundException("Address not found"));
        if(Boolean.TRUE.equals(userAddress.getIsDefault())){
            throw new BadRequestException("Cannot delete default address. Set another address as default first");
        }
        addressRepository.delete(userAddress);
    }

    public AddressResponse getDefaultAddress(){
        User user=util.getCurrentUser();
        UserAddress userAddress=addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .orElseThrow(()->new ResourceNotFoundException("No default address found. Please set a default address"));
        return  mapToAddress(userAddress);
    }

    @Transactional
    public AddressResponse makeDefault(Long id){
        User user=util.getCurrentUser();
        UserAddress address=addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(()->new ResourceNotFoundException("Address not found"));
        addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                        .ifPresent(currentAddress->
                        {currentAddress.setIsDefault(false);
                            addressRepository.save(currentAddress);});
        address.setIsDefault(true);
        addressRepository.save(address);
        return mapToAddress(address);
    }


    private AddressResponse mapToAddress(UserAddress address){
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phoneNumber(address.getPhoneNumber())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .pincode(address.getPincode())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .build();
    }

}
