package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseAccountOrder;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.activation.MimetypesFileTypeMap;

@Controller
@RequestMapping("/approval")
public class ApprovalController {

    @Autowired
    CourseService courseService;

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseAccountOrderService courseAccountOrderService;

    @Autowired
    CourseSectionService courseSectionService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    private CourseSectionFileService courseSectionFileService;

    @Autowired
    private ApprovalCourseProcessService approvalCourseProcessService;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();


    public ApprovalController() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("마이페이지");
    }

    // 미결함
    @GetMapping("/listApprProcess")
    public String listApprProcess(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("미결함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        // 완결
        model.addAttribute("borders", courseAccountOrderService.getAllByNext(userId, "1", "0", pageable));

        return "content/approval/listApprProcess";
    }


    // 결재함 조회
    @GetMapping("/listApprAll")
    public String listApprAll(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("결재함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        // 완결
        model.addAttribute("borders", courseAccountOrderService.getAllByUserApproval(userId, pageable));

        return "content/approval/listApprProcess";
    }

    // 내전체함
    @GetMapping("/listMyAll")
    public String listMyAll(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("내전체함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountService.getListUserId(userId, pageable));

        return "content/approval/listMyAll";
    }

    // 내완료함
    @GetMapping("/listMyComplete")
    public String listMyComplete(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountService.getAllByStatus(userId,"1", pageable));

//        model.addAttribute("borders", courseAccountOrderService.getAllByApproval(userId, pageable));

        return "content/approval/listMyComplete";
    }



    // 교육결재 진행함 조회
    @GetMapping("/listMyProcess")
    public String listMyProcess(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        String userId = SessionUtil.getUserId();

        model.addAttribute(pageInfo);
        // 완결
//        model.addAttribute("borders", courseAccountService.getListApprUserId1AndIsAppr1(pageable, userId, "Y"));

        model.addAttribute("borders", courseAccountService.getAllByStatus(userId, "0", pageable));


        return "content/approval/listMyProcess";
    }

    // 교육결재 반려함 조회
    @GetMapping("/listMyReject")
    public String listMyReject(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("반려함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        // 완결
//        model.addAttribute("borders", courseAccountService.getListApprUserId1AndIsAppr1(pageable, userId, "Y"));

        model.addAttribute("borders", courseAccountService.getAllByStatus(userId,"2", pageable));

        return "content/approval/listMyReject";
    }


    // 교육신청 승인
    @GetMapping("/successAppr1/{orderId}")
    public String successAppr1(@PathVariable("orderId") Long orderId, Model model) {

        CourseAccountOrder courseAccountOrder = courseAccountOrderService.getById(orderId);

        // 승인 처리
        approvalCourseProcessService.courseApproval1Proces(courseAccountOrder);

        return "redirect:/approval/listApprProcess";
    }

    // 교육신청 반려
    @GetMapping("/rejectAppr1/{orderId}")
    public String rejectAppr1(@PathVariable("orderId") Long orderId, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육신청 반려");

        CourseAccountOrder courseAccountOrder = courseAccountOrderService.getById(orderId);

        // 1차 기각 처리
        approvalCourseProcessService.courseReject1Proces(courseAccountOrder);

        model.addAttribute(pageInfo);

        return "redirect:/approval/listApprProcess";
    }


    // 교육결재(2차 과정 관리자) 전체함
    @GetMapping("/listAppr2")
    public String listAppr2(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountService.getCustomByUserCommit(userId, pageable));

        return "content/approval/listAppr2";
    }

    // 교육결재(2차 과정 관리자) 미결함
    @GetMapping("/listAppr2Process")
    public String listAppr2Process(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("미결함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        // 완결
        model.addAttribute("borders", courseAccountService.getListApprUserId2AndIsAppr2("Y", userId, "N", "Y", pageable));

        return "content/approval/listAppr2Process";
    }

    // 교육결재(2차 과정 관리자) 진행함
    @GetMapping("/listAppr2Commit")
    public String listAppr2Commit(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        // 완결
        model.addAttribute("borders", courseAccountService.getAllByStatus(userId, "0", pageable));

        return "content/approval/listAppr2Commit";
    }

    // 교육결재(2차 과정 관리자) 승인
    @GetMapping("/successAppr2/{orderId}")
    public String successAppr2(@PathVariable("orderId") Long orderId, Model model) {

        CourseAccountOrder courseAccountOrder = courseAccountOrderService.getById(orderId);

        // 2차 승인 처리
        approvalCourseProcessService.courseApproval2Proces(courseAccountOrder);

        return "redirect:/approval/listAppr2Commit";
    }

    // 교육결재(2차 과정 관리자) 기각
    @GetMapping("/rejectAppr2/{orderId}")
    public String rejectAppr2(@PathVariable("orderId") Long orderId, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재 기각");

        CourseAccountOrder courseAccountOrder = courseAccountOrderService.getById(orderId);

        // 2차 승인 처리
        approvalCourseProcessService.courseReject2Proces(courseAccountOrder);

        model.addAttribute(pageInfo);
        // 완결

        return "redirect:/approval/listAppr2";
    }

}

