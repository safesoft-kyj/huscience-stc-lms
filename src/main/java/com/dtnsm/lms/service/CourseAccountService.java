package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.repository.CourseAccountOrderRepository;
import com.dtnsm.lms.repository.CourseAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseAccountService {

    @Autowired
    CourseAccountRepository courseAccountRepository;

    @Autowired
    CourseAccountOrderRepository courseAccountOrderRepository;

    @Autowired
    CourseAccountOrderService courseAccountOrderService;

    public CourseAccount save(CourseAccount courseAccount){
        return courseAccountRepository.save(courseAccount);
    }

    public void delete(CourseAccount courseAccount) {

        courseAccountRepository.delete(courseAccount);
    }

    public void delete(String userId) {
        courseAccountRepository.deleteCourseAccountByAccount_UserId(userId);
    }

    public void delete(long courseId) {
        courseAccountRepository.deleteCourseAccountByCourse_Id(courseId);
    }

    public CourseAccount getById(long id) {
        return courseAccountRepository.findById(id).get();
    }

    public CourseAccount getByCourseIdAndUserId(long courseId, String userId) {
        return courseAccountRepository.findByCourse_IdAndAccount_UserId(courseId, userId);
    }


    public List<CourseAccount> getCourseAccountByCourseId(long courseId) {
        return courseAccountRepository.findByCourse_Id(courseId);
    }

    public List<CourseAccount> getCourseAccountByUserId(String userId) {
        return courseAccountRepository.findByAccount_UserId(userId);
    }

    // 상태별 신청 조회
    public List<CourseAccount> getAllByStatus(String status) {

        return courseAccountRepository.findByFStatus(status);
    }

    // 상태별 신청 조회
    public Page<CourseAccount> getAllByStatus(String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByFStatus(status, pageable);
    }

    // 최종 완결(신청결재, 교육)
    public List<CourseAccount> getCourseAccountIsCommitByUserId(String userId, String isCommit) {
        return courseAccountRepository.findAllByAccount_UserIdAndIsCommit(userId, isCommit);
    }

    // 내신청함
    public Page<CourseAccount> getListUserId(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserId(userId, pageable);
    }

    //진행중 문서
    public Page<CourseAccount> getAllByStatus(String userId, String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, status, pageable);
    }
    //진행중 문서
    public List<CourseAccount> getAllByStatus(String userId, String status) {
        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, status);
    }


    //완결 조회(기각 제외)
    public Page<CourseAccount> getCustomByUserCommit(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, "1", pageable);
    }


    public List<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit) {

        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, isCommit);
    }

    // 2차결재자(과정 관리자) 미결, 완결 구분
    public Page<CourseAccount> getListApprUserId2AndIsAppr2(String isManagerApproval,  String userId, String status, String isAppr1, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, status, pageable);
    }


    //    public CourseAccount getByCourseIdAndApprUserId1(long courseId, String userId) {
//        return courseAccountRepository.findByCourse_IdAndApprUserId1_UserId(courseId, userId);
//    }
//
//    public CourseAccount getByCourseIdAndApprUserId2(long courseId, String userId) {
//        return courseAccountRepository.findByCourse_IdAndApprUserId2_UserId(courseId, userId);
//    }



//    //반려 조회(내가 결재라인에 속한 경우)
//    public Page<CourseAccount> getCustomByUserIdAndReject(Pageable pageable, String userId) {
//
//        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
//
//        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));
//
//        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, "2", pageable);
//    }



//    public Page<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit, Pageable pageable) {
//
//        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, isCommit, pageable);
//    }
//
//    public List<CourseAccount> getCustomListTop5ByUserIdAndIsCommit(String userId, String isCommit) {
//
//        List<CourseAccount> returnList = new ArrayList<>();
//
//        int i = 0;
//        for(CourseAccount courseAccount : getCustomListByUserIdAndIsCommit(userId, isCommit)) {
//
//            if(i >= 5) break;
//            returnList.add(courseAccount);
//            i++;
//        }
//
//        return returnList;
//    }

//    // 1차 미결문서 5개 조회 (홈에서 사용)
//    public List<CourseAccount> getListTop5ByApprUserId1AndIsAppr1(String userId, String status) {
//
//        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, status);
//    }
//
//    // 1차 미결건 조회
//    public List<CourseAccount> getListByAppr1Process(String isTeamManagerApproval, String userId, String status) {
//
//        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, status);
//    }
//
//    // 2차 교육결재 미결건 조회
//    public List<CourseAccount> getListByAppr2Process(String isCourseMangerApproval, String userId, String status, String isAppr1) {
//
//        return courseAccountRepository.findByAccount_UserIdAndFStatus(userId, status);
//    }


//
//    // 2차결재자(과정 관리자) 전체
//    public Page<CourseAccount> getListApprUserId2(Pageable pageable, String userId) {
//
//        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
//
//        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));
//
//        return courseAccountRepository.findByAccount_UserId(userId, pageable);
//    }


}
