package com.example.starter.business.service;

import com.example.starter.business.entity.Business;
import com.example.starter.business.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessService {
    @Autowired
    BusinessRepository businessRepository;
    public List<Business> getAllBusiness(){
        return businessRepository.findAll();
    }
}
