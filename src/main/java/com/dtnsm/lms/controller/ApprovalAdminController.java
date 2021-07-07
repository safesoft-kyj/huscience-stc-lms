package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.repository.CourseSectionSetupRepository;
import com.dtnsm.lms.service.CourseAccountOrderService;
import com.dtnsm.lms.service.CourseAccountService;
import com.dtnsm.lms.service.DocumentService;
import com.dtnsm.lms.util.GlobalUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/approval")
public class ApprovalAdminController {

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseAccountOrderService courseAccountOrderService;

    @Autowired
    DocumentService documentService;

    @Autowired
    CourseSectionSetupRepository courseSectionSetupRepository;

    private PageInfo pageInfo = new PageInfo();

    public ApprovalAdminController() {
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

        // 보여줄 내부교육(Self, Class 교육)
        List<String> typeList = new ArrayList<>();
        typeList.add("BC0101"); // Self
        typeList.add("BC0102"); // Class
//        typeList.add("BC0103"); // 부서별

        long requestCount1 = courseAccountService.countByCourseRequest2(
                "%", typeList,"1","9", "0", "0", "9");

        long requestCount2 = courseAccountService.countByCourseRequest2(
                "%", typeList,"1","0", "1", "0", "9");

//        long requestCount3 = courseAccountService.countByCourseRequest(
//                userId, "BC0102","1","1", "%", "0", "9");

//        long requestCount4 = courseAccountService.countByCourseRequest(
//                userId, "BC0102","1","2", "%", "0", "9");

//        long requestCount5 = courseAccountService.countByCourseRequest(
//                userId, "BC0102","1","%", "%", "0", "9");



        // 교육 결재 상태(status) : 0: 진행중, 1: 승인, 2:기각, 9:미진행
        if (fStatus.equals("request")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdInAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", typeList, "1","9", "0", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "9", pageable);
        } else if (fStatus.equals("process")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdInAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", typeList,"1","0", "1", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "0", pageable);
        } else if(fStatus.equals("complete")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdInAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", typeList,"1","1", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "1", pageable);
        } else if (fStatus.equals("reject")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdInAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", typeList,"1","2", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdInAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", typeList,"1","%", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "%", pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("status", fStatus);
        model.addAttribute("requestName", "mainRequest1");
        // 요청중 문서
        model.addAttribute("requestCount1", requestCount1);
        // 진행중 문서
        model.addAttribute("requestCount2", requestCount2);
//        model.addAttribute("requestCount3", requestCount3);
//        model.addAttribute("requestCount4", requestCount4);
//        model.addAttribute("requestCount5", requestCount5);
        model.addAttribute("borders", courseAccountList);

        return "admin/approval/mainRequest1";
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
                "%", "BC0104", "1", "9", "0", "1", "9");

        long requestCount2 = courseAccountService.countByCourseRequest(
                "%", "BC0104", "1", "0", "1", "1", "9");


        long requestCount5 = courseAccountService.countByCourseRequest(
                "%", "BC0104", "1", "1", "%", "1", "1");

        // 교육 결재 상태(status) : 0: 진행중, 1: 승인, 2:기각, 9:미진행
        if (fStatus.equals("request")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", "BC0104", "1", "9", "0", "1", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "9", pageable);
        } else if (fStatus.equals("process")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", "BC0104", "1", "0", "1", "1", "9", pageable);
//            courseAccountList2 = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
//                    userId, "BC0104", "1","1", "1", "1", "0", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "0", pageable);
        } else if (fStatus.equals("complete")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", "BC0104", "1", "1", "%", "1", "1", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "1", pageable);
        } else if (fStatus.equals("reject")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", "BC0104", "1", "2", "%", "1", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else if (fStatus.equals("report")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", "BC0104", "1", "1", "%", "1", "1", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    "%", "BC0104", "1", "%", "%", "1", "%", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "%", pageable);
        }
        model.addAttribute(pageInfo);
        model.addAttribute("status", fStatus);
        model.addAttribute("requestName", "mainRequest2");
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

        return "admin/approval/mainRequest2";
    }


    // 교육신청 결재현황
    @GetMapping("/{requestName}/approvalCourse/{id}")
    public String approvalCourse(@PathVariable("requestName") String requestName
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status
            , @PathVariable("id") long id, Model model) {

        CourseAccount courseAccount = courseAccountService.getById(id);

        pageInfo.setPageTitle(courseAccount.getCourse().getCourseMaster().getCourseName());

        model.addAttribute(pageInfo);
        model.addAttribute("course", courseAccount.getCourse());
        model.addAttribute("courseAccount", courseAccount);


        return "admin/approval/approvalCourse";
    }

    // 참석보고서 결재현황
    @GetMapping("/{requestName}/approvalDocument/{id}")
    public String approvalDocument(@PathVariable("requestName") String requestName
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status
            , @PathVariable("id") long docId, Model model) {

        Document document = documentService.getById(docId);

        pageInfo.setPageTitle(document.getTemplate().getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("document", document);
//        model.addAttribute("signature", GlobalUtil.getSignature(signatureRepository, SessionUtil.getUserId()));

        return "admin/approval/approvalDocument";
    }

    // 진행함
    @GetMapping("/listApprProcess")
    public String listMyAll(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountService.getAllByStatus("0", pageable));

        return "admin/approval/listApprAll";
    }

    // 완료함
    @GetMapping("/listApprComplete")
    public String listMyComplete(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountService.getAllByStatus("1", pageable));

        return "admin/approval/listApprAll";
    }


    // 반려함
    @GetMapping("/listApprReject")
    public String listMyReject(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("반려함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        model.addAttribute("borders", courseAccountService.getAllByStatus("2", pageable));

        return "admin/approval/listApprAll";
    }

    @GetMapping("/classTrainingRetest")
    public String myClassroomView(Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("Class Training 재응시 결재");

        List<CourseAccount> courseAccountList = courseAccountService.getAllByTestFail(true);

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccountList", courseAccountList);

        return "admin/approval/classTrainingRetest";
    }

    @GetMapping("/classTrainingRetest/retest")
    public String myClassroomView(@RequestParam("docId") Long docId) {

        CourseAccount courseAccount = courseAccountService.getById(docId);

        if(courseAccount.getCourse().getCourseMaster().getId().equals("BC0102")) // class training
        {
            courseAccount.setTestFail(false);
            courseAccountService.save(courseAccount);
        }

        return "redirect:";
    }
}
