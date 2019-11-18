package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.JobDescriptionVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface JobDescriptionVersionRepository extends JpaRepository<JobDescriptionVersion, Long>, QuerydslPredicateExecutor<JobDescriptionVersion> {
//    JobDescriptionVersion findByJd_IdAndIsActive(long jdId, String isActive);

//    List<JobDescriptionVersion> findAllByActive(boolean active);

//    List<JobDescriptionVersion> findAllByJobDescription_IdOrderByVerDesc(long jdId);


}