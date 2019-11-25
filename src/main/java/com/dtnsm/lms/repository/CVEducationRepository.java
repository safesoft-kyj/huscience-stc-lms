package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVEducationRepository extends JpaRepository<CVEducation, Integer>, QuerydslPredicateExecutor<CVEducation> {
}
