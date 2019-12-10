package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Customer;
import com.dtnsm.lms.service.CustomerService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    private PageInfo pageInfo = new PageInfo();

    public CustomerController() {
        pageInfo.setParentId("m-customer");
        pageInfo.setParentTitle("협약기관");
    }

    @GetMapping("/list-page")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        Page<Customer> customers = customerService.getCustomerPageList(pageable);

        pageInfo.setPageId("m-customer-list-page");
        pageInfo.setPageTitle("협약기관");
        model.addAttribute(pageInfo);
        model.addAttribute("customers", customers);

        return "content/customer/list-page";
    }

    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        Customer oldCustomer = customerService.getCustomerById(id).get();

        oldCustomer.setViewCnt(oldCustomer.getViewCnt() + 1);

        Customer customer= customerService.save(oldCustomer);

        pageInfo.setPageTitle("협약기관");

        model.addAttribute(pageInfo);
        model.addAttribute("customer", customer);

        return "content/customer/view";
    }
}
