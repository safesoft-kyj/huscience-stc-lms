package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseAccountId;
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

    // 미결건중 내가 1차 결재자인 건
    List<CourseAccount> findAllByIsTeamMangerApprovalAndApprUserId1_UserIdAndIsAppr1(String isTeamApproval, String userId, String isAppr1);

    // 미결건중 내가 2차 결재자인 건
    List<CourseAccount> findAllByIsTeamMangerApprovalAndApprUserId2_UserIdAndIsAppr2(String isManagerApproval, String userId, String isAppr2);

    Page<CourseAccount> findAllByIsTeamMangerApprovalAndApprUserId1_UserIdAndIsAppr1(String isTeamApproval, String userId, String isAppr1, Pageable pageable);

    // 내가 1차결재자이면서 승인을 하지않는건(미결건) 5개
    List<CourseAccount> findTOp5ByApprUserId1_UserIdAndIsAppr1(String userId, String isAppr1);



    // 2차 결재자(과정 관리자)
    Page<CourseAccount> findByApprUserId2_UserId(String userId, Pageable pageable);

    Page<CourseAccount> findByApprUserId2_UserIdAndIsAppr2(String userId, String isAppr2, Pageable pageable);

}