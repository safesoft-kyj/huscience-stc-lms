package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CurriculumVitae;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CurriculumVitaeRepository extends JpaRepository<CurriculumVitae, Integer>, QuerydslPredicateExecutor<CurriculumVitae> {
}
