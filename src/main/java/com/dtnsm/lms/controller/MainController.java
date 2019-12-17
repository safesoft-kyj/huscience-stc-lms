package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.LmsAlarmGubun;
import com.dtnsm.lms.domain.constant.LmsAlarmType;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.mybatis.service.CourseMapperService;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    CourseAccountOrderService courseAccountOrderService;

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
    DocumentService documentService;

    @Autowired
    DocumentAccountOrderService documentAccountOrderService;

    @Autowired
    LmsNotificationService lmsNotificationService;

    @Autowired
    SignatureService signatureService;

    @Autowired
    BorderMasterService borderMasterService;

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

        model.addAttribute("borders_name", borderMasterService.getById("BA0101").getBorderName());


        // 법규/가이드라인
        model.addAttribute("borders3", borderService.getListTop5ByBorderMasterId("BA0102"));
        model.addAttribute("borders3_name", borderMasterService.getById("BA0102").getBorderName());

        // What's New
        model.addAttribute("borders2", borderService.getListTop5ByBorderMasterId("BA0104"));
        model.addAttribute("borders2_name", borderMasterService.getById("BA0104").getBorderName());

        String userId = SessionUtil.getUserId();

        logger.info("startdate = " + DateUtil.getStringMonthStartDate());
        logger.info("enddate = " + DateUtil.getStringMonthEndDate());

        // 월간 교육신청시작일 목록
        model.addAttribute("monthRequestList"
                , courseService.getCourseTop5ByRequestFromDateBetween(
                        DateUtil.getStringMonthStartDate()
                        , DateUtil.getStringMonthEndDate(), 0));

        // Employee training matrix
        model.addAttribute("schedule", scheduleService.getByIsActive(ScheduleType.MATRIX, 1));


        // 교육신청 미결건
        List<CourseAccountOrder> courseAccountOrders1 = courseAccountOrderService.getAllByNext(userId, "1", "0");

        // 교육신청 진행건
        List<CourseAccount> courseAccountOrders2 = courseAccountService.getAllByStatus(userId, "0");

        // 전자결재 미결건
        List<DocumentAccountOrder> documentAccountOrders1 = documentAccountOrderService.getAllByNext(userId, "1", "0");

        // 전자결재 진행건
        List<Document> documentAccountOrders2 = documentService.getAllByStatus(userId, "0");


        // 교육결재 미결함
        model.addAttribute("app1List", courseAccountOrders1);

        // 교육결재 진행함
        model.addAttribute("app1CommitList", courseAccountOrders2);

        // 전자결재 미결함
        model.addAttribute("app2List", documentAccountOrders1);

        // 전자결재 진행함
        model.addAttribute("app2CommitList", documentAccountOrders2);


        // 교육결재 1차 미결건수
        model.addAttribute("courseApprCount", courseAccountOrders1.size());

        // 교육결재 1차 진행건수
        model.addAttribute("courseProcessCount", courseAccountOrders2.size());


        // 전자결재 1차 미결건수
        model.addAttribute("documentApprCount", documentAccountOrders1.size());

        // 전자결재 1차 진행건수
        model.addAttribute("documentProcessCount", documentAccountOrders2.size());


        // 과정그룹별 과정 수
        model.addAttribute("courseMasterList", courseMasterService.getList());

        // 과정그룹별 과정 수
        model.addAttribute("courseCountVO", courseMapperService.getCourseCount());

        model.addAttribute(pageInfo);

        model.addAttribute("alarmList", lmsNotificationService.getTop5ByUserNotification(SessionUtil.getUserId()));


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