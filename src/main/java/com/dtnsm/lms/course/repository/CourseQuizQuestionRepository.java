package com.dtnsm.lms.course.repository;

import com.dtnsm.lms.course.domain.CourseQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseQuizQuestionRepository extends JpaRepository<CourseQuizQuestion, Long> {

}