package com.dtnsm.lms.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElCustomerService {

    ElCustomerRepository elCustomerRepository;

    public ElCustomerService(ElCustomerRepository elCustomerRepository) {
        this.elCustomerRepository = elCustomerRepository;
    }

    public List<ElCustomer> getCustomerList() {
        return elCustomerRepository.findAll();
    }

    public Page<ElCustomer> getCustomerPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "condate"));

        return elCustomerRepository.findAll(pageable);
    }

    public Optional<ElCustomer> getCustomerById(Long id) {

        return elCustomerRepository.findById(id);
    }

    public ElCustomer save(ElCustomer elcCustomer){
        return elCustomerRepository.save(elcCustomer);
    }

    public void delete(ElCustomer elcCustomer) {
        elCustomerRepository.delete(elcCustomer);
    }



}
