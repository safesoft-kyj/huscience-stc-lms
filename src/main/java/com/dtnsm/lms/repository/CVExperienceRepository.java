package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CVExperience;
import com.dtnsm.lms.domain.CurriculumVitae;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CVExperienceRepository extends JpaRepository<CVExperience, Integer>, QuerydslPredicateExecutor<CVExperience> {
    List<CVExperience> findAllByCurriculumVitaeOrderByIdDesc(CurriculumVitae cv);
}
