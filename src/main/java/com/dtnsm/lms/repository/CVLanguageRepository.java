package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CVLanguageRepository extends JpaRepository<CVLanguage, Integer>, QuerydslPredicateExecutor<CVLanguage> {
}
