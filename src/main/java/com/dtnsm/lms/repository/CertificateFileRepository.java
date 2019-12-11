package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CertificateFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateFileRepository extends JpaRepository<CertificateFile, Integer> {

    List<CertificateFile> findAllByAccount_UserId(String userId);
    Page<CertificateFile> findAllByAccount_UserId(String userId, Pageable pageable);
}