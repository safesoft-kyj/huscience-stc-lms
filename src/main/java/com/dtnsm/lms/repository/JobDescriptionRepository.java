package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.JobDescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

}