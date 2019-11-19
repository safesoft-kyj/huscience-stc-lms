package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.DocumentAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentAccountRepository extends JpaRepository<DocumentAccount, Long> {

    //  status 0:진행중, 1:최종승인, 2:최종기각
    List<DocumentAccount> findByAccount_UserIdAndFStatus(String userId, String status);

    Page<DocumentAccount> findByAccount_UserIdAndFStatus(String userId, String status, Pageable pageable);

    List<DocumentAccount> findByAccount_UserId(String userId);

    List<DocumentAccount> findAllByAccount_UserIdAndIsCommit(String userId, String isCommit);

    List<DocumentAccount> findByDocument_Id(long docId);

    void deleteDocumentAccountByAccount_UserId(String userId);
    void deleteDocumentAccountByDocument_Id(long docId);

    // 내신청함
    Page<DocumentAccount> findByAccount_UserId(String userId, Pageable pageable);


    // 상태별 신청 조회
    List<DocumentAccount> findByFStatus(String status);

    Page<DocumentAccount> findByFStatus(String status, Pageable pageable);


    DocumentAccount findByDocument_IdAndAccount_UserId(long docId, String userId);


}