package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.CourseTrainingLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseTrainingLogRepository extends JpaRepository<CourseTrainingLog, Long> {

    // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
    List<CourseTrainingLog> findAllByAccount_UserIdAndIsUpload(String userId, String isUpload);

    // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
    Page<CourseTrainingLog> findAllByAccount_UserIdAndIsUpload(String userId, String isUpload, Pageable pageable);

    // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
    List<CourseTrainingLog> findAllByAccount_UserIdOrderByCompleteDateAsc(String userId);

    // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
    Page<CourseTrainingLog> findAllByAccount_UserIdOrderByCompleteDateAsc(String userId, Pageable pageable);

    // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
    List<CourseTrainingLog> findAllByAccount_UserIdOrderByCompleteDateDesc(String userId);

    // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
    Page<CourseTrainingLog> findAllByAccount_UserIdOrderByCompleteDateDesc(String userId, Pageable pageable);
}