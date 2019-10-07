package com.dtnsm.lms.Controller;

import com.dtnsm.lms.customer.ElCustomer;
import com.dtnsm.lms.customer.ElCustomerService;
import com.dtnsm.lms.util.PageInfo;
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
public class CustomerController {

    @Autowired
    ElCustomerService elCustomerService;

    private PageInfo pageInfo = new PageInfo();

    public CustomerController() {
        pageInfo.setParentId("m-customer");
        pageInfo.setParentTitle("협약기관관리");
    }

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageId("m-customer-list");
        pageInfo.setPageTitle("협약기관조회");
        model.addAttribute(pageInfo);
        model.addAttribute("elCustomers", elCustomerService.getCustomerList());

        return "admin/customer/list";
    }

    @GetMapping("/list-page")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        Page<ElCustomer> elcCustomers = elCustomerService.getCustomerPageList(pageable);

        pageInfo.setPageId("m-customer-list-page");
        pageInfo.setPageTitle("조회");
        model.addAttribute(pageInfo);
        model.addAttribute("elCustomers", elcCustomers);

        return "admin/customer/list-page";
    }

    @GetMapping("/add")
    public String add(ElCustomer elCustomer, Model model) {

        pageInfo.setPageId("m-customer-add");
        pageInfo.setPageTitle("협약기관등록");
        model.addAttribute(pageInfo);
        //model.addAttribute("elCustomer", new ElCustomer());

        return "admin/customer/add";
    }

    @PostMapping("/add-post")
    public String addPost(@Valid ElCustomer elCustomer, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/customer/add";
        }

        elCustomerService.save(elCustomer);

        return "redirect:/admin/customer/list";
    }


    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        ElCustomer elCustomer = elCustomerService.getCustomerById(id)
                .orElseThrow(()->new IllegalArgumentException("Invalid customer Id:" + id));

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle("협약기관수정");
        model.addAttribute(pageInfo);
        model.addAttribute("elCustomer", elCustomer);

        return "admin/customer/update";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable("id") long id, @Valid ElCustomer elCustomer, BindingResult result) {
        if(result.hasErrors()) {
            elCustomer.setId(id);
            return "content/customer/update";
        }

        elCustomerService.save(elCustomer);

        return "redirect:/admin/customer/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") long id) {

        ElCustomer cust = elCustomerService.getCustomerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer id:" + id));

        elCustomerService.delete(cust);

        return "redirect:/admin/customer/list";
    }
}
