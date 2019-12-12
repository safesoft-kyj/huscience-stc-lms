package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.DocumentAccountOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentAccountOrderRepository extends JpaRepository<DocumentAccountOrder, Long> {

    DocumentAccountOrder findById(long id);

    DocumentAccountOrder findByDocumentAccount_IdAndFnSeq(long fNo, int seq);

    // 사용자가 결재한 모든 문서
    Page<DocumentAccountOrder> findAllByFnUser_UserIdAndFnSeqGreaterThan(String userId, int seq, Pageable pageable);

    // 사용자가 결재한 모든 문서
    List<DocumentAccountOrder> findAllByFnUser_UserIdAndFnSeqGreaterThan(String userId, int seq);

    // 사용자별 진행문서(fStatus = '0')
    Page<DocumentAccountOrder> findAllByFnUser_UserIdAndFnStatus(String userId, String fStatus, Pageable pageable);

    // 사용자별 미결문서(fStatus = '0')
    List<DocumentAccountOrder> findAllByFnUser_UserIdAndFnStatus(String userId, String fStatus);

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    Page<DocumentAccountOrder> findAllByFnUser_UserIdAndFnNextAndFnStatus(String userId, String fNext, String fStatus, Pageable pageable);

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    List<DocumentAccountOrder> findAllByFnUser_UserIdAndFnNextAndFnStatus(String userId, String fNext, String fStatus);


    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
    Page<DocumentAccountOrder> findAllByFnUser_UserIdAndFnNextAndFnStatusAndFnSeq(String userId, String fNext, String fStatus, int fSeq, Pageable pageable);

    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
    List<DocumentAccountOrder> findAllByFnUser_UserIdAndFnNextAndFnStatusAndFnSeq(String userId, String fNext, String fStatus, int fSeq);




}