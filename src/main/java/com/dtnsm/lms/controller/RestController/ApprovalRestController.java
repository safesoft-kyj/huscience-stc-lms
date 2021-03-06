package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseAccountOrder;
import com.dtnsm.lms.domain.Document;
import com.dtnsm.lms.domain.DocumentAccountOrder;
import com.dtnsm.lms.service.CourseAccountOrderService;
import com.dtnsm.lms.service.CourseAccountService;
import com.dtnsm.lms.service.DocumentAccountOrderService;
import com.dtnsm.lms.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApprovalRestController {

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseAccountOrderService courseAccountOrderService;

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentAccountOrderService documentAccountOrderService;


    // 결재진행중인 건수
    @GetMapping("/approval")
    public Map<String, String> treeViewGet(@RequestParam("userId") String userId){

        Map<String, String > map = new HashMap<>();


        courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(userId, "1", "0", 0);

        // 교육결재 미결건
        List<CourseAccountOrder> courseAccountList = courseAccountOrderService.getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(userId, "1", "0", 0);

        // 교육결재 미결건
        List<CourseAccount> courseAccountList3 = courseAccountService.getAllByStatus("0");

        // 교육결재 진행중인 건
        List<CourseAccount> courseAccountList2 = courseAccountService.getAllByStatus(userId, "0");




        // 전자결재 미결건
        List<DocumentAccountOrder> documentAccountList = documentAccountOrderService.getAllByNext(userId, "1", "0");

        // 전자결재 미결건
        List<Document> documentAccountList3 = documentService.getAllByStatus("0");

        // 전자결재 진행중인 건
        List<Document> documentAccountList2 = documentService.getAllByStatus(userId, "0");


        // 사용자별 결재 미결건
        map.put("courseMyApproval", String.valueOf(courseAccountList.size()));
        // 사용자별 결재 진행건
        map.put("courseMyProcess", String.valueOf(courseAccountList2.size()));

        // 관리자 진행중 건
        map.put("courseMyProcess2", String.valueOf(courseAccountList3.size()));

        // 전자결재 1차 미결건
        map.put("documentMyApproval", String.valueOf(documentAccountList.size()));
        // 전자결재 1차 진행건
        map.put("documentMyProcess", String.valueOf(documentAccountList2.size()));

        // 전자결재 2차 미결건
        map.put("documentMyProcess2", String.valueOf(documentAccountList3.size()));


        return map;
    }
}
