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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
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

    @Value("${site.code}")
    private String siteCode;

    @Value("${site.link}")
    private String siteLink;

    @Value("${site.image-logo}")
    private String imageLogo;

    @Value("${site.login-image}")
    private String loginImage;

    @Value("${meta.keywords}")
    private String keywords;

    @Value("${meta.description}")
    private String description;

    private PageInfo pageInfo = new PageInfo();

    public MainController() {
        pageInfo.setParentId("m-base-home");
        pageInfo.setParentTitle("????????????");
    }

    @GetMapping("/")
    public String root(Model model) {
        String userId = SessionUtil.getUserId();
        String userType = SessionUtil.getUserDetail().getUser().getUserType();
        String redirectURL = "/";

        if (userType.equals("U") || userId.equals("admin")) {
            redirectURL = "/home";
        } else {
            redirectURL = "/homeOther";
        }

        return String.format("redirect:%s", redirectURL);
    }

    /**
     * ???????????? ?????? ??????
     * @param
     * @return
     * @exception
     * @see
     */
    @GetMapping("/home")
    public String home(Model model) {

        model.addAttribute(pageInfo);
        model.addAttribute("customers", customerService.getCustomerList());

        // ????????????
        model.addAttribute("borders1", borderService.getListTop5ByBorderMasterId("BA0101"));
        model.addAttribute("borders1_name", borderMasterService.getById("BA0101").getBorderName());

        // ????????????
        model.addAttribute("borders2", borderService.getListTop5ByBorderMasterId("BA0102"));
        model.addAttribute("borders2_name", borderMasterService.getById("BA0102").getBorderName());

        // ???????????????
        model.addAttribute("borders3", borderService.getListTop5ByBorderMasterId("BA0103"));
        model.addAttribute("borders3_name", borderMasterService.getById("BA0103").getBorderName());

        String userId = SessionUtil.getUserId();

//        log.info("startdate = " + DateUtil.getStringMonthStartDate());
//        log.info("enddate = " + DateUtil.getStringMonthEndDate());

        // ?????? ????????????????????? ??????
        model.addAttribute("monthRequestList"
                , courseService.getCourseTop5ByRequestFromDateBetween(
                        DateUtil.getStringMonthStartDate()
                        , DateUtil.getStringMonthEndDate(), 0));

        // Employee training matrix
        model.addAttribute("schedule", scheduleService.getByIsActive(ScheduleType.MATRIX, 1));


        // ????????? ?????? ?????? ??????
        long appRequest = courseAccountOrderService.countByCourseOrderRequest(
                userId, "1", "0", "0", 0);

        appRequest += documentAccountOrderService.countByDocumentRequest(
                userId, "1", "0", "0", 0);

        // ????????????????????? ?????? ??????
        long documentAppRequest = courseAccountService.countByCourseRequest(
                userId, "BC0104","1","1", "%", "1", "9");


        // ????????? ?????? ?????? ??????
        model.addAttribute("appRequest", appRequest);

        // ????????????????????? ?????? ??????
        model.addAttribute("documentAppRequest", documentAppRequest);


        // ???????????? ?????????
        List<CourseAccountOrder> courseAccountOrders1 = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(userId, "1", "0", 0);

        // ???????????? ?????????
        List<CourseAccount> courseAccountOrders2 = courseAccountService.getAllByStatus(userId, "0");

        // ???????????? ?????????
        List<DocumentAccountOrder> documentAccountOrders1 = documentAccountOrderService.getAllByNext(userId, "1", "0");

        // ???????????? ?????????
        List<Document> documentAccountOrders2 = documentService.getAllByStatus(userId, "0");


        // ???????????? ?????????
        model.addAttribute("app1List", courseAccountOrders1);

        // ???????????? ?????????
        model.addAttribute("app1CommitList", courseAccountOrders2);

        // ???????????? ?????????
        model.addAttribute("app2List", documentAccountOrders1);

        // ???????????? ?????????
        model.addAttribute("app2CommitList", documentAccountOrders2);





        // ???????????? 1??? ????????????
        model.addAttribute("courseApprCount", courseAccountOrders1.size());

        // ???????????? 1??? ????????????
        model.addAttribute("courseProcessCount", courseAccountOrders2.size());


        // ???????????? 1??? ????????????
        model.addAttribute("documentApprCount", documentAccountOrders1.size());

        // ???????????? 1??? ????????????
        model.addAttribute("documentProcessCount", documentAccountOrders2.size());


//        // ??????????????? ?????? ???
//        model.addAttribute("courseMasterList", courseMasterService.getList());
//
//        // ??????????????? ?????? ???
//        model.addAttribute("courseCountVO", courseMapperService.getCourseCount());

        model.addAttribute("alarmList", lmsNotificationService.getTop5ByUserNotification(userId));


        return "content/home";
    }

    /**
     * ??????????????? ?????? ??????
     * @param
     * @return
     * @exception
     * @see
     */
    @GetMapping("/homeOther")
    public String homeOhter(Model model) {

        String userId = SessionUtil.getUserId();

        // ????????????
        model.addAttribute(pageInfo);
        model.addAttribute("alarmList", lmsNotificationService.getTop5ByUserNotification(userId));

        // ????????????
        model.addAttribute("borders1", borderService.getListTop5ByBorderMasterId("BA0101"));
        model.addAttribute("borders1_name", borderMasterService.getById("BA0101").getBorderName());

        // ????????????
        model.addAttribute("borders2", borderService.getListTop5ByBorderMasterId("BA0102"));
        model.addAttribute("borders2_name", borderMasterService.getById("BA0102").getBorderName());

        // ???????????????
        model.addAttribute("borders3", borderService.getListTop5ByBorderMasterId("BA0103"));
        model.addAttribute("borders3_name", borderMasterService.getById("BA0103").getBorderName());

        return "content/homeOther";
    }


    @GetMapping("/login")
    public String login(Authentication authentication, Model model) {

        if(ObjectUtils.isEmpty(authentication)) {
            model.addAttribute("imageLogo", imageLogo);
            model.addAttribute("siteLink", siteLink);
            model.addAttribute("loginImage", loginImage);
            model.addAttribute("siteCode", siteCode);
            model.addAttribute("description", description);
            model.addAttribute("keywords", keywords);

            return "login-new";
        } else {
            return "redirect:/notice";
        }
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

        return "redirect:/login?logout";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }

}