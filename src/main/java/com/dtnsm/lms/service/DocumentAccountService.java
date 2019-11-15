package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.DocumentAccount;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.repository.DocumentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentAccountService {

    @Autowired
    DocumentAccountRepository documentAccountRepository;

    public DocumentAccount save(DocumentAccount documentAccount){
        return documentAccountRepository.save(documentAccount);
    }

    public void delete(DocumentAccount documentAccount) {

        documentAccountRepository.delete(documentAccount);
    }

    public void delete(String userId) {
        documentAccountRepository.deleteDocumentAccountByAccount_UserId(userId);
    }

    public void delete(long courseId) {
        documentAccountRepository.deleteDocumentAccountByDocument_Id(courseId);
    }

    public DocumentAccount getByDocumentIdAndUserId(long documentId, String userId) {
        return documentAccountRepository.findByDocument_IdAndAccount_UserId(documentId, userId);
    }


    public DocumentAccount getByDocumentIdAndApprUserId1(long documentId, String userId) {
        return documentAccountRepository.findByDocument_IdAndApprUserId1_UserId(documentId, userId);
    }

    public DocumentAccount getByDocumentIdAndApprUserId2(long documentId, String userId) {
        return documentAccountRepository.findByDocument_IdAndApprUserId2_UserId(documentId, userId);
    }

    public List<DocumentAccount> getDocumentAccountByCourseId(long documentId) {
        return documentAccountRepository.findByDocument_Id(documentId);
    }

    public List<DocumentAccount> getDocumentAccountByUserId(String userId) {
        return documentAccountRepository.findByAccount_UserId(userId);
    }

    // 내신청함
    public Page<DocumentAccount> getListUserId(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByAccount_UserId(userId, pageable);
    }

    // 1차결재자(팀장/부서장)
    public Page<DocumentAccount> getListApprUserId1(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByApprUserId1_UserId(userId, pageable);
    }

    // 1차결재자(팀장/부서장) 미결, 완결 구분
    public Page<DocumentAccount> getListApprUserId1AndIsAppr1(Pageable pageable, String userId, String isAppr1) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByApprUserId1_UserIdAndIsAppr1(userId, isAppr1, pageable);
    }

    // 2차결재자(과정 관리자) 전체
    public Page<DocumentAccount> getListApprUserId2(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByApprUserId2_UserId(userId, pageable);
    }

    // 2차결재자(과정 관리자) 미결, 완결 구분
    public Page<DocumentAccount> getListApprUserId2AndIsAppr2(Pageable pageable, String userId, String isAppr2) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByApprUserId2_UserIdAndIsAppr2(userId, isAppr2, pageable);
    }


    //진행중 문서
    public Page<DocumentAccount> getCustomByUserIdAndIsCommit(Pageable pageable, String userId, String isCommit) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.getCustomListByUserIdAndIsCommit(userId, isCommit, pageable);
    }

    public List<DocumentAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit) {

        return documentAccountRepository.getCustomListByUserIdAndIsCommit(userId, isCommit);
    }

    //진행중 문서
    public Page<DocumentAccount> getCustomByUserIdAndStatus(Pageable pageable, String userId, ApprovalStatusType status) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.getCustomListByUserIdAndStatus(userId, status, pageable);
    }


    //반려 문서
    public Page<DocumentAccount> getCustomByUserIdAndReject(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.getCustomListByUserIdAndReject(userId, pageable);
    }

    //완결 조회(기각 제외)
    public Page<DocumentAccount> getCustomByUserCommit(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.getCustomListByUserCommit(userId, pageable);
    }

    // 1차 미결건 조회
    public List<DocumentAccount> getListByAppr1Process(String userId, String isAppr1, String isCommit) {

        return documentAccountRepository.findAllByApprUserId1_UserIdAndIsAppr1AndIsCommit(userId, isAppr1, isCommit);
    }

    // 2차 미결건 조회
    public List<DocumentAccount> getListByAppr2Process(String isCourseMangerApproval, String userId, String isAppr2, String isAppr1) {

        return documentAccountRepository.findAllByIsCourseMangerApprovalAndApprUserId2_UserIdAndIsAppr2AndIsAppr1(isCourseMangerApproval, userId, isAppr2, isAppr1);
    }

    public List<DocumentAccount> getCustomListTop5ByUserIdAndIsCommit(String userId, String isCommit) {

        List<DocumentAccount> returnList = new ArrayList<>();

        int i = 0;
        for(DocumentAccount courseAccount : getCustomListByUserIdAndIsCommit(userId, isCommit)) {

            if(i >= 5) break;
            returnList.add(courseAccount);
            i++;
        }

        return returnList;
    }

}
