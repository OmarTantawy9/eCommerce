package com.ecom.service;

import com.ecom.exceptions.APIException;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.model.Address;
import com.ecom.model.User;
import com.ecom.payload.AddressDTO;
import com.ecom.repository.AddressRepository;
import com.ecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private final AuthUtil authUtil;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AddressDTO addAddress(AddressDTO addressDTO) {

        User user = authUtil.getLoggedInUser();
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        user.getAddresses().add(address);

        Address savedAddress =  addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {

        List<Address> addresses = addressRepository.findAll();

        if(addresses.isEmpty()){
            throw new APIException("No addresses found");
        }

        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        return addressDTOS;

    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses() {

        User user = authUtil.getLoggedInUser();

        List<Address> addresses = addressRepository.findByUserUserId(user.getUserId());

        if(addresses.isEmpty()){
            throw new APIException("No addresses found");
        }

        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        return addressDTOS;
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(AddressDTO addressDTO, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPostalCode(addressDTO.getPostalCode());

        Address updatedAddress = addressRepository.save(address);

        return modelMapper.map(updatedAddress, AddressDTO.class);

    }

    @Override
    @Transactional
    public String deleteAddressById(Long addressId) {

        if(!(addressRepository.existsById(addressId))){
            throw new ResourceNotFoundException("Address", "id", addressId);
        }

        addressRepository.deleteById(addressId);

        return "Address successfully deleted";
    }

}
