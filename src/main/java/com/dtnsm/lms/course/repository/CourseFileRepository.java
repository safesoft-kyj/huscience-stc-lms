package com.dtnsm.lms.course.repository;

import com.dtnsm.lms.course.domain.CourseFile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseFileRepository extends JpaRepository<CourseFile, Long> {

}