package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.LmsNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface LmsNotificationRepository extends JpaRepository<LmsNotification, Integer>, QuerydslPredicateExecutor<LmsNotification> {

    List<LmsNotification> findAllByAccount_UserId(String userId);

    List<LmsNotification> findTop5ByAccount_UserIdOrderByCreatedDateDesc(String userId);

    List<LmsNotification> findAllByCourse_IdAndAccount_UserId(Long courseId, String userId);

    List<LmsNotification> findAllByCourse_Id(Long courseId);

    List<LmsNotification> findAllByDocument_Id(Long docId);
}