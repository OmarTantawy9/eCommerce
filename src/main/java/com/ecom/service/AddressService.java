package com.ecom.service;

import com.ecom.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO addAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses();

    AddressDTO updateAddress(AddressDTO addressDTO, Long addressId);

    String deleteAddressById(Long addressId);
}
