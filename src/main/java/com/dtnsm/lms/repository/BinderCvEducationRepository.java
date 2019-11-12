package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.BinderCv;
import com.dtnsm.lms.domain.BinderCvEducation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinderCvEducationRepository extends JpaRepository<BinderCvEducation, Long> {
    
}
