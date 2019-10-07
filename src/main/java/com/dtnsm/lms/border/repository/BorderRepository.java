package com.dtnsm.lms.border.repository;

import com.dtnsm.lms.border.domain.Border;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BorderRepository extends JpaRepository<Border, Long> {

    Page<Border> findAllByBorderMaster_Id(String typeId, Pageable pageable);

    List<Border> findAllByTitle(String title);
}