package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.ElFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<ElFile, Integer> {

}