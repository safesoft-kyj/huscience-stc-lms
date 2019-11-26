package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVPhaseRepository extends JpaRepository<CVPhase, Integer>, QuerydslPredicateExecutor<CVPhase> {
}
