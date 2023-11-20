package com.example.starter.business.repository;

import com.example.starter.business.entity.Business;
import com.example.starter.business.entity.BusinessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessTypeRepository extends JpaRepository<BusinessType, Long> {
}
