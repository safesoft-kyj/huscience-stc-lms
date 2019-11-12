package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseAccount;
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
import org.springframework.web.bind.annotation.*;

import javax.activation.MimetypesFileTypeMap;

@Controller
@RequestMapping("/approval")
public class ApprovalController {

    @Autowired
    CourseService courseService;

    @Autowired
    CourseAccountService courseAccountService;

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


    // 교육결재(1차 팀장/부서장 전체) 조회
    @GetMapping("/listMyAppr")
    public String listMyAppr(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountService.getListUserId(pageable, userId));

        return "content/approval/listMyAppr";
    }

    // 교육결재(1차 팀장/부서장 전체) 조회
    @GetMapping("/listAppr1")
    public String listAppr1(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
//        model.addAttribute("borders", courseAccountService.getListApprUserId1(pageable, userId));

        model.addAttribute("borders", courseAccountService.getCustomByUserIdAndIsCommit(pageable, userId, "1"));

        return "content/approval/listAppr1";
    }

    // 교육결재(1차 팀장/부서장) 진행함 조회
    @GetMapping("/listAppr1Process")
    public String listAppr1Process(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        // 완결
        model.addAttribute("borders", courseAccountService.getListApprUserId1AndIsAppr1("Y", userId, "N", pageable));

        return "content/approval/listAppr1Process";
    }

    // 교육결재(1차 팀장/부서장) 완료함 조회
    @GetMapping("/listAppr1Commit")
    public String listAppr1Commit(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        // 완결
//        model.addAttribute("borders", courseAccountService.getListApprUserId1AndIsAppr1(pageable, userId, "Y"));

        model.addAttribute("borders", courseAccountService.getCustomByUserIdAndIsCommit(pageable, userId, "0"));


        return "content/approval/listAppr1Commit";
    }


    // 교육결재(1차 팀장/부서장) 승인
    @GetMapping("/successAppr1/{courseId}/{userId}")
    public String successAppr1(@PathVariable("courseId") Long courseId
            , @PathVariable("userId") String userId
            , Model model) {

        // 과정ID와 사용자ID로 과정신청정보를 가지고 온다.
        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, userId);

        // 1차 승인 처리
        approvalCourseProcessService.courseApproval1Proces(courseAccount);

        return "redirect:/approval/listAppr1Commit";
    }

    // 교육결재(1차 팀장/부서장) 기각
    @GetMapping("/rejectAppr1/{courseId}/{userId}")
    public String rejectAppr1(@PathVariable("courseId") Long courseId
            , @PathVariable("userId") String userId
            , Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");

        // 과정ID와 사용자ID로 과정신청정보를 가지고 온다.
        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, userId);

        // 1차 기각 처리
        approvalCourseProcessService.courseReject1Proces(courseAccount);

        model.addAttribute(pageInfo);

        return "redirect:/approval/listAppr1Process";
    }


    // 교육결재(2차 과정 관리자) 조회
    @GetMapping("/listAppr2")
    public String listAppr2(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("전체함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountService.getListApprUserId2(pageable, userId));

        return "content/approval/listAppr2";
    }

    // 교육결재(2차 과정 관리자) 진행함 조회
    @GetMapping("/listAppr2Process")
    public String listAppr2Process(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        // 완결
        model.addAttribute("borders", courseAccountService.getListApprUserId2AndIsAppr2(pageable, userId, "N"));

        return "content/approval/listAppr2Process";
    }

    // 교육결재(2차 과정 관리자) 완료함 조회
    @GetMapping("/listAppr2Commit")
    public String listAppr2Commit(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        // 완결
        model.addAttribute("borders", courseAccountService.getListApprUserId2AndIsAppr2(pageable, userId, "Y"));

        return "content/approval/listAppr2Commit";
    }

    // 교육결재(2차 과정 관리자) 승인
    @GetMapping("/successAppr2/{courseId}/{userId}")
    public String successAppr2(@PathVariable("courseId") Long courseId
            , @PathVariable("userId") String userId
            , Model model) {

        // 세션 아이디를 가지고 온다.
//        String apprUserId = SessionUtil.getUserId();

        // 과정ID와 사용자ID로 과정신청정보를 가지고 온다.
        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, userId);

        // 2차 승인 처리
        approvalCourseProcessService.courseApproval2Proces(courseAccount);

        return "redirect:/approval/listAppr2Commit";
    }

    // 교육결재(2차 과정 관리자) 기각
    @GetMapping("/rejectAppr2/{courseId}/{userId}")
    public String rejectAppr2(@PathVariable("courseId") Long courseId
            , @PathVariable("userId") String userId
            , Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");

        // 과정ID와 사용자ID로 과정신청정보를 가지고 온다.
        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, userId);

        // 2차 승인 처리
        approvalCourseProcessService.courseReject2Proces(courseAccount);

        model.addAttribute(pageInfo);
        // 완결

        return "redirect:/approval/listAppr2";
    }

}

