package com.example.starter.business.repository;

import com.example.starter.business.entity.Address;
import com.example.starter.business.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
