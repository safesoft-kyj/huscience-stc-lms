package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.repository.CourseAccountOrderRepository;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.repository.CourseCertificateInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseAccountService {

    @Autowired
    CourseAccountRepository courseAccountRepository;

    @Autowired
    CourseAccountOrderRepository courseAccountOrderRepository;

    @Autowired
    CourseAccountOrderService courseAccountOrderService;

    @Autowired
    UserService userService;

    @Autowired
    CourseManagerService courseManagerService;

    @Autowired
    CourseCertificateInfoRepository courseCertificateInfoRepository;


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

    public Optional<CourseAccount> getId(long id) {
        return courseAccountRepository.findById(id);
    }


    // 사용자별 월간 교육 일정
    public List<CourseAccount> getCourseByUserAndFromDateBetween(String userId, String fromDate, String toDate, CourseStepStatus courseStepStatus) {
        return courseAccountRepository.findByAccount_UserIdAndFromDateBetweenAndCourseStatus(userId, fromDate, toDate, courseStepStatus);
    }

    // 사용자별 월간 교육 신청 일정
    public List<CourseAccount> getCourseByUserAndRequestFromDateBetween(String userId, String fromDate, String toDate, CourseStepStatus courseStepStatus) {
        return courseAccountRepository.findByAccount_UserIdAndRequestDateBetweenAndCourseStatus(userId, fromDate, toDate, courseStepStatus);
    }


    public CourseAccount getByCourseIdAndUserId(long courseId, String userId) {
        return courseAccountRepository.findByCourse_IdAndAccount_UserId(courseId, userId);
    }

    public CourseAccount getByCourseIdAndUserIdAndRequestType(long courseId, String userId, String requestType) {
        return courseAccountRepository.findByCourse_IdAndAccount_UserIdAndRequestType(courseId, userId, requestType);
    }

    public CourseAccount getByCourseIdAndUserIdAndCourseStatus(long courseId, String userId, CourseStepStatus courseStepStatus) {
        return courseAccountRepository.findByCourse_IdAndAccount_UserIdAndCourseStatus(courseId, userId, courseStepStatus);
    }


    public List<CourseAccount> getCourseAccountByCourseId(long courseId) {
        return courseAccountRepository.findByCourse_Id(courseId);
    }

    public List<CourseAccount> getCourseAccountByUserId(String userId) {
        return courseAccountRepository.findByAccount_UserId(userId);
    }

    // 상태별 신청 조회
    public List<CourseAccount> getAllByStatus(String status) {

        return courseAccountRepository.findByFnStatus(status);
    }

    // 상태별 신청 조회
    public Page<CourseAccount> getAllByStatus(String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByFnStatus(status, pageable);
    }

    // 최종 완결(신청결재, 교육)
    public List<CourseAccount> getCourseAccountIsCommitByUserId(String userId, String isCommit) {
        return courseAccountRepository.findAllByAccount_UserIdAndIsCommit(userId, isCommit);
    }

    // MyPage/main
    public Page<CourseAccount> getListUserId(String userId, String typeId, String title, CourseStepStatus courseStepStatus, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "fromDate", "isCommit"));

        return courseAccountRepository.findByAccount_UserIdAndCourse_CourseMaster_idLikeAndCourse_TitleLikeAndCourseStatusLike(userId, typeId, title, courseStepStatus, pageable);
    }

    // MyPage/main
    public Page<CourseAccount> getListUserId(String userId, String typeId, String title, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "fromDate", "isCommit"));

        return courseAccountRepository.findByAccount_UserIdAndCourse_CourseMaster_idLikeAndCourse_TitleLike(userId, typeId, title, pageable);
    }

    //교육결재 기안함
    public Page<CourseAccount> getAllByAccount_UserIdAndIsApprovalAndFnStatusLikeAndRequestTypeLike(String userId, String isApproval, String status, String requestType, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdAndIsApprovalAndFnStatusLikeAndRequestTypeLike(userId, isApproval, status, requestType, pageable);
    }


    //교육결재 기안함
    public Page<CourseAccount> getAllByAccount_UserIdAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(String userId, String typeId, String isApproval, String status, String requestType, String isReport, String reportStatus, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdLikeAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(userId, typeId, isApproval, status, requestType, isReport, reportStatus, pageable);
    }

    // 교육결재 count
    public long countByCourseRequest(String userId, String typeId, String isApproval, String status, String requestType, String isReport, String reportStatus) {
        return courseAccountRepository.countByAccount_UserIdLikeAndCourse_CourseMaster_IdLikeAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(userId, typeId, isApproval, status, requestType, isReport, reportStatus);
    }


    //교육결재 기안함
    public Page<CourseAccount> getAllByAccount_UserIdAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(String userId, String isApproval, String status, String requestType, String isReport, String reportStatus, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(userId, isApproval, status, requestType, isReport, reportStatus, pageable);
    }

    // 내신청함
    public Page<CourseAccount> getListUserId(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "isCommit", "toDate"));

        return courseAccountRepository.findByAccount_UserId(userId, pageable);
    }

    //진행중 문서
    public Page<CourseAccount> getAllByStatus(String userId, String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdAndFnStatus(userId, status, pageable);
    }


    //진행중 문서
    public List<CourseAccount> getAllByStatus(String userId, String status) {
        return courseAccountRepository.findByAccount_UserIdAndFnStatus(userId, status);
    }


    //완결 조회(기각 제외)
    public Page<CourseAccount> getCustomByUserCommit(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdAndFnStatus(userId, "1", pageable);
    }


    public List<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit) {

        return courseAccountRepository.findByAccount_UserIdAndFnStatus(userId, isCommit);
    }

    // 2차결재자(과정 관리자) 미결, 완결 구분
    public Page<CourseAccount> getListApprUserId2AndIsAppr2(String isManagerApproval,  String userId, String status, String isAppr1, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdAndFnStatus(userId, status, pageable);
    }


    // 수강생별 증명서 발급 대상 과정
    public Page<CourseAccount> getAllByAccount_UserIdAndIsCommitAndCourse_IsCerti(String userId,  String isCommit, String isCerti, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findAllByAccount_UserIdAndIsCommitAndCourse_IsCerti(userId, isCommit, isCerti, pageable);
    }



    // 과정유형, 사용자, 완결여부로 가져오기(전자결재 팦업창에서 사용)
    public List<CourseAccount> getAllByCourse_CourseMaster_IdAndAccount_UserIdAndIsCommit(String typeId, String userId, String isCommit) {
        return courseAccountRepository.findAllByCourse_CourseMaster_IdAndAccount_UserIdAndIsCommit(typeId, userId, isCommit);
    }

    // 과정유형, 사용자, 완결여부로 가져오기(전자결재 팦업창에서 사용)
    public Page<CourseAccount> getAllByCourse_CourseMaster_IdAndAccount_UserIdAndIsCommit(String typeId, String userId, String isCommit, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findAllByCourse_CourseMaster_IdAndAccount_UserIdAndIsCommit(typeId, userId, isCommit, pageable);
    }


    // 과정유형, 사용자, 완결여부로 가져오기(전자결재 팦업창에서 사용)
    public List<CourseAccount> getAllByCourse_CourseMaster_IdAndAccount_UserIdAndFStatusAndIsCommit(String typeId, String userId, String fStatus, String isCommit) {
        return courseAccountRepository.findAllByCourse_CourseMaster_IdAndAccount_UserIdAndFnStatusAndIsCommit(typeId, userId, fStatus, isCommit);
    }

    // 과정유형, 사용자, 완결여부로 가져오기(전자결재 팦업창에서 사용)
    public Page<CourseAccount> getAllByCourse_CourseMaster_IdAndAccount_UserIdAndFStatusAndIsCommit(String typeId, String userId, String fStatus, String isCommit, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findAllByCourse_CourseMaster_IdAndAccount_UserIdAndFnStatusAndIsCommit(typeId, userId, fStatus, isCommit, pageable);
    }


    // 과정 신청에 필요한 기본 검증을 진행한다.
    // 0: 계정이 존재하지 않음
    // 1: 상위결재권자가 지정되지 않음
    // 2: 관리자가 지정되지 않음
    // 9: 과정 신청 가능
    public int accountVerification(String userId) {

        Account account = userService.findByUserId(userId);

        // 계정이 존재하지 않으면
        if(account == null) return 0;

        // 상위결재권자가 지정되지 않았으면
        if (account.getParentUserId().trim().isEmpty()) return 1;

        // 과정 관리자가 등록되어 있지 않은 경우
        if (courseManagerService.getCourseManager() == null || courseManagerService.getCourseManager().getUserId().isEmpty()) return 2;

        //  수료증 기준정보가 등록되지 않은 경우
        if (courseCertificateInfoRepository.findByIsActive(1) == null) return 3;

        return 9;
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
