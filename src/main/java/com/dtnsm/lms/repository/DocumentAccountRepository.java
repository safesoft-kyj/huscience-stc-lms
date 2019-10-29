package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.DocumentAccount;
import com.dtnsm.lms.domain.DocumentAccountId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentAccountRepository extends JpaRepository<DocumentAccount, DocumentAccountId> {

    List<DocumentAccount> findByAccount_UserId(String userId);

    List<DocumentAccount> findByDocument_Id(long documentId);

    DocumentAccount findByDocument_IdAndAccount_UserId(long documentId, String userId);

    DocumentAccount findByDocument_IdAndApprUserId1_UserId(long documentId, String userId);

    DocumentAccount findByDocument_IdAndApprUserId2_UserId(long documentId, String userId);



    void deleteDocumentAccountByAccount_UserId(String userId);
    void deleteDocumentAccountByDocument_Id(long courseId);

    // 내신청함
    Page<DocumentAccount> findByAccount_UserId(String userId, Pageable pageable);

    // 1차 결재자(팀장/부서장)
    Page<DocumentAccount> findByApprUserId1_UserId(String userId, Pageable pageable);

    Page<DocumentAccount> findByApprUserId1_UserIdAndIsAppr1(String userId, String isAppr1, Pageable pageable);

    // 2차 결재자(과정 관리자)
    Page<DocumentAccount> findByApprUserId2_UserId(String userId, Pageable pageable);

    Page<DocumentAccount> findByApprUserId2_UserIdAndIsAppr2(String userId, String isAppr2, Pageable pageable);

}