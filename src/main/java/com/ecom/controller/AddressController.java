package com.ecom.controller;

import com.ecom.payload.AddressDTO;
import com.ecom.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {

   private final AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO savedAddressDTO = addressService.addAddress(addressDTO);
        return new  ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOS = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        List<AddressDTO> addressDTOS = addressService.getUserAddresses();
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO,
                                                    @PathVariable Long addressId) {
        AddressDTO updatedAddressDTO = addressService.updateAddress(addressDTO, addressId);
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        String status = addressService.deleteAddressById(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
