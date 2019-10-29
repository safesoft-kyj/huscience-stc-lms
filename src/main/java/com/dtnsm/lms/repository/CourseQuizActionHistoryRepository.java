package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseQuizActionHistory;
import com.dtnsm.lms.domain.CourseSectionActionHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseQuizActionHistoryRepository extends JpaRepository<CourseQuizActionHistory, Long> {


}