package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Survey findByIsActive(int isActive);

}