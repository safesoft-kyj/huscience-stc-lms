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

    void deleteCourseAccountByAccount_UserId(String userId);
    void deleteCourseAccountByCourse_Id(long courseId);

}