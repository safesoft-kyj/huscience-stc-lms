package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.CVEducation;
import com.dtnsm.lms.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface BorderRepository extends JpaRepository<Border, Long>, CustomBorderRepository {

    Page<Border> findAllByBorderMaster_Id(String typeId, Pageable pageable);

    // 제목, 내용 검색
    Page<Border> findAllByBorderMaster_IdAndTitleLikeOrContentLike(String typeId, String title, String content, Pageable pageable);
    // 내용 검색
    Page<Border> findAllByBorderMaster_IdAndContentLike(String typeId, String content, Pageable pageable);
    // 제목 검색
    Page<Border> findAllByBorderMaster_IdAndTitleLike(String typeId, String title, Pageable pageable);

    List<Border> findAllByTitle(String title);

    List<Border> findAllByBorderMaster_Id(String masterId);

    List<Border> findAllByBorderMaster_Id(String masterId, Sort orders);

    List<Border> findFirst5ByBorderMaster_Id(String masterId);

    List<Border> findFirst5ByBorderMaster_Id(String masterId, Sort orders);

    List<Border> findAllByIsNoticeAndToDateLessThan(String isNotice, String toDate);
}