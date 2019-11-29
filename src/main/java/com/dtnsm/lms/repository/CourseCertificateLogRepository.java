package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.CourseCertificateInfo;
import com.dtnsm.lms.domain.CourseCertificateLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCertificateLogRepository extends JpaRepository<CourseCertificateLog, Integer> {

}