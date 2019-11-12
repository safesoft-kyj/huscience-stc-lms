package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.DocumentAccount;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
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
import java.util.Date;

@Controller
@RequestMapping("/approval/document")
public class ApprovalDocumentController {

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentAccountService documnetAccountService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    private CourseSectionFileService courseSectionFileService;


    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();


    public ApprovalDocumentController() {
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
        model.addAttribute("borders", documnetAccountService.getListUserId(pageable, userId));

        return "content/approval/document/listMyAppr";
    }

    // 교육결재(1차 팀장/부서장 전체) 조회
    @GetMapping("/listAppr1")
    public String listAppr1(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documnetAccountService.getListApprUserId1(pageable, userId));

        return "content/approval/document/listAppr1";
    }

    // 교육결재(1차 팀장/부서장) 진행함 조회
    @GetMapping("/listAppr1Process")
    public String listAppr1Process(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        // 완결
        model.addAttribute("borders", documnetAccountService.getListApprUserId1AndIsAppr1(pageable, userId, "N"));

        return "content/approval/document/listAppr1Process";
    }

    // 교육결재(1차 팀장/부서장) 완료함 조회
    @GetMapping("/listAppr1Commit")
    public String listAppr1Commit(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        // 완결
        model.addAttribute("borders", documnetAccountService.getCustomByUserIdAndIsCommit(pageable, userId, "0"));

        return "content/approval/document/listAppr1Commit";
    }


    // 교육결재(1차 팀장/부서장) 승인
    @GetMapping("/successAppr1/{documentId}/{userId}")
    public String successAppr1(@PathVariable("documentId") Long documentId
            , @PathVariable("userId") String userId
            , Model model) {

        // 과정ID와 사용자ID로 과정신청정보를 가지고 온다.
        DocumentAccount documentAccount = documnetAccountService.getByDocumentIdAndUserId(documentId, userId);

        // 과정 승인 처리한다.
        // isAppr1 = 'Y'
        // apprDate1 = today
        // status = ApprovalStatusType.APPROVAL_TEAM_DONE

        documentAccount.setIsAppr1("Y");
        documentAccount.setApprDate1(DateUtil.getTodayString());
        documentAccount.setApprDateTime1(new Date());
        documentAccount.setStatus(ApprovalStatusType.APPROVAL_TEAM_DONE);

        // 최종승인이 팀장인 경우 상태를 종결한다.
        if(documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getIsCourseMangerApproval().equals("N")) documentAccount.setIsCommit("1");

        documnetAccountService.save(documentAccount);

        return "redirect:/approval/document/listAppr1Commit";
    }

    // 교육결재(1차 팀장/부서장) 기각
    @GetMapping("/rejectAppr1/{documentId}/{userId}")
    public String rejectAppr1(@PathVariable("documentId") Long documentId
            , @PathVariable("userId") String userId
            , Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");

        // 과정ID와 사용자ID로 과정신청정보를 가지고 온다.
        DocumentAccount documentAccount = documnetAccountService.getByDocumentIdAndUserId(documentId, userId);

        // 과정 승인 처리한다.
        // isAppr1 = 'Y'
        // apprDate1 = today
        // status = ApprovalStatusType.APPROVAL_TEAM_DONE

        documentAccount.setIsAppr1("Y");
        documentAccount.setApprDate1(DateUtil.getTodayString());
        documentAccount.setApprDateTime1(new Date());
        documentAccount.setStatus(ApprovalStatusType.APPROVAL_TEAM_DONE);

        // 최종승인이 팀장인 경우 상태를 종결한다.
        if(documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getIsCourseMangerApproval().equals("N")) documentAccount.setIsCommit("2");

        model.addAttribute(pageInfo);
        // 완결

        return "redirect:/approval/document/listAppr1Process";
    }


    // 교육결재(2차 과정 관리자) 조회
    @GetMapping("/listAppr2")
    public String listAppr2(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("전체함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documnetAccountService.getListApprUserId2(pageable, userId));

        return "content/approval/document/listAppr2";
    }

    // 교육결재(2차 과정 관리자) 진행함 조회
    @GetMapping("/listAppr2Process")
    public String listAppr2Process(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        // 완결
        model.addAttribute("borders", documnetAccountService.getListApprUserId2AndIsAppr2(pageable, userId, "N"));

        return "content/approval/document/listAppr2Process";
    }

    // 교육결재(2차 과정 관리자) 완료함 조회
    @GetMapping("/listAppr2Commit")
    public String listAppr2Commit(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        // 완결
        model.addAttribute("borders", documnetAccountService.getListApprUserId2AndIsAppr2(pageable, userId, "Y"));

        return "content/approval/document/listAppr2Commit";
    }

    // 교육결재(2차 과정 관리자) 승인
    @GetMapping("/successAppr2/{courseId}/{userId}")
    public String successAppr2(@PathVariable("documentId") Long documentId
            , @PathVariable("userId") String userId
            , Model model) {

        // 과정ID와 사용자ID로 과정신청정보를 가지고 온다.
        DocumentAccount documentAccount = documnetAccountService.getByDocumentIdAndUserId(documentId, userId);

        // 과정 승인 처리한다.
        // isAppr1 = 'Y'
        // apprDate1 = today
        // status = ApprovalStatusType.APPROVAL_MANAGER_DONE

        documentAccount.setIsAppr2("Y");
        documentAccount.setApprDate2(DateUtil.getTodayString());
        documentAccount.setApprDateTime2(new Date());
        documentAccount.setStatus(ApprovalStatusType.APPROVAL_MANAGER_DONE);

        // 최종승인이 팀장인 경우 상태를 종결한다.
        if(documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getIsCourseMangerApproval().equals("Y")) documentAccount.setIsCommit("1");

        documnetAccountService.save(documentAccount);
        return "redirect:/approval/document/listAppr2Commit";
    }

    // 교육결재(2차 과정 관리자) 기각
    @GetMapping("/rejectAppr2/{documentId}/{userId}")
    public String rejectAppr2(@PathVariable("documentId") Long documentId
            , @PathVariable("userId") String userId
            , Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");

        // 과정ID와 사용자ID로 과정신청정보를 가지고 온다.
        DocumentAccount documentAccount = documnetAccountService.getByDocumentIdAndUserId(documentId, userId);

        // 과정 승인 처리한다.
        // isAppr1 = 'Y'
        // apprDate1 = today
        // status = ApprovalStatusType.APPROVAL_MANAGER_DONE

        documentAccount.setIsAppr2("Y");
        documentAccount.setApprDate2(DateUtil.getTodayString());
        documentAccount.setApprDateTime2(new Date());
        documentAccount.setStatus(ApprovalStatusType.APPROVAL_MANAGER_DONE);

        // 최종승인이 팀장인 경우 상태를 종결한다.
        if(documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getIsCourseMangerApproval().equals("Y")) documentAccount.setIsCommit("2");

        model.addAttribute(pageInfo);
        // 완결

        return "redirect:/approval/document/listAppr2";
    }

}

