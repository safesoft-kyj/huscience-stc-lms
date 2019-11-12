package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findAllByCourseMaster_Id(String typeId, Pageable pageable);

    // 제목, 내용 검색
    Page<Course> findAllByCourseMaster_IdAndTitleLikeOrContentLike(String typeId, String title, String content, Pageable pageable);

    // 내용 검색
    Page<Course> findAllByCourseMaster_IdAndContentLike(String typeId, String content, Pageable pageable);

    // 제목 검색
    Page<Course> findAllByCourseMaster_IdAndTitleLike(String typeId, String title, Pageable pageable);

    Page<Course> findAllByTitleLike(String title, Pageable pageable);

    List<Course> findAllByTitle(String title);

    List<Course> findByFromDateBetween(String fromDate, String toDate);

    List<Course> findByRequestFromDateBetween(String fromDate, String toDate);

    List<Course> findTop5ByRequestFromDateBetween(String fromDate, String toDate);

}