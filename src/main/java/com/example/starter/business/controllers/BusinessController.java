package com.example.starter.business.controllers;

import com.example.starter.auth.service.CognitoService;
import com.example.starter.business.entity.Business;
import com.example.starter.business.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private CognitoService cognitoService;

    @GetMapping("/all")
    public List<Business> getAllBusiness(){
        return businessService.getAllBusiness();
    }
    @GetMapping("/{businessId}")
    public Business getBusiness(@PathVariable Long businessId){
        return businessService.getBusiness(businessId);
    }
    @PostMapping("/save")
    public Business addBusiness(@RequestBody Business business){
        return businessService.addBusiness(business);
    }
    @PutMapping("/update")
    public Business updateBusiness(@RequestBody Business business){
        return businessService.updateBusiness(business);
    }
    @DeleteMapping("{businessId}")
    public String deleteYourEntity(@PathVariable Long businessId) {
        businessService.deleteById(businessId);
        return "Business deleted successfully";
    }
}
