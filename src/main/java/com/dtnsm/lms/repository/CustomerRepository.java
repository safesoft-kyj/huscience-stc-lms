package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {


}