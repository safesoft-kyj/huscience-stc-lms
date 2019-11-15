package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.DocumentAccount;
import com.dtnsm.lms.domain.DocumentAccountId;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    // 미결건중 내가 1차 결재자인 건
    List<DocumentAccount> findAllByApprUserId1_UserIdAndIsAppr1AndIsCommit(String userId, String isAppr1, String isCommit);

    // 미결건중 내가 2차 결재자인 건
    List<DocumentAccount> findAllByIsCourseMangerApprovalAndApprUserId2_UserIdAndIsAppr2AndIsAppr1(String isCourseMangerApproval, String userId, String isAppr2, String isAppr1);


    @Query("SELECT g FROM DocumentAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = :isCommit")
    Page<DocumentAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit, Pageable pageable);

    @Query("SELECT g FROM DocumentAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = :isCommit")
    List<DocumentAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit);

    @Query("SELECT g FROM DocumentAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.status = :status")
    Page<DocumentAccount> getCustomListByUserIdAndStatus(String userId, ApprovalStatusType status, Pageable pageable);

    @Query("SELECT g FROM DocumentAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.status = :status")
    List<DocumentAccount> getCustomListByUserIdAndStatus(String userId, ApprovalStatusType status);

    @Query("SELECT g FROM DocumentAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.status in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
    Page<DocumentAccount> getCustomListByUserIdAndReject(String userId, Pageable pageable);

    @Query("SELECT g FROM DocumentAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.status in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
    List<DocumentAccount> getCustomListByUserIdAndReject(String userId);

    // 승인 완료된 문서 조회(기각 제외)
    @Query("SELECT g FROM DocumentAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = '1' and g.status not in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
    Page<DocumentAccount> getCustomListByUserCommit(String userId, Pageable pageable);

    // 승인 완료된 문서 조회(기각 제외)
    @Query("SELECT g FROM DocumentAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = '1' and g.status not in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
    List<DocumentAccount> getCustomListByUserCommit(String userId);

}