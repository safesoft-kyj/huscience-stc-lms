package com.dtnsm.lms.base;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ElMinorRepository extends JpaRepository<ElMinor, String> {

    List<ElMinor> findByElMajor_MajorCd(String majorCd);
}