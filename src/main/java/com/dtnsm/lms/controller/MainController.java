package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ScheduleType;
import com.dtnsm.lms.mybatis.service.CourseMapperService;
import com.dtnsm.lms.repository.LoginHistoryRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
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

    @Autowired
    LoginHistoryRepository loginHistoryRepository;


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
        model.addAttribute("borders1", borderService.getListTop5ByBorderMasterId("BA0101"));
        model.addAttribute("borders1_name", borderMasterService.getById("BA0101").getBorderName());

        // 법령정보
        model.addAttribute("borders2", borderService.getListTop5ByBorderMasterId("BA0102"));
        model.addAttribute("borders2_name", borderMasterService.getById("BA0102").getBorderName());

        // 가이드라인
        model.addAttribute("borders3", borderService.getListTop5ByBorderMasterId("BA0103"));
        model.addAttribute("borders3_name", borderMasterService.getById("BA0103").getBorderName());

        String userId = SessionUtil.getUserId();

//        log.info("startdate = " + DateUtil.getStringMonthStartDate());
//        log.info("enddate = " + DateUtil.getStringMonthEndDate());

        // 월간 교육신청시작일 목록
        model.addAttribute("monthRequestList"
                , courseService.getCourseTop5ByRequestFromDateBetween(
                        DateUtil.getStringMonthStartDate()
                        , DateUtil.getStringMonthEndDate(), 0));

        // Employee training matrix
        model.addAttribute("schedule", scheduleService.getByIsActive(ScheduleType.MATRIX, 1));


        // 결재함 결재 요청 건수
        long appRequest = courseAccountOrderService.countByCourseOrderRequest(
                userId, "1", "0", "0", 0);

        appRequest += documentAccountOrderService.countByDocumentRequest(
                userId, "1", "0", "0", 0);

        // 교육참석보고서 미결 건수
        long documentAppRequest = courseAccountService.countByCourseRequest(
                userId, "BC0104","1","1", "%", "1", "9");


        // 결재함 결재 요청 건수
        model.addAttribute("appRequest", appRequest);

        // 교육참석보고서 미결 건수
        model.addAttribute("documentAppRequest", documentAppRequest);


        // 교육신청 미결건
        List<CourseAccountOrder> courseAccountOrders1 = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(userId, "1", "0", 0);

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


//        // 과정그룹별 과정 수
//        model.addAttribute("courseMasterList", courseMasterService.getList());
//
//        // 과정그룹별 과정 수
//        model.addAttribute("courseCountVO", courseMapperService.getCourseCount());

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

    @PostMapping("/logout")
    public String logout(Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

//        log.info("====/logout session=== : {}", customUserDetails.getLoginSessionId());

        if (authentication != null && authentication.getDetails() != null) {
            try {

                Optional<LoginHistory> loginHistory = loginHistoryRepository.findById(customUserDetails.getLoginSessionId());

                if(loginHistory.isPresent()) {
                    LoginHistory loginHistory1 = loginHistory.get();
                    loginHistory1.setLogoutDateTime(new Date());
                    loginHistoryRepository.save(loginHistory1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }

}