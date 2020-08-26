package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.TrainingRecordReview;
import com.dtnsm.lms.domain.TrainingRecordReviewJd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface TrainingRecordReviewJdRepository extends JpaRepository<TrainingRecordReviewJd, Integer>, QuerydslPredicateExecutor<TrainingRecordReviewJd> {
    Optional<List<TrainingRecordReviewJd>> findAllByTrainingRecordReview(TrainingRecordReview trainingRecordReview);
}
