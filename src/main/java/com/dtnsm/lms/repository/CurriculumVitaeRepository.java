package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CurriculumVitae;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface CurriculumVitaeRepository extends JpaRepository<CurriculumVitae, Integer>, QuerydslPredicateExecutor<CurriculumVitae> {
    Optional<CurriculumVitae> findTop1ByAccountAndStatusOrderByIdDesc(Account account, CurriculumVitaeStatus status);
}
