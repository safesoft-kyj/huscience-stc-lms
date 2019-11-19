package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccountOrder;
import com.dtnsm.lms.domain.DocumentAccountOrder;
import com.dtnsm.lms.repository.CourseAccountOrderRepository;
import com.dtnsm.lms.repository.DocumentAccountOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentAccountOrderService {

    @Autowired
    DocumentAccountOrderRepository documentAccountOrderRepository;

    public DocumentAccountOrder save(DocumentAccountOrder courseAccountOrder){
        return documentAccountOrderRepository.save(courseAccountOrder);
    }

    public void delete(DocumentAccountOrder courseAccountOrder) {

        documentAccountOrderRepository.delete(courseAccountOrder);
    }

    public void delete(long id) {
        delete(getById(id));
    }

    public DocumentAccountOrder getById(long id) {
        return documentAccountOrderRepository.findById(id);
    }

    //  다음 결재 Order 가져오기
    public DocumentAccountOrder getByFnoAndSeq(long fNo, int seq) {
        return documentAccountOrderRepository.findByDocumentAccount_IdAndFSeq(fNo, seq);
    }

    // 사용자가 결재한 모든 문서(seq 0:기안자, 1:1차결재자, 2:2차결재자)
    public Page<DocumentAccountOrder> getAllByUserApproval(String userId, Pageable pageable) {
        return documentAccountOrderRepository.findAllByFUser_UserIdAndFSeqGreaterThan(userId, 0, pageable);
    }

    // 사용자가 결재한 모든 문서(seq 0:기안자, 1:1차결재자, 2:2차결재자)
    public List<DocumentAccountOrder> getAllByUserApproval(String userId) {
        return documentAccountOrderRepository.findAllByFUser_UserIdAndFSeqGreaterThan(userId, 0);
    }

    // 사용자별 진행문서(fStatus = '0')
    public Page<DocumentAccountOrder> getAllByProcess(String userId, String fStatus, Pageable pageable) {
        return documentAccountOrderRepository.findAllByFUser_UserIdAndFStatus(userId, fStatus, pageable);
    }

    // 사용자별 진행문서(fStatus = '0')
    public List<DocumentAccountOrder> getAllByProcess(String userId, String fStatus) {
        return documentAccountOrderRepository.findAllByFUser_UserIdAndFStatus(userId, fStatus);
    }

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    public Page<DocumentAccountOrder> getAllByNext(String userId, String fNext, String fStatus, Pageable pageable) {
        return documentAccountOrderRepository.findAllByFUser_UserIdAndFNextAndFStatus(userId, fNext, fStatus, pageable);
    }

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    public List<DocumentAccountOrder> getAllByNext(String userId, String fNext, String fStatus) {
        return documentAccountOrderRepository.findAllByFUser_UserIdAndFNextAndFStatus(userId, fNext, fStatus);
    }


    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
    public Page<DocumentAccountOrder> getAllByNext(String userId, String fNext, String fStatus, int fSeq, Pageable pageable){
        return documentAccountOrderRepository.findAllByFUser_UserIdAndFNextAndFStatusAndFSeq(userId, fNext, fStatus, fSeq, pageable);
    }

    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
    public List<DocumentAccountOrder> getAllByNext(String userId, String fNext, String fStatus, int fSeq){
        return documentAccountOrderRepository.findAllByFUser_UserIdAndFNextAndFStatusAndFSeq(userId, fNext, fStatus, fSeq);
    }
}