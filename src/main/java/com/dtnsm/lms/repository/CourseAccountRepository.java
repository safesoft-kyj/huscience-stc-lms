package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface CourseAccountRepository extends JpaRepository<CourseAccount, Long>, QuerydslPredicateExecutor<CourseAccount>, CourseAccountCustomRepository {




    //  status 0:진행중, 1:최종승인, 2:최종기각, 9:결재없음

    List<CourseAccount> findByAccount_UserIdAndIsApprovalAndFnStatusLikeAndRequestTypeLike(String userId, String isApproval, String status, String requestType);

    Page<CourseAccount> findByAccount_UserIdAndIsApprovalAndFnStatusLikeAndRequestTypeLike(String userId, String isApproval, String status, String requestType, Pageable pageable);

    Page<CourseAccount> findByAccount_UserIdAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(String userId, String isApproval, String status, String requestType, String isReport, String reportStatus, Pageable pageable);

    List<CourseAccount> findByAccount_UserIdAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(String userId, String isApproval, String status, String requestType, String isReport, String reportStatus);



    Page<CourseAccount> findByAccount_UserIdLikeAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(String userId, String typeId, String isApproval, String status, String requestType, String isReport, String reportStatus, Pageable pageable);

    List<CourseAccount> findByAccount_UserIdLikeAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(String userId, String typeId, String isApproval, String status, String requestType, String isReport, String reportStatus);

    long countByAccount_UserIdLikeAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(String userId, String typeId, String isApproval, String status, String requestType, String isReport, String reportStatus);

    List<CourseAccount> findByAccount_UserIdAndFnStatus(String userId, String status);

    Page<CourseAccount> findByAccount_UserIdAndFnStatus(String userId, String status, Pageable pageable);

    List<CourseAccount> findByAccount_UserId(String userId);

    List<CourseAccount> findAllByAccount_UserIdAndIsCommit(String userId, String isCommit);

    List<CourseAccount> findByCourse_Id(long courseId);

    CourseAccount findByCourse_IdAndAccount_UserId(long courseId, String userId);

    CourseAccount findByCourse_IdAndAccount_UserIdAndRequestType(long courseId, String userId, String requestType);

    CourseAccount findByCourse_IdAndAccount_UserIdAndCourseStatus(long courseId, String userId, CourseStepStatus courseStepStatus);

    void deleteCourseAccountByAccount_UserId(String userId);
    void deleteCourseAccountByCourse_Id(long courseId);


    // 교육과정별 교육 상태
    List<CourseAccount> findByCourseStatus(CourseStepStatus courseStepStatus);

    // 사용자별 교육 일정
    List<CourseAccount> findByAccount_UserIdAndFromDateBetweenAndCourseStatus(String userId, String fromDate, String toDate, CourseStepStatus courseStepStatus);

    // 사용자별 교육 신청 일정
    List<CourseAccount> findByAccount_UserIdAndRequestDateBetweenAndCourseStatus(String userId, String fromDate, String toDate, CourseStepStatus courseStepStatus);



    // Mypage/main
    Page<CourseAccount> findByAccount_UserIdAndCourse_CourseMaster_idLikeAndCourse_TitleLikeAndCourseStatusLike(String userId, String typeId, String title, CourseStepStatus courseStepStatus, Pageable pageable);

    // Mypage/main
    Page<CourseAccount> findByAccount_UserIdAndCourse_CourseMaster_idLikeAndCourse_TitleLike(String userId, String typeId, String title, Pageable pageable);

    // 내신청함
    Page<CourseAccount> findByAccount_UserId(String userId, Pageable pageable);




    // 상태별 신청 조회
    List<CourseAccount> findByFnStatus(String status);

    Page<CourseAccount> findByFnStatus(String status, Pageable pageable);


    // 과정유형, 사용자, 완결여부로 가져오기(전자결재 팦업창에서 사용)
    Page<CourseAccount> findAllByCourse_CourseMaster_IdAndAccount_UserIdAndFnStatusAndIsCommit(String typeId, String userId, String fStatus, String isCommit, Pageable pageable);

    List<CourseAccount> findAllByCourse_CourseMaster_IdAndAccount_UserIdAndFnStatusAndIsCommit(String typeId, String userId, String fStatus, String isCommit);

    Page<CourseAccount> findAllByCourse_CourseMaster_IdAndAccount_UserIdAndIsCommit(String typeId, String userId, String isCommit, Pageable pageable);

    List<CourseAccount> findAllByCourse_CourseMaster_IdAndAccount_UserIdAndIsCommit(String typeId, String userId, String isCommit);


    // 수강생별 증명서 발급 대상 과정
    Page<CourseAccount> findAllByAccount_UserIdAndIsCommitAndCourse_IsCerti(String userId, String isCommit, String isCerti, Pageable pageable);


    //    CourseAccount findByCourse_IdAndApprUserId1_UserId(long courseId, String userId);
//
//    CourseAccount findByCourse_IdAndApprUserId2_UserId(long courseId, String userId);

    // 1차 결재자(팀장/부서장)
//    Page<CourseAccount> findByApprUserId1_UserId(String userId, Pageable pageable);

//    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = :isCommit")
//    Page<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit, Pageable pageable);
//
//    // 내가 결재라인에 속하였지만 진행중인 건
//    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = :isCommit")
//    List<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit);
//
//    // 내가 결재라인에 속한 경우 상태값 조회(pasing)
//    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.approvalStatus = :status")
//    Page<CourseAccount> getCustomListByUserIdAndStatus(String userId, ApprovalStatusType status, Pageable pageable);
//
//    // 내가 결재라인에 속한 경우 상태값 조회 건
//    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.approvalStatus = :status")
//    List<CourseAccount> getCustomListByUserIdAndStatus(String userId, ApprovalStatusType status);
//
//    // 내가 결재라인에 속한 반려 조회(pasing)
//    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.approvalStatus in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
//    Page<CourseAccount> getCustomListByUserIdAndReject(String userId, Pageable pageable);
//
//    // 내가 결재라인에 속한 반려 조회 건
//    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.approvalStatus in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
//    List<CourseAccount> getCustomListByUserIdAndReject(String userId);
//
//    // 승인 완료된 문서 조회(기각 제외)
//    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = '1' and g.approvalStatus not in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
//    Page<CourseAccount> getCustomListByUserCommit(String userId, Pageable pageable);
//
//    // 승인 완료된 문서 조회(기각 제외)
//    @Query("SELECT g FROM CourseAccount g where (g.account.userId = :userId or g.apprUserId1.userId = :userId or g.apprUserId2.userId = :userId) and g.isCommit = '1' and g.approvalStatus not in ('APPROVAL_TEAM_REJECT', 'APPROVAL_MANAGER_REJECT')")
//    List<CourseAccount> getCustomListByUserCommit(String userId);


    // 미결건중 내가 1차 결재자인 건
//    List<CourseAccount> findAllByIsTeamMangerApprovalAndApprUserId1_UserIdAndIsAppr1(String isTeamApproval, String userId, String isAppr1);

    // 미결건중 내가 1차 결재자인 건(페이징)
//    Page<CourseAccount> findAllByIsTeamMangerApprovalAndApprUserId1_UserIdAndIsAppr1(String isTeamApproval, String userId, String isAppr1, Pageable pageable);

    // 미결건중 내가 2차 결재자인 건
//    List<CourseAccount> findAllByIsCourseMangerApprovalAndApprUserId2_UserIdAndIsAppr2AndIsAppr1(String isManagerApproval, String userId, String isAppr2, String isAppr1);

    // 미결건중 내가 2차 결재자인 건(페이징)
//    Page<CourseAccount> findAllByIsCourseMangerApprovalAndApprUserId2_UserIdAndIsAppr2AndIsAppr1(String isManagerApproval, String userId, String isAppr2, String isAppr1, Pageable pageable);

    // 내가 1차결재자이면서 승인을 하지않는건(미결건) 5개
//    List<CourseAccount> findTOp5ByApprUserId1_UserIdAndIsAppr1(String userId, String isAppr1);



    // 2차 결재자(과정 관리자)
//    Page<CourseAccount> findByApprUserId2_UserId(String userId, Pageable pageable);
//
//    Page<CourseAccount> findByApprUserId2_UserIdAndIsAppr2(String userId, String isAppr2, Pageable pageable);

}