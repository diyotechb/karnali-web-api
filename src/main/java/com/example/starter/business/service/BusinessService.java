package com.example.starter.business.service;

import com.example.starter.auth.service.AuthService;
import com.example.starter.business.entity.Business;
import com.example.starter.business.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusinessService {
    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    AuthService authService;

    public List<Business> getAllBusiness(){
        return businessRepository.findAll();
    }

    public Business getBusiness(Long businessId){
        return businessRepository.findById(businessId).orElse(null);
    }

    public Business addBusiness(Business business){
        String userId = authService.getLoggedInUserId();
        business.setUserId(userId);
        return businessRepository.save(business);
    }
    public void deleteById(Long id) {
        businessRepository.deleteById(id);
    }
    public Business updateBusiness( Business business) {
        Optional<Business> businessOptional = businessRepository.findById(business.getId());
        Business business1 = businessOptional.get();
        business1.setName(business.getName());
        business1.setAddress(business.getAddress());
        business1.setUserId(business.getUserId());
        business1.setBusinessType(business.getBusinessType());
        return businessRepository.save(business1);
    }
}
