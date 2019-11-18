package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long>, QuerydslPredicateExecutor<JobDescription> {

}