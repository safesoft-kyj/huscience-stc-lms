package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.DocumentAccount;
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

import java.util.List;

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

    @Autowired
    DocumentAccountService documentAccountService;

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
                        , DateUtil.getStringMonthEndDate(), 0));

        // Employee training matrix
        model.addAttribute("schedule", scheduleService.getTop1BySctypeOrderByCreatedDateDesc(ScheduleType.MATRIX));

        // 교육결재 1차 미결함
        model.addAttribute("app1List", courseAccountService.getListTop5ByApprUserId1AndIsAppr1(SessionUtil.getUserId(), "N"));

        // 교육결재 1차 완결함
        model.addAttribute("app1CommitList", courseAccountService.getCustomListTop5ByUserIdAndIsCommit(SessionUtil.getUserId(), "0"));



        // 교육결재 1차 결재자 미결건
        int courseApprCount = courseAccountService.getListByAppr1Process("Y",   SessionUtil.getUserId(), "N").size();

        // 교육결재 내가 결재라인에 있으면서 진행중인 건
        int courseProcessCount = courseAccountService.getCustomListByUserIdAndIsCommit(SessionUtil.getUserId(), "0").size();

        // 전자결재 1차 결재자 미결건
        int documentApprCount = documentAccountService.getListByAppr1Process(SessionUtil.getUserId(), "N", "0").size();

        // 전자결재 내가 결재라인에 있으면서 진행중인 건
        int documentProcessCount = documentAccountService.getCustomListByUserIdAndIsCommit(SessionUtil.getUserId(), "0").size();


        // 교육결재 1차 미결건수
        model.addAttribute("courseApprCount", courseApprCount);

        // 교육결재 1차 진행건수
        model.addAttribute("courseProcessCount", courseProcessCount);


        // 전자결재 1차 미결건수
        model.addAttribute("documentApprCount", documentApprCount);

        // 전자결재 1차 진행건수
        model.addAttribute("documentProcessCount", documentProcessCount);


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