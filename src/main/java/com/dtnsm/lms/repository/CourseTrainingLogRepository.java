package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.CourseTrainingLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseTrainingLogRepository extends JpaRepository<CourseTrainingLog, Long> {

    // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
    List<CourseTrainingLog> findAllByAccount_UserIdAndIsUpload(String userId, String isUpload);
}