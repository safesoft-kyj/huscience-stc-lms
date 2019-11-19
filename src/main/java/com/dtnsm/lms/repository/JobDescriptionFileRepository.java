package com.dtnsm.lms.repository;

import com.dtnsm.common.entity.JobDescriptionVersionFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDescriptionFileRepository extends JpaRepository<JobDescriptionVersionFile, Integer> {

}