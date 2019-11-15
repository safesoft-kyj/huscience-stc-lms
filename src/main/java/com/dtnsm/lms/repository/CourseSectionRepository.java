package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

    List<CourseSection> findAllByCourse_Id(long courseId);
}