package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CourseManagerRepository extends JpaRepository<CourseManager, String> {


}