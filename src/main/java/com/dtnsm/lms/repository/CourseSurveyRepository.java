package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseSurveyRepository extends JpaRepository<CourseSurvey, Long> {
    List<CourseSurvey> findAllByCourse_Id(long courseId);
}