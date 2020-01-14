package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.LmsNotification;
import javassist.expr.NewArray;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LmsNotificationRepository extends JpaRepository<LmsNotification, Integer> {

    List<LmsNotification> findAllByAccount_UserId(String userId);

    List<LmsNotification> findTop5ByAccount_UserIdOrderByCreatedDateDesc(String userId);

    List<LmsNotification> findAllByCourse_IdAndAccount_UserId(Long courseId, String userId);

    List<LmsNotification> findAllByCourse_Id(Long courseId);

    List<LmsNotification> findAllByDocument_Id(Long docId);
}