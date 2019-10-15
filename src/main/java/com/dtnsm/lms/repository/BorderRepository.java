package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Border;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BorderRepository extends JpaRepository<Border, Long> {

    Page<Border> findAllByBorderMaster_Id(String typeId, Pageable pageable);

    List<Border> findAllByTitle(String title);

    List<Border> findAllByBorderMaster_Id(String masterId);

    List<Border> findFirst5ByBorderMaster_Id(String masterId);
}