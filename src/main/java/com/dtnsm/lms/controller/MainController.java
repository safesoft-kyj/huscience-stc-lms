package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.mybatis.service.CourseMapperService;
import com.dtnsm.lms.service.BorderService;
import com.dtnsm.lms.service.CourseMasterService;
import com.dtnsm.lms.service.CustomerService;
import com.dtnsm.lms.service.ScheduleService;
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
    ScheduleService scheduleService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CustomerService customerService;

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    CourseMapperService courseMapperService;

    private PageInfo pageInfo = new PageInfo();

    public MainController() {
        pageInfo.setParentId("m-base-home");
        pageInfo.setParentTitle("코드관리");
    }

    @GetMapping("/")
    public String root(Model model) {

        model.addAttribute(pageInfo);
        model.addAttribute("customers", customerService.getCustomerList());
        // 공지사항
        model.addAttribute("borders", borderService.getListTop5ByBorderMasterId("BA0101"));
        // What's New
        model.addAttribute("borders2", borderService.getListTop5ByBorderMasterId("BA0104"));

        // Employee training matrix
        model.addAttribute("schedule", scheduleService.getTop1BySctypeOrderByCreatedDateDesc(ScheduleType.MATRIX));


        // 과정그룹별 과정 수
        model.addAttribute("courseMasterList", courseMasterService.getList());

        // 과정그룹별 과정 수
        model.addAttribute("courseCountVO", courseMapperService.getCourseCount());

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