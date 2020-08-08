package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseMaster;
import com.dtnsm.lms.domain.mapper.CourseMasterMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CourseMasterRepository extends JpaRepository<CourseMaster, String>, QuerydslPredicateExecutor<CourseMaster> {
    List<CourseMasterMapper> findAllBy();
}