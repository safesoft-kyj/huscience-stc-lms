package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseSurveyAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseSurveyActionRepository extends JpaRepository<CourseSurveyAction, Long> {
    List<CourseSurveyAction> findAllByAccount_UserId(String userId);

    CourseSurveyAction findTop1ByAccount_UserIdOrderByCreatedDateDesc(String userId);

}