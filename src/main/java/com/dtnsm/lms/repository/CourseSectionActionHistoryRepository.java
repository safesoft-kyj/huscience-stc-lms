package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseSectionAction;
import com.dtnsm.lms.domain.CourseSectionActionHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseSectionActionHistoryRepository extends JpaRepository<CourseSectionActionHistory, Long> {


}