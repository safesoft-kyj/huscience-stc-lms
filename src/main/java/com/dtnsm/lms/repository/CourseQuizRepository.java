package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseQuizRepository extends JpaRepository<CourseQuiz, Long> {
    List<CourseQuiz> findAllByCourse_Id(long courseId);
}