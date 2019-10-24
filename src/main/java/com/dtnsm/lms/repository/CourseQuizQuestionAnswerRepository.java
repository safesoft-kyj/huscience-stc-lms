package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseQuizActionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseQuizQuestionAnswerRepository extends JpaRepository<CourseQuizActionAnswer, Long> {

}