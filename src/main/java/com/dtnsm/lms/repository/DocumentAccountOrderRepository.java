package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.DocumentAccountOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentAccountOrderRepository extends JpaRepository<DocumentAccountOrder, Long> {

    DocumentAccountOrder findById(long id);

    DocumentAccountOrder findByCourseAccount_IdAndFSeq(long fNo, int seq);

    // 사용자가 결재한 모든 문서
    Page<DocumentAccountOrder> findAllByFUser_UserIdAndFSeqGreaterThan(String userId, int seq, Pageable pageable);

    // 사용자가 결재한 모든 문서
    List<DocumentAccountOrder> findAllByFUser_UserIdAndFSeqGreaterThan(String userId, int seq);

    // 사용자별 진행문서(fStatus = '0')
    Page<DocumentAccountOrder> findAllByFUser_UserIdAndFStatus(String userId, String fStatus, Pageable pageable);

    // 사용자별 미결문서(fStatus = '0')
    List<DocumentAccountOrder> findAllByFUser_UserIdAndFStatus(String userId, String fStatus);

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    Page<DocumentAccountOrder> findAllByFUser_UserIdAndFNextAndFStatus(String userId, String fNext, String fStatus, Pageable pageable);

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    List<DocumentAccountOrder> findAllByFUser_UserIdAndFNextAndFStatus(String userId, String fNext, String fStatus);


    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
    Page<DocumentAccountOrder> findAllByFUser_UserIdAndFNextAndFStatusAndFSeq(String userId, String fNext, String fStatus, int fSeq, Pageable pageable);

    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
    List<DocumentAccountOrder> findAllByFUser_UserIdAndFNextAndFStatusAndFSeq(String userId, String fNext, String fStatus, int fSeq);




}