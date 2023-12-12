package com.example.starter.business.service;

import com.example.starter.auth.service.AuthService;
import com.example.starter.business.entity.Address;
import com.example.starter.business.entity.Business;
import com.example.starter.business.repository.AddressRepository;
import com.example.starter.business.repository.BusinessRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusinessService {
    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    AuthService authService;

    public List<Business> getAllBusiness(){
        return businessRepository.findAll();
    }

    public Business getBusiness(Long businessId){
        return businessRepository.findById(businessId).orElse(null);
    }

    @Transactional
    public Business addBusiness(Business business) {
        try {
            String userId = authService.getLoggedInUserId();
            Address savedAddress = addressRepository.save(business.getAddress());
            business.setUserId(userId);
            business.setAddress(savedAddress);

            Business newBusiness = new Business();
            business.setName(newBusiness.getName());
            business.setUserId(userId);
            business.setAddress(savedAddress);

            return businessRepository.save(business);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add business", e);
        }
    }
    @Transactional
    public void deleteById(Long id) {
        try {
            Optional<Business> businessOptional = businessRepository.findById(id);
            if (businessOptional.isPresent()) {
                Business business = businessOptional.get();
                Address address = business.getAddress();

                businessRepository.delete(business);
                addressRepository.delete(address);
            } else {
                throw new EntityNotFoundException("Business with ID " + id + " not found");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to delete business with ID " + id, e);
        }
    }

    @Transactional
    public Business updateBusiness(Business businessRequest) {
        try {
            Long businessId = businessRequest.getId();
            Optional<Business> businessOptional = businessRepository.findById(businessId);

            if (businessOptional.isPresent()) {
                Business existingBusiness = businessOptional.get();

                if (businessRequest.getAddress() != null) {
                    updateAddress(existingBusiness.getAddress(), businessRequest.getAddress());
                    addressRepository.save(existingBusiness.getAddress());
                }

                existingBusiness.setName(businessRequest.getName());
                existingBusiness.setAddress(businessRequest.getAddress());
                existingBusiness.setUserId(businessRequest.getUserId());
                existingBusiness.setBusinessType(businessRequest.getBusinessType());

                return businessRepository.save(existingBusiness);
            } else {
                throw new EntityNotFoundException("Business with ID " + businessId + " not found");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to update business with ID " + businessRequest.getId(), e);
        }
    }

    private void updateAddress(Address existingAddress, Address addressDTO) {
        existingAddress.setStreet(addressDTO.getStreet());
        existingAddress.setCity(addressDTO.getCity());
        existingAddress.setState(addressDTO.getState());
        existingAddress.setPostalCode(addressDTO.getPostalCode());
    }
}
