package com.dtnsm.lms.repository;


import com.dtnsm.lms.domain.CourseCertificateNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseCertificateNumberRepository extends JpaRepository<CourseCertificateNumber, Integer> {

    CourseCertificateNumber findAllByCerTextAndCerYear(String cerText, String cerYear);
}