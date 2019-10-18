package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseManager;
import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.service.BorderService;
import com.dtnsm.lms.service.CustomerService;
import com.dtnsm.lms.service.ScheduleService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class MainController {

    @Autowired
    BorderService borderService;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    UserServiceImpl userService;

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
        model.addAttribute("borders2", borderService.getListTop5ByBorderMasterId("BA0104"));

        model.addAttribute("schedule", scheduleService.getTop1BySctypeOrderByCreatedDateDesc(ScheduleType.MATRIX));


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