package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.mybatis.service.CourseMapperService;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    BorderService borderService;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseAccountService courseAccountService;

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

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseAdminController.class);

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



        logger.info("startdate = " + DateUtil.getStringMonthStartDate());
        logger.info("enddate = " + DateUtil.getStringMonthEndDate());

        // 월간 교육신청시작일 목록
        model.addAttribute("monthRequestList"
                , courseService.getCourseTop5ByRequestFromDateBetween(
                        DateUtil.getStringMonthStartDate()
                        , DateUtil.getStringMonthEndDate()));

        // Employee training matrix
        model.addAttribute("schedule", scheduleService.getTop1BySctypeOrderByCreatedDateDesc(ScheduleType.MATRIX));

        // 교육결재 1차 미결함
        model.addAttribute("app1List", courseAccountService.getListTop5ByApprUserId1AndIsAppr1(SessionUtil.getUserId(), "N"));

        // 교육결재 1차 완결함
        model.addAttribute("app1CommitList", courseAccountService.getCustomListTop5ByUserIdAndIsCommit(SessionUtil.getUserId(), "0"));


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