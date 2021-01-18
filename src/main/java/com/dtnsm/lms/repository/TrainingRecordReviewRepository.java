package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.TrainingRecordReview;
import com.dtnsm.lms.domain.constant.TrainingRecordReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface TrainingRecordReviewRepository extends JpaRepository<TrainingRecordReview, Integer>, QuerydslPredicateExecutor<TrainingRecordReview> {
    Optional<TrainingRecordReview> findTop1ByAccountOrderByIdDesc(Account account);

    List<TrainingRecordReview> findAllByAccountAndStatusOrderByDateOfReviewDesc(Account account, TrainingRecordReviewStatus status);

    List<TrainingRecordReview> findAllByAccountAndStatusOrderByDateOfReviewAsc(Account account, TrainingRecordReviewStatus status);
}
