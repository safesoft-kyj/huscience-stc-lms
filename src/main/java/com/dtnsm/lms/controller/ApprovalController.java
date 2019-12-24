package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseAccountOrder;
import com.dtnsm.lms.domain.Document;
import com.dtnsm.lms.domain.DocumentAccountOrder;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.activation.MimetypesFileTypeMap;
import java.util.List;
import java.util.Optional;

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
    DocumentService documentService;

    @Autowired
    DocumentAccountOrderService documentAccountOrderService;

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
        pageInfo.setParentTitle("교육결재");
    }

    // 내부결재기안함(Class 교육만 진행한다)
    @GetMapping({"/mainRequest1", "/mainRequest1/{status}"})
    public String mainRequest1(@PathVariable Optional<String> status, @PageableDefault Pageable pageable, Model model) {

        String fStatus = status.isPresent() ? status.get() : "all";

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("내부결재");

        String userId = SessionUtil.getUserDetail().getUserId();
        Page<CourseAccount> courseAccountList = null;


        long requestCount1 = courseAccountService.countByCourseRequest(
                userId, "BC0102","1","9", "0", "0", "9");

        long requestCount2 = courseAccountService.countByCourseRequest(
                userId, "BC0102","1","0", "1", "0", "9");

//        long requestCount3 = courseAccountService.countByCourseRequest(
//                userId, "BC0102","1","1", "%", "0", "9");

//        long requestCount4 = courseAccountService.countByCourseRequest(
//                userId, "BC0102","1","2", "%", "0", "9");

//        long requestCount5 = courseAccountService.countByCourseRequest(
//                userId, "BC0102","1","%", "%", "0", "9");



        // 교육 결재 상태(status) : 0: 진행중, 1: 승인, 2:기각, 9:미진행
        if (fStatus.equals("request")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102", "1","9", "0", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "9", pageable);
        } else if (fStatus.equals("process")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102","1","0", "1", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "0", pageable);
        } else if(fStatus.equals("complete")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102","1","1", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "1", pageable);
        } else if (fStatus.equals("reject")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102","1","2", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102","1","%", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "%", pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("status", fStatus);
        // 요청중 문서
        model.addAttribute("requestCount1", requestCount1);
        // 진행중 문서
        model.addAttribute("requestCount2", requestCount2);
//        model.addAttribute("requestCount3", requestCount3);
//        model.addAttribute("requestCount4", requestCount4);
//        model.addAttribute("requestCount5", requestCount5);
        model.addAttribute("borders", courseAccountList);

        return "content/approval/mainRequest1";
    }

    // 외부결재기안함(외부교육만 진행한다)
    @GetMapping({"/mainRequest2", "/mainRequest2/{status}"})
    public String mainRequest2(@PathVariable Optional<String> status, @PageableDefault Pageable pageable, Model model) {

        String fStatus = status.isPresent() ? status.get() : "all";

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("외부결재");

        String userId = SessionUtil.getUserDetail().getUserId();
        Page<CourseAccount> courseAccountList = null;
        Page<CourseAccount> courseAccountList2 = null;
        Page<Document> documentList = null;

        long requestCount1 = courseAccountService.countByCourseRequest(
                userId, "BC0104","1","9", "0", "1", "9");

        long requestCount2 = courseAccountService.countByCourseRequest(
                userId, "BC0104","1","0", "1", "1", "9");

//        long requestCount3 = courseAccountService.countByCourseRequest(
//                userId, "BC0104","1","1", "%", "1", "1");
//
//        long requestCount4 = courseAccountService.countByCourseRequest(
//                userId, "BC0104","1","2", "%", "1", "2");

        long requestCount5 = courseAccountService.countByCourseRequest(
                userId, "BC0104","1","1", "%", "1", "1");

//        long requestCount6 = courseAccountService.countByCourseRequest(
//                userId, "BC0104","1","%", "%", "1", "%");

        // 교육 결재 상태(status) : 0: 진행중, 1: 승인, 2:기각, 9:미진행
        if (fStatus.equals("request")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","9", "0", "1", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "9", pageable);
        } else if (fStatus.equals("process")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","0", "1", "1", "9", pageable);
//            courseAccountList2 = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
//                    userId, "BC0104", "1","1", "1", "1", "0", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "0", pageable);
        } else if(fStatus.equals("complete")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","1", "%", "1", "1", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "1", pageable);
        } else if (fStatus.equals("reject")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","2", "%", "1", "2", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else if (fStatus.equals("report")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","1", "%", "1", "1", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","%", "%", "1", "%", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "%", pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("status", fStatus);
        // 요청중 문서
        model.addAttribute("requestCount1", requestCount1);
        // 진행중 문서
        model.addAttribute("requestCount2", requestCount2);
//        model.addAttribute("requestCount3", requestCount3);
//        model.addAttribute("requestCount4", requestCount4);
        model.addAttribute("requestCount5", requestCount5);
//        model.addAttribute("requestCount6", requestCount6);
        model.addAttribute("borders", courseAccountList);
        model.addAttribute("borders2", courseAccountList2);

        return "content/approval/mainRequest2";
    }

    // 기안함
    @GetMapping({"/mainApproval", "/mainApproval/{status}"})
    public String mainApproval(@PathVariable Optional<String> status, @PageableDefault Pageable pageable, Model model) {

        String fStatus = status.isPresent() ? status.get() : "all";

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("결재함");

        String userId = SessionUtil.getUserDetail().getUserId();
        List<CourseAccountOrder> courseAccountOrderList = null;
        List<DocumentAccountOrder> documentAccountOrderList = null;

        long requestCount1 = courseAccountOrderService.countByCourseOrderRequest(
                userId, "1", "0", "0", 0);

        requestCount1 += documentAccountOrderService.countByDocumentRequest(
                userId, "1", "0", "0", 0);

        long requestCount2 = courseAccountOrderService.countByCourseOrderRequest(
                userId, "%", "0", "%", 0);

        requestCount2 += documentAccountOrderService.countByDocumentRequest(
                userId, "%", "0", "%", 0);



        // 전자 결재 상태(status) : 0: 진행중, 1: 승인, 2:기각, 9:미진행
        if (fStatus.equals("request")) {
            courseAccountOrderList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "1", "0", "0", 0);

            documentAccountOrderList = documentAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "1", "0", "0", 0);
        } else if (fStatus.equals("process")) {
            courseAccountOrderList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "0", "%", 0);

            documentAccountOrderList = documentAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "0", "%", 0);

        } else if (fStatus.equals("complete")) {
            courseAccountOrderList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                        userId, "%", "1", "%", 0);

            documentAccountOrderList = documentAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "1", "%", 0);
        } else if (fStatus.equals("reject")) {
            courseAccountOrderList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "2", "%",0);

            documentAccountOrderList = documentAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "2", "%",0);

        } else {
            courseAccountOrderList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "%", "%",0);

            documentAccountOrderList = documentAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "%", "%",0);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("status", fStatus);
        // 요청중 문서
        model.addAttribute("requestCount1", requestCount1);
        // 진행중 문서
        model.addAttribute("requestCount2", requestCount2);
        model.addAttribute("borders", courseAccountOrderList);
        model.addAttribute("documents", documentAccountOrderList);

        return "content/approval/mainApproval";
    }


    // 미결함
    @GetMapping("/listApprProcess")
    public String listApprProcess(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("미결함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        // 완결
        model.addAttribute("borders", courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(userId, "1", "0", 0, pageable));

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

        return "content/approval/listApprAll";
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

        return "redirect:/approval/mainApproval/all";
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

        return "redirect:/approval/mainApproval/all";
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

        return "redirect:/approval/mainApproval/all";
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

        return "redirect:/approval/mainApproval/all";
    }

}

