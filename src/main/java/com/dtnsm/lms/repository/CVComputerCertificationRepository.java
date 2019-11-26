package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVComputerCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVComputerCertificationRepository extends JpaRepository<CVComputerCertification, Integer>, QuerydslPredicateExecutor<CVComputerCertification> {
}
