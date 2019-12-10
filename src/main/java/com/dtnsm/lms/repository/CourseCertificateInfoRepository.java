package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.CourseCertificateInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCertificateInfoRepository extends JpaRepository<CourseCertificateInfo, Integer> {

    CourseCertificateInfo findByIsActive(int isActive);
}