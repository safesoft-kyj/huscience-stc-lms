package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.BinderJd;
import com.dtnsm.lms.domain.JobDescription;
import com.dtnsm.lms.domain.JobDescriptionVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface JobDescriptionVersionRepository extends JpaRepository<JobDescriptionVersion, Long> {
    JobDescriptionVersion findByJd_IdAndIsActive(long jdId, String isActive);

    List<JobDescriptionVersion> findAllByIsActive(String isActive);

    List<JobDescriptionVersion> findAllByJd_IdOrderByVerDesc(long jdId);


}