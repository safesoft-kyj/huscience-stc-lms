package com.dtnsm.lms.repository;

import com.dtnsm.common.entity.JobDescription;
import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Long>, QuerydslPredicateExecutor<Course> {

    Page<Course> findAllByActiveGreaterThan(int active, Pageable pageable);

    Page<Course> findAllByCourseMaster_Id(String typeId, Pageable pageable);

    Page<Course> findAllByCourseMaster_IdAndActiveGreaterThan(String typeId, int active, Pageable pageable);

    // 제목, 내용 검색(status 0은 서비스 전 교육과정)
    Page<Course> findAllByCourseMaster_IdAndTitleLikeOrContentLikeAndActiveGreaterThan(String typeId, String title, String content, int active, Pageable pageable);

    // 내용 검색
    Page<Course> findAllByCourseMaster_IdAndContentLikeAndActiveGreaterThan(String typeId, String content, int active, Pageable pageable);

    // 제목 검색
    Page<Course> findAllByCourseMaster_IdAndTitleLikeAndActiveGreaterThan(String typeId, String title, int active, Pageable pageable);


    Page<Course> findAllByTitleLikeAndActiveGreaterThan(String title, int active, Pageable pageable);

    List<Course> findAllByTitleAndActiveGreaterThan(String title, int active);

    List<Course> findByFromDateBetweenAndActiveGreaterThan(String fromDate, String toDate, int active);

    List<Course> findByRequestFromDateBetweenAndActiveGreaterThan(String fromDate, String toDate, int active);

    List<Course> findTop5ByRequestFromDateBetweenAndActiveGreaterThan(String fromDate, String toDate, int active);

    // 신입사원 필수교육(페이징)
    Page<Course> findAllByIsNewEmpCourse(String isNewEmpCourse, Pageable pageable);

    // 신입사원 필수교육
    List<Course> findAllByIsNewEmpCourse(String isNewEmpCourse);
}