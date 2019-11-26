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


    //  status 0:진행중, 1:최종승인, 2:최종기각
    List<Document> findByAccount_UserIdAndFStatus(String userId, String status);

    Page<Document> findByAccount_UserIdAndFStatus(String userId, String status, Pageable pageable);

    List<Document> findByAccount_UserId(String userId);

    // 내신청함
    Page<Document> findByAccount_UserId(String userId, Pageable pageable);


    List<Document> findAllByAccount_UserIdAndIsCommit(String userId, String isCommit);

    List<Document> findById(long docId);

    void deleteByAccount_UserId(String userId);

    // 상태별 신청 조회
    List<Document> findByFStatus(String status);

    Page<Document> findByFStatus(String status, Pageable pageable);


    Document findByIdAndAccount_UserId(long docId, String userId);


    // 과정유형, 사용자, 완결여부로 가져오기(전자결재 팦업창에서 사용)
    Page<Document> findAllByTemplate_IdAndIsCommit(int templateId, String isCommit, Pageable pageable);

    List<Document> findAllByTemplate_IdAndIsCommit(int templateId, String isCommit);
}