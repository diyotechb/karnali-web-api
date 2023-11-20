package com.example.starter.business.controllers;

import com.example.starter.business.entity.Business;
import com.example.starter.business.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/business")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @GetMapping("/all")
    public List<Business> getAllBusiness(){
        return businessService.getAllBusiness();
    }
}
