package com.dtnsm.lms.controller;

import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.GlobalUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    DocumentTemplateService templateService;

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentAccountOrderService documentAccountOrderService;

    @Autowired
    private DocumentCourseAccountService documentCourseAccountService;

    @Autowired
    private DocumentFileService documentFileService;

    @Autowired
    ApprovalDocumentProcessService approvalDocumentProcessService;

    @Autowired
    CourseSectionService courseSectionService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    SignatureRepository signatureRepository;

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
    @GetMapping("/mainRequest1")
    public String mainRequest1(@RequestParam(value = "status", required = false, defaultValue = "all") String status, @PageableDefault Pageable pageable, Model model) {

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
        if (status.equals("request")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102", "1","9", "0", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "9", pageable);
        } else if (status.equals("process")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102","1","0", "1", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "0", pageable);
        } else if(status.equals("complete")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102","1","1", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "1", pageable);
        } else if (status.equals("reject")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102","1","2", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0102","1","%", "%", "0", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "%", pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("status", status);
        model.addAttribute("requestName", "mainRequest1");
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
    @GetMapping("/mainRequest2")
    public String mainRequest2(@RequestParam(value = "status", required = false, defaultValue = "all") String status, @PageableDefault Pageable pageable, Model model) {

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
                userId, "BC0104","1","1", "%", "1", "9");

//        long requestCount6 = courseAccountService.countByCourseRequest(
//                userId, "BC0104","1","%", "%", "1", "%");

        // 교육 결재 상태(status) : 0: 진행중, 1: 승인, 2:기각, 9:미진행
        if (status.equals("request")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","9", "0", "1", "9", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "9", pageable);
        } else if (status.equals("process")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","0", "1", "1", "9", pageable);
//            courseAccountList2 = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
//                    userId, "BC0104", "1","1", "1", "1", "0", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "0", pageable);
        } else if(status.equals("complete")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","1", "%", "1", "1", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "1", pageable);
        } else if (status.equals("reject")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","2", "%", "1", "2", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else if (status.equals("report")) {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","1", "%", "1", "%", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "2", pageable);
        } else {
            courseAccountList = courseAccountService.getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(
                    userId, "BC0104","1","%", "%", "1", "%", pageable);

//            documentList = documentService.getAllByAccount_UserIdAndFnStatusLike(userId, "%", pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("status", status);
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

        return "content/approval/mainRequest2";
    }

    // 기안함
    @GetMapping("/mainApproval")
    public String mainApproval(@RequestParam(value = "status", required = false, defaultValue = "all") String status, @PageableDefault Pageable pageable, Model model) {

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
        if (status.equals("request")) {
            courseAccountOrderList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "1", "0", "0", 0);

            documentAccountOrderList = documentAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "1", "0", "0", 0);
        } else if (status.equals("process")) {
            courseAccountOrderList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "0", "%", 0);

            documentAccountOrderList = documentAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "0", "%", 0);

        } else if (status.equals("complete")) {
            courseAccountOrderList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                        userId, "%", "1", "%", 0);

            documentAccountOrderList = documentAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(
                    userId, "%", "1", "%", 0);
        } else if (status.equals("reject")) {
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
        model.addAttribute("status", status);
        model.addAttribute("requestName", "mainApproval");
        // 요청중 문서
        model.addAttribute("requestCount1", requestCount1);
        // 진행중 문서
        model.addAttribute("requestCount2", requestCount2);
        model.addAttribute("borders", courseAccountOrderList);
        model.addAttribute("documents", documentAccountOrderList);


        return "content/approval/mainApproval";
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


        return "content/approval/approvalCourse";
    }

    @GetMapping("/{requestName}/approvalCourseOrder/{id}")
    public String approvalCourseOrder(@PathVariable("requestName") String requestName
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status
            , @PathVariable("id") long id, Model model) {

        CourseAccountOrder courseAccountOrder = courseAccountOrderService.getById(id);

        pageInfo.setPageTitle(courseAccountOrder.getCourseAccount().getCourse().getCourseMaster().getCourseName());

        model.addAttribute(pageInfo);
        model.addAttribute("course", courseAccountOrder.getCourseAccount().getCourse());
        model.addAttribute("courseAccount", courseAccountOrder.getCourseAccount());
        model.addAttribute("courseAccountOrder", courseAccountOrder);
        model.addAttribute("userId", SessionUtil.getUserId());

        return "content/approval/approvalCourseOrder";
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

        return "content/approval/approvalDocument";
    }

    // 참석보고서 결재현황
    @GetMapping("/{requestName}/approvalDocumentOrder/{id}")
    public String approvalDocumentOrder(@PathVariable("requestName") String requestName
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status
            , @PathVariable("id") long id, Model model) {

        DocumentAccountOrder documentAccountOrder = documentAccountOrderService.getById(id);

        pageInfo.setPageTitle(documentAccountOrder.getDocument().getTemplate().getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("document", documentAccountOrder.getDocument());
        model.addAttribute("documentAccountOrder", documentAccountOrder);
//        model.addAttribute("signature", GlobalUtil.getSignature(signatureRepository, SessionUtil.getUserId()));
        model.addAttribute("userId", SessionUtil.getUserId());

        return "content/approval/approvalDocumentOrder";
    }

    // 참석보고서 작성
    @GetMapping("/{requestName}/addDocument")
    public String addDocumentAccount(@PathVariable("requestName") String requestName
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status
            , @RequestParam("templateId") int templateId
            , @RequestParam("docId") long docId
            , Model model) {

        DocumentTemplate template = templateService.getById(templateId);

        Document document = new Document();
        document.setTemplate(template);
        document.setTitle("[" + template.getTitle() + "]");
        document.setContent(template.getContent());
        document.setCourseAccount(courseAccountService.getById(docId));

        pageInfo.setPageTitle(template.getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("document", document);
        model.addAttribute("requestName", requestName);
        model.addAttribute("status", status);

        return "content/approval/addDocument";
    }

    // 외부교육 보고서 작성 처리
    @PostMapping("/{requestName}/addDocument-post")
    @Transactional
    public String addAccountPost(@Valid Document document
            , @PathVariable("requestName") String requestName
            , @RequestParam("status") String status
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            //return "document/add/" + document.getTemplate().getId();
            return "redirect:/document/add/" + document.getTemplate().getId() + '/' + document.getCourseAccount().getId();
        }

        DocumentTemplate template = templateService.getById(document.getTemplate().getId());

        Optional<CourseAccount> optCourseAccount = courseAccountService.getId(document.getCourseAccount().getId());

        if(optCourseAccount.isPresent()) {

            CourseAccount courseAccount = optCourseAccount.get();
            // 교육과정의 보고서 상태를 진행상태로 변경한다.
            courseAccount.setReportStatus("0");

            // 상태를 변경하고 저장한다.
            document.setCourseAccount(courseAccountService.save(courseAccount));
        }

        document.setAccount(userService.getAccountByUserId(SessionUtil.getUserId()));
        document.setTemplate(template);
        Document document1 = documentService.save(document);

        if (files.length > 0) {
            Arrays.asList(files)
                    .stream()
                    .map(file -> documentFileService.storeFile(file, document1))
                    .collect(Collectors.toList());
        }

        // 기안 정보 등록
        Account account = userService.findByUserId(SessionUtil.getUserId());
        approvalDocumentProcessService.documentRequestProcess(account, document1);

        // 이전 URL를 리턴한다.
        return String.format("redirect:/approval/%s?status=%s", requestName, status);
    }

    // 참석보고서 수정
    @GetMapping("/{requestName}/editDocument")
    public String editDocumentAccount(@PathVariable("requestName") String requestName
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status
            , @RequestParam("id") long id, Model model) {

        Document document = documentService.getById(id);

        pageInfo.setPageTitle(document.getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("document", document);
        model.addAttribute("requestName", requestName);
        model.addAttribute("status", status);
        model.addAttribute("id", document.getId());

        return "content/approval/editDocument";
    }

    @PostMapping("/{requestName}/editDocument-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @PathVariable("requestName") String requestName
            , @RequestParam("status") String status
            , @Valid Document document
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            document.setId(id);
            return "/content/document/list";
        }

        Document oldDocument = documentService.getById(id);


        if (oldDocument != null) {

            // 다음결재자가 결재를 하지 않은 경우는 수정한다.
            if (oldDocument.getFnCurrSeq() <= 1) {

                // 제목, 내용, 첨부파일만 수정을 허용한다.
                document.setFnStatus(oldDocument.getFnStatus());
                document.setFnCurrSeq(oldDocument.getFnCurrSeq());
                document.setFnFinalCount(oldDocument.getFnFinalCount());
                document.setIsCommit(oldDocument.getIsCommit());
                document.setAccount(oldDocument.getAccount());
                document.setFnWdate(DateUtil.getTodayDate());
                document.setRequestDate(oldDocument.getRequestDate());
                document.setCourseAccount(oldDocument.getCourseAccount());
                document.setDocumentFiles(oldDocument.getDocumentFiles());
                document.setDocumentCourseAccountList(oldDocument.getDocumentCourseAccountList());
                document.setDocumentAccountOrders(oldDocument.getDocumentAccountOrders());

                Document document1 = documentService.save(document);

                Arrays.asList(files)
                        .stream()
                        .map(file -> documentFileService.storeFile(file, document1))
                        .collect(Collectors.toList());

            }
        }

        // 이전 URL를 리턴한다.
        return String.format("redirect:/approval/%s?status=%s", requestName, status);
    }

    @GetMapping("/{requestName}/deleteDocument")
    public String deleteDocument(@PathVariable("requestName") String requestName
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status
            , @RequestParam("id") long id, HttpServletRequest request) {

        Document document = documentService.getById(id);

        // 다음결재자가 결재를 하지 않은 경우는 삭제한다.
        if (document != null) {

            if (document.getFnCurrSeq() <= 1) {

                // 보고서 작성 삭제시 교육과정 상태값을 작성전으로 변경한다.
                CourseAccount courseAccount = document.getCourseAccount();
                courseAccount.setReportStatus("9");
                courseAccountService.save(courseAccount);

                documentService.delete(document);
            }
        }

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
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

        return "redirect:/approval/mainApproval?status=request";
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

        return "redirect:/approval/mainApproval?status=request";
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


    // 팀장 결재전 삭제 기능
    @GetMapping("/deleteCourseAccount/{id}")
    public String noticeDelete(@PathVariable("id") long docId, HttpServletRequest request) {

        CourseAccount courseAccount = courseAccountService.getById(docId);

        if (courseAccount.getAccount().getUserId().equals(SessionUtil.getUserId()) && courseAccount.getFnCurrSeq() <= 1) {
            courseAccountService.delete(courseAccount);
        }

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
    }

}

