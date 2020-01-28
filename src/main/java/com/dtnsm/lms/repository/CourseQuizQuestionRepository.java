package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseQuizQuestionRepository extends JpaRepository<CourseQuizQuestion, Long> {

    List<CourseQuizQuestion> findAllByQuiz_Id(Long quizId);
}