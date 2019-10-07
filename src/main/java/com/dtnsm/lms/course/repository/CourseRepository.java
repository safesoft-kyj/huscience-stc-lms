package com.dtnsm.lms.course.repository;

import com.dtnsm.lms.course.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findAllByCourseMaster_Id(String typeId, Pageable pageable);

    List<Course> findAllByTitle(String title);

}