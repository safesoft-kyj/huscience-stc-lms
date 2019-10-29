package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Document;
import com.dtnsm.lms.domain.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Integer> {

}