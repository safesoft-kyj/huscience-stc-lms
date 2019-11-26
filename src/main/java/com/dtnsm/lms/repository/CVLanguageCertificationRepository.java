package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVLanguageCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVLanguageCertificationRepository extends JpaRepository<CVLanguageCertification, Integer>, QuerydslPredicateExecutor<CVLanguageCertification> {
}
