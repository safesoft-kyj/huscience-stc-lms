package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CourseMasterRepository extends JpaRepository<CourseMaster, String>, QuerydslPredicateExecutor<CourseMaster> {

}