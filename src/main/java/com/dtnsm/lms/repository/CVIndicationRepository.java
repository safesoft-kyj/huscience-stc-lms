package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVIndication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVIndicationRepository extends JpaRepository<CVIndication, Integer>, QuerydslPredicateExecutor<CVIndication> {
}
