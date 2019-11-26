package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVLicenseRepository extends JpaRepository<CVLicense, Integer>, QuerydslPredicateExecutor<CVLicense> {
}
