package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.TrainingRecordReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface TrainingRecordReviewRepository extends JpaRepository<TrainingRecordReview, Integer>, QuerydslPredicateExecutor<TrainingRecordReview> {
    Optional<TrainingRecordReview> findTop1ByAccountOrderByIdDesc(Account account);
}
