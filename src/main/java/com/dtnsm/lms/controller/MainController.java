package com.dtnsm.lms.controller;

import com.dtnsm.lms.service.BorderService;
import com.dtnsm.lms.service.CustomerService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    BorderService borderService;

    @Autowired
    CustomerService customerService;

    private PageInfo pageInfo = new PageInfo();

    public MainController() {
        pageInfo.setParentId("m-basecode");
        pageInfo.setParentTitle("코드관리");
    }

    @GetMapping("/")
    public String root(Model model) {

        model.addAttribute(pageInfo);
        model.addAttribute("customers", customerService.getCustomerList());
        model.addAttribute("borders", borderService.getListTop5ByBorderMasterId("BA0101"));

        model.addAttribute(pageInfo);

        return "content/home";
    }

    @GetMapping("/home")
    public String home(Model model) {



        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }
}