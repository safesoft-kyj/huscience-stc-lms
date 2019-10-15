package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.Customer;
import com.dtnsm.lms.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    CustomerRepository elCustomerRepository;

    public CustomerService(CustomerRepository elCustomerRepository) {
        this.elCustomerRepository = elCustomerRepository;
    }

    public List<Customer> getCustomerList() {
        return elCustomerRepository.findAll();
    }

    public Page<Customer> getCustomerPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "condate"));

        return elCustomerRepository.findAll(pageable);
    }

    public Optional<Customer> getCustomerById(Long id) {

        return elCustomerRepository.findById(id);
    }

    public Customer save(Customer elcCustomer){
        return elCustomerRepository.save(elcCustomer);
    }

    public void delete(Customer elcCustomer) {
        elCustomerRepository.delete(elcCustomer);
    }



}
