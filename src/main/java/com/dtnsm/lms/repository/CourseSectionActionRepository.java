package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseQuizAction;
import com.dtnsm.lms.domain.CourseSectionAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseSectionActionRepository extends JpaRepository<CourseSectionAction, Long> {

    CourseSectionAction findByCourseSection_IdAndAccount_UserId(long sectionId, String userId);

    List<CourseSectionAction> findAllByAccount_UserId(String userId);
}