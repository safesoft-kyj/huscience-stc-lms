package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.DocumentAccount;
import com.dtnsm.lms.service.CourseAccountService;
import com.dtnsm.lms.service.DocumentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApprovalRestController {

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    DocumentAccountService documentAccountService;


    // 결재진행중인 건수
    @GetMapping("/approval")
    public Map<String, String> treeViewGet(@RequestParam("userId") String userId){

        Map<String, String > map = new HashMap<>();


        // 교육결재 1차 결재자 미결건
        List<CourseAccount> courseAccountList = courseAccountService.getListByAppr1Process("Y",   userId, "N");

        // 교육결재 2차 결재자 미결건(1차결재가 완료되고 관리자결재가 Y인 경우)
        List<CourseAccount> courseAccountList3 = courseAccountService.getListByAppr2Process("Y",   userId, "N", "Y");

        // 교육결재 내가 결재라인에 있으면서 진행중인 건
        List<CourseAccount> courseAccountList2 = courseAccountService.getCustomListByUserIdAndIsCommit(userId, "0");




        // 전자결재 1차 결재자 미결건
        List<DocumentAccount> documentAccountList = documentAccountService.getListByAppr1Process(userId, "N", "0");

        // 전자결재 내가 결재라인에 있으면서 진행중인 건
        List<DocumentAccount> documentAccountList2 = documentAccountService.getCustomListByUserIdAndIsCommit(userId, "0");

        // 전자결재 2차 결재자 미결건
        List<DocumentAccount> documentAccountList3 = documentAccountService.getListByAppr2Process("Y", userId, "N", "Y");



        // 1차 결재 미결건
        map.put("courseMyApproval", String.valueOf(courseAccountList.size()));
        // 1차 결재 진행건
        map.put("courseMyProcess", String.valueOf(courseAccountList2.size()));

        // 2차 결재 미결건
        map.put("courseMyApproval2", String.valueOf(courseAccountList3.size()));

        // 전자결재 1차 미결건
        map.put("documentMyApproval", String.valueOf(documentAccountList.size()));
        // 전자결재 1차 진행건
        map.put("documentMyProcess", String.valueOf(documentAccountList2.size()));

        // 전자결재 2차 미결건
        map.put("documentMyApproval2", String.valueOf(documentAccountList3.size()));


        return map;
    }
}
