package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.DocumentAccount;
import com.dtnsm.lms.repository.DocumentAccountOrderRepository;
import com.dtnsm.lms.repository.DocumentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentAccountService {

    @Autowired
    DocumentAccountRepository documentAccountRepository;

    @Autowired
    DocumentAccountOrderRepository documentAccountOrderRepository;

    @Autowired
    DocumentAccountOrderService documentAccountOrderService;

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

    public DocumentAccount getOne(long id) {
        return documentAccountRepository.getOne(id);
    }

    public DocumentAccount getByDocumentIdAndUserId(long docId, String userId) {
        return documentAccountRepository.findByDocument_IdAndAccount_UserId(docId, userId);
    }


    public List<DocumentAccount> getDocumentAccountByDocId(long docId) {
        return documentAccountRepository.findByDocument_Id(docId);
    }

    public List<DocumentAccount> getCourseAccountByUserId(String userId) {
        return documentAccountRepository.findByAccount_UserId(userId);
    }

    // 상태별 신청 조회
    public List<DocumentAccount> getAllByStatus(String status) {

        return documentAccountRepository.findByFStatus(status);
    }

    // 상태별 신청 조회
    public Page<DocumentAccount> getAllByStatus(String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByFStatus(status, pageable);
    }

    // 최종 완결(신청결재, 교육)
    public List<DocumentAccount> getCourseAccountIsCommitByUserId(String userId, String isCommit) {
        return documentAccountRepository.findAllByAccount_UserIdAndIsCommit(userId, isCommit);
    }

    // 내신청함
    public Page<DocumentAccount> getListUserId(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByAccount_UserId(userId, pageable);
    }

    //진행중 문서
    public Page<DocumentAccount> getAllByStatus(String userId, String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByAccount_UserIdAndFStatus(userId, status, pageable);
    }

    //진행중 문서
    public List<DocumentAccount> getAllByStatus(String userId, String status) {
        return documentAccountRepository.findByAccount_UserIdAndFStatus(userId, status);
    }


    //완결 조회(기각 제외)
    public Page<DocumentAccount> getCustomByUserCommit(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByAccount_UserIdAndFStatus(userId, "1", pageable);
    }


    public List<DocumentAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit) {

        return documentAccountRepository.findByAccount_UserIdAndFStatus(userId, isCommit);
    }

    // 2차결재자(과정 관리자) 미결, 완결 구분
    public Page<DocumentAccount> getListApprUserId2AndIsAppr2(String isManagerApproval,  String userId, String status, String isAppr1, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentAccountRepository.findByAccount_UserIdAndFStatus(userId, status, pageable);
    }
}
