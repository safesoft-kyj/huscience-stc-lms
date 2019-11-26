package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVExperienceRepository extends JpaRepository<CVExperience, Integer>, QuerydslPredicateExecutor<CVExperience> {
}
