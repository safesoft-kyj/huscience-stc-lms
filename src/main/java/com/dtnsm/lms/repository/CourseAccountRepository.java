package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseAccountId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseAccountRepository extends JpaRepository<CourseAccount, CourseAccountId> {

    List<CourseAccount> findByAccount_UserId(String userId);

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

    Page<CourseAccount> findByApprUserId1_UserIdAndIsAppr1(String userId, String isAppr1, Pageable pageable);

    // 2차 결재자(과정 관리자)
    Page<CourseAccount> findByApprUserId2_UserId(String userId, Pageable pageable);

    Page<CourseAccount> findByApprUserId2_UserIdAndIsAppr2(String userId, String isAppr2, Pageable pageable);

}