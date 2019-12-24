package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccountOrder;
import com.dtnsm.lms.repository.CourseAccountOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseAccountOrderService {

    @Autowired
    CourseAccountOrderRepository courseAccountOrderRepository;

    public CourseAccountOrder save(CourseAccountOrder courseAccountOrder){
        return courseAccountOrderRepository.save(courseAccountOrder);
    }

    public void delete(CourseAccountOrder courseAccountOrder) {

        courseAccountOrderRepository.delete(courseAccountOrder);
    }

    public void delete(long id) {
        delete(getById(id));
    }

    public CourseAccountOrder getById(long id) {
        return courseAccountOrderRepository.findById(id);
    }

    //  다음 결재 Order 가져오기
    public CourseAccountOrder getByFnoAndSeq(long fNo, int seq) {
        return courseAccountOrderRepository.findByCourseAccount_IdAndFnSeq(fNo, seq);
    }

//    // 사용자가 결재루틴에 포함된 모든 승인문서
//    public Page<CourseAccountOrder> getAllByApproval(String userId, Pageable pageable) {
//        return courseAccountOrderRepository.searchAllByApproval(userId, pageable);
//    }
//
//    // 사용자가 결재루틴에 포함된 모든 승인문서
//    public List<CourseAccountOrder> getAllByApproval(String userId) {
//        return courseAccountOrderRepository.searchAllByApproval(userId);
//    }
//
    // 사용자가 결재한 모든 문서(seq 0:기안자, 1:1차결재자, 2:2차결재자)
    public Page<CourseAccountOrder> getAllByUserApproval(String userId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnSeqGreaterThan(userId, 0, pageable);
    }

    // 사용자가 결재한 모든 문서(seq 0:기안자, 1:1차결재자, 2:2차결재자)
    public List<CourseAccountOrder> getAllByUserApproval(String userId) {
        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnSeqGreaterThan(userId, 0);
    }

    // 사용자별 진행문서(fStatus = '0')
    public Page<CourseAccountOrder> getAllByProcess(String userId, String fStatus, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnStatus(userId, fStatus, pageable);
    }

    // 사용자별 진행문서(fStatus = '0')
    public List<CourseAccountOrder> getAllByProcess(String userId, String fStatus) {
        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnStatus(userId, fStatus);
    }

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    public Page<CourseAccountOrder> getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(String userId, String fNext, String fStatus, int seq, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnNextLikeAndFnStatusLikeAndFnSeqGreaterThan(userId, fNext, fStatus, seq, pageable);
    }

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    public List<CourseAccountOrder> getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(String userId, String fNext, String fStatus, int seq) {
        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnNextLikeAndFnStatusLikeAndFnSeqGreaterThan(userId, fNext, fStatus, seq);
    }


    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    public Page<CourseAccountOrder> getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(String userId, String fNext, String parentFstatus, String fStatus, int seq, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(userId, fNext, parentFstatus, fStatus, seq, pageable);
    }

    public List<CourseAccountOrder> getAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(String userId, String fNext, String parentFstatus, String fStatus, int seq) {
        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(userId, fNext, parentFstatus, fStatus, seq);
    }

    public long countByCourseOrderRequest(String userId, String fNext, String parentFstatus, String fStatus, int seq) {
        return courseAccountOrderRepository.countByFnUser_UserIdAndFnNextLikeAndAndCourseAccount_FnStatusLikeAndFnStatusLikeAndFnSeqGreaterThan(userId, fNext, parentFstatus, fStatus, seq);
    }


//    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
//    public Page<CourseAccountOrder> getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(String userId, String fNext, String fStatus, int fSeq, Pageable pageable){
//        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnNextAndFnStatusAndFnSeq(userId, fNext, fStatus, fSeq, pageable);
//    }
//
//    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
//    public List<CourseAccountOrder> getAllByFnUser_UserIdAndFnNextLikeAndFnStatusLike(String userId, String fNext, String fStatus, int fSeq){
//        return courseAccountOrderRepository.findAllByFnUser_UserIdAndFnNextAndFnStatusAndFnSeq(userId, fNext, fStatus, fSeq);
//    }
}
