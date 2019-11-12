package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseSurveyActionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseSurveyQuestionAnswerRepository extends JpaRepository<CourseSurveyActionAnswer, Long> {

}