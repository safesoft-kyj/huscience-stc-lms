package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.CourseAccountOrder;
import com.dtnsm.lms.domain.DocumentAccountOrder;
import com.dtnsm.lms.service.ApprovalDocumentProcessService;
import com.dtnsm.lms.service.DocumentAccountOrderService;
import com.dtnsm.lms.service.DocumentService;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/approval/document")
public class ApprovalDocumentController {

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentService documentAccountService;

    @Autowired
    DocumentAccountOrderService documentAccountOrderService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private ApprovalDocumentProcessService approvalDocumentProcessService;

    private PageInfo pageInfo = new PageInfo();

    public ApprovalDocumentController() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("전자결재");
    }


    // 미결함
    @GetMapping("/listApprProcess")
    public String listApprProcess(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("미결함");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documentAccountOrderService.getAllByNext(SessionUtil.getUserId(), "1", "0", pageable));

        return "content/approval/document/listApprProcess";
    }


    // 결재함 조회
    @GetMapping("/listApprAll")
    public String listApprAll(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("결재함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documentAccountOrderService.getAllByUserApproval(SessionUtil.getUserId(), pageable));

        return "content/approval/document/listApprAll";
    }

    // 내전체함
    @GetMapping("/listMyAll")
    public String listMyAll(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("내전체함");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documentAccountService.getListUserId(SessionUtil.getUserId(), pageable));

        return "content/approval/document/listMyAll";
    }

    // 내완료함
    @GetMapping("/listMyComplete")
    public String listMyComplete(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documentAccountService.getAllByStatus(SessionUtil.getUserId(),"1", pageable));

        return "content/approval/document/listMyComplete";
    }

    // 교육결재 진행함 조회
    @GetMapping("/listMyProcess")
    public String listMyProcess(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documentAccountService.getAllByStatus(SessionUtil.getUserId(), "0", pageable));

        return "content/approval/document/listMyProcess";
    }

    // 교육결재 반려함 조회
    @GetMapping("/listMyReject")
    public String listMyReject(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("반려함");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documentAccountService.getAllByStatus(SessionUtil.getUserId(),"2", pageable));

        return "content/approval/document/listMyReject";
    }


    // 교육신청 승인
    @GetMapping("/successAppr1/{orderId}")
    public String successAppr1(@PathVariable("orderId") Long orderId, Model model) {

        DocumentAccountOrder documentAccountOrder = documentAccountOrderService.getById(orderId);

        // 승인 처리
        approvalDocumentProcessService.documentApproval1Proces(documentAccountOrder);

//        return "redirect:/approval/document/listApprProcess";
        return "redirect:/approval/mainApproval?status=request";
    }

    // 교육보고서 반려
    @GetMapping("/rejectAppr1/{orderId}")
    public String rejectAppr1(@PathVariable("orderId") Long orderId, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육보고서 반려");

        DocumentAccountOrder  documentAccountOrder = documentAccountOrderService.getById(orderId);

        // 1차 기각 처리
        approvalDocumentProcessService.documentReject1Proces(documentAccountOrder);

        model.addAttribute(pageInfo);

//        return "redirect:/approval/document/listApprProcess";
        return "redirect:/approval/mainApproval?status=request";
    }

    // 교육보고서 반려
    @PostMapping("/rejectAppr1/{orderId}")
    public String rejectAppr1(@PathVariable("orderId") Long orderId
            , @RequestParam(value = "rejectMemo", defaultValue = "") String rejectMemo
            , Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육보고서 반려");

        DocumentAccountOrder  documentAccountOrder = documentAccountOrderService.getById(orderId);
        documentAccountOrder.setFnComment(rejectMemo);

        // 1차 기각 처리
        approvalDocumentProcessService.documentReject1Proces(documentAccountOrder);

        model.addAttribute(pageInfo);

//        return "redirect:/approval/document/listApprProcess";
        return "redirect:/approval/mainApproval?status=request";
    }
}

