package com.dtnsm.lms.controller;

import com.dtnsm.lms.service.DocumentAccountOrderService;
import com.dtnsm.lms.service.DocumentAccountService;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/approval/document")
public class ApprovalAdminDocumentController {

    @Autowired
    DocumentAccountService documentAccountService;

    @Autowired
    DocumentAccountOrderService documentAccountOrderService;

    private PageInfo pageInfo = new PageInfo();

    public ApprovalAdminDocumentController() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("마이페이지");
    }

    // 진행함
    @GetMapping("/listApprProcess")
    public String listMyAll(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("진행함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documentAccountService.getAllByStatus("0", pageable));

        return "admin/approval/document/listApprAll";
    }

    // 완료함
    @GetMapping("/listApprComplete")
    public String listMyComplete(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("완료함");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documentAccountService.getAllByStatus("1", pageable));

        return "admin/approval/document/listApprAll";
    }


    // 반려함
    @GetMapping("/listApprReject")
    public String listMyReject(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("반려함");

        String userId = SessionUtil.getUserDetail().getUserId();

        model.addAttribute(pageInfo);

        model.addAttribute("borders", documentAccountService.getAllByStatus("2", pageable));

        return "admin/approval/document/listApprAll";
    }
}

