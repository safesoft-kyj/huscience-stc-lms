package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.Customer;
import com.dtnsm.lms.service.CustomerService;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/customer")
public class CustomerAdminController {

    @Autowired
    CustomerService customerService;

    @Autowired
    UserService userService;

    private PageInfo pageInfo = new PageInfo();

    public CustomerAdminController() {
        pageInfo.setParentId("m-customer");
        pageInfo.setParentTitle("협약기관");
    }

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageId("m-customer-list");
        pageInfo.setPageTitle("협약기관");
        model.addAttribute(pageInfo);
        model.addAttribute("customers", customerService.getCustomerList());

        return "admin/customer/list";
    }

    @GetMapping("/list-page")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        Page<Customer> elcCustomers = customerService.getCustomerPageList(pageable);

        pageInfo.setPageId("m-customer-list-page");
        pageInfo.setPageTitle("협약기관");
        model.addAttribute(pageInfo);
        model.addAttribute("customers", elcCustomers);

        return "admin/customer/list-page";
    }

    @GetMapping("/add")
    public String add(Customer customer, Model model) {

        pageInfo.setPageId("m-customer-add");
        pageInfo.setPageTitle("협약기관");
        model.addAttribute(pageInfo);
        //model.addAttribute("elCustomer", new ElCustomer());

        return "admin/customer/add";
    }

    @PostMapping("/add-post")
    public String addPost(@Valid Customer customer, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/customer/add";
        }

        customer.setAccount(userService.findByUserId(SessionUtil.getUserId()));

        customerService.save(customer);

        return "redirect:/admin/customer/list";
    }


    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Customer elCustomer = customerService.getCustomerById(id)
                .orElseThrow(()->new IllegalArgumentException("Invalid customer Id:" + id));

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle("협약기관");
        model.addAttribute(pageInfo);
        model.addAttribute("customer", elCustomer);

        return "admin/customer/update";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable("id") long id, @Valid Customer customer, BindingResult result) {
        if(result.hasErrors()) {
            customer.setId(id);
            return "content/customer/update";
        }

        customer.setAccount(userService.findByUserId(SessionUtil.getUserId()));
        customerService.save(customer);

        return "redirect:/admin/customer/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") long id) {

        Customer cust = customerService.getCustomerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer id:" + id));

        customerService.delete(cust);

        return "redirect:/admin/customer/list";
    }
}
