package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentRepository extends JpaRepository<Document, Long> {

    // 어드민 조회
    List<Document> findAllByTemplate_Id(String templateId);

    Page<Document> findAllByTemplate_Id(String templateId, Pageable page);

    Page<Document> findAllByTitleLike(String title, Pageable pageable);

    Page<Document> findAllByContentLike(String content, Pageable pageable);

    Page<Document> findAllByTitleLikeOrContentLike(String title, String content, Pageable pageable);


    // 사용자 계정 조회
    List<Document> findAllByAccount_UserId(String userId);

    Page<Document> findAllByAccount_UserId(String userId, Pageable pageable);

    Page<Document> findAllByAccount_UserIdAndTitleLike(String userId, String title, Pageable pageable);

    Page<Document> findAllByAccount_UserIdAndContentLike(String userId, String content, Pageable pageable);

    Page<Document> findAllByAccount_UserIdAndTitleLikeOrContentLike(String userId, String title, String content, Pageable pageable);
}