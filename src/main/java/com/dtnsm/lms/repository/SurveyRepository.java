package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SurveyRepository extends JpaRepository<Survey, Long> {

    List<Survey> findAllByIsActive(int isActive);

}