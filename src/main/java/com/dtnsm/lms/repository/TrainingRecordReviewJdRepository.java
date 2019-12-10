package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.TrainingRecordReviewJd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TrainingRecordReviewJdRepository extends JpaRepository<TrainingRecordReviewJd, Integer>, QuerydslPredicateExecutor<TrainingRecordReviewJd> {
}
