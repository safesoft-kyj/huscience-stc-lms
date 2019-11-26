package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVComputerKnowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVComputerKnowledgeRepository extends JpaRepository<CVComputerKnowledge, Integer>, QuerydslPredicateExecutor<CVComputerKnowledge> {
}
