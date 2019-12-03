package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseManager;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseManagerRepository extends JpaRepository<CourseManager, String> {

    CourseManager findByIsActive(int isActive);

}