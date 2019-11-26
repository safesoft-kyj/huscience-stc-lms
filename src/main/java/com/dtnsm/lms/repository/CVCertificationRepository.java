package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVCertificationRepository extends JpaRepository<CVCertification, Integer>, QuerydslPredicateExecutor<CVCertification> {
}
