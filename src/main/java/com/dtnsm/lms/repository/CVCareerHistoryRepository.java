package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVCareerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVCareerHistoryRepository extends JpaRepository<CVCareerHistory, Integer>, QuerydslPredicateExecutor<CVCareerHistory>, CVCareerHistoryRepositoryCustom {
}
