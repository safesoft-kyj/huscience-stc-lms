package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseAccountId;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CourseAccountRepository extends JpaRepository<CourseAccount, CourseAccountId> {

    List<CourseAccount> findByAccount_UserId(String userId);

    List<CourseAccount> findAllByAccount_UserIdAndIsCommit(String userId, String isCommit);

    List<CourseAccount> findByCourse_Id(long courseId);

    CourseAccount findByCourse_IdAndAccount_UserId(long courseId, String userId);

    CourseAccount findByCourse_IdAndApprUserId1_UserId(long courseId, String userId);

    CourseAccount findByCourse_IdAndApprUserId2_UserId(long courseId, String userId);


    void deleteCourseAccountByAccount_UserId(String userId);
    void deleteCourseAccountByCourse_Id(long courseId);

    // 내신청함
    Page<CourseAccount> findByAccount_UserId(String userId, Pageable pageable);

    // 1차 결재자(팀장/부서장)
    Page<CourseAccount> findByApprUserId1_UserId(String userId, Pageable pageable);

    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = :isCommit")
    Page<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit, Pageable pageable);

    // 내가 결재라인에 속하였지만 진행중인 건
    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = :isCommit")
    List<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit);

    // 내가 결재라인에 속한 경우 상태값 조회(pasing)
    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.approvalStatus = :status")
    Page<CourseAccount> getCustomListByUserIdAndStatus(String userId, ApprovalStatusType status, Pageable pageable);

    // 내가 결재라인에 속한 경우 상태값 조회 건
    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.approvalStatus = :status")
    List<CourseAccount> getCustomListByUserIdAndStatus(String userId, ApprovalStatusType status);

    // 내가 결재라인에 속한 반려 조회(pasing)
    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.approvalStatus in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
    Page<CourseAccount> getCustomListByUserIdAndReject(String userId, Pageable pageable);

    // 내가 결재라인에 속한 반려 조회 건
    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.approvalStatus in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
    List<CourseAccount> getCustomListByUserIdAndReject(String userId);

    // 승인 완료된 문서 조회(기각 제외)
    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = '1' and g.approvalStatus not in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
    Page<CourseAccount> getCustomListByUserCommit(String userId, Pageable pageable);

    // 승인 완료된 문서 조회(기각 제외)
    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = '1' and g.approvalStatus not in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
    List<CourseAccount> getCustomListByUserCommit(String userId);


    // 미결건중 내가 1차 결재자인 건
    List<CourseAccount> findAllByIsTeamMangerApprovalAndApprUserId1_UserIdAndIsAppr1(String isTeamApproval, String userId, String isAppr1);

    // 미결건중 내가 1차 결재자인 건(페이징)
    Page<CourseAccount> findAllByIsTeamMangerApprovalAndApprUserId1_UserIdAndIsAppr1(String isTeamApproval, String userId, String isAppr1, Pageable pageable);

    // 미결건중 내가 2차 결재자인 건
    List<CourseAccount> findAllByIsCourseMangerApprovalAndApprUserId2_UserIdAndIsAppr2AndIsAppr1(String isManagerApproval, String userId, String isAppr2, String isAppr1);

    // 미결건중 내가 2차 결재자인 건(페이징)
    Page<CourseAccount> findAllByIsCourseMangerApprovalAndApprUserId2_UserIdAndIsAppr2AndIsAppr1(String isManagerApproval, String userId, String isAppr2, String isAppr1, Pageable pageable);

    // 내가 1차결재자이면서 승인을 하지않는건(미결건) 5개
    List<CourseAccount> findTOp5ByApprUserId1_UserIdAndIsAppr1(String userId, String isAppr1);



    // 2차 결재자(과정 관리자)
    Page<CourseAccount> findByApprUserId2_UserId(String userId, Pageable pageable);

    Page<CourseAccount> findByApprUserId2_UserIdAndIsAppr2(String userId, String isAppr2, Pageable pageable);

}