package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseQuizAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseQuizActionRepository extends JpaRepository<CourseQuizAction, Long> {
    List<CourseQuizAction> findAllByAccount_UserId(String userId);

    CourseQuizAction findTop1ByAccount_UserIdOrderByCreatedDateDesc(String userId);

    CourseQuizAction findByCourseAccount_idAndIsActive(long docId, String isActive);

}