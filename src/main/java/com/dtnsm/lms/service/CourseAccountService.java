package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import com.dtnsm.lms.domain.constant.SurveyStatusType;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseAccountService {

    @Autowired
    CourseAccountRepository courseAccountRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CourseManagerService courseManagerService;

    @Autowired
    private CourseSectionService sectionService;

    @Autowired
    private CourseSectionActionService sectionActionService;


    @Autowired
    private CourseSurveyService courseSurveyService;

    @Autowired
    private CourseSurveyActionService surveyActionService;


    @Autowired
    private CourseQuizService quizService;

    @Autowired
    private CourseQuizActionService quizActionService;

    @Autowired
    private MailService mailService;


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

    public CourseAccount getByCourseIdAndUserId(long courseId, String userId) {
        return courseAccountRepository.findByCourse_IdAndAccount_UserId(courseId, userId);
    }


    public CourseAccount getByCourseIdAndApprUserId1(long courseId, String userId) {
        return courseAccountRepository.findByCourse_IdAndApprUserId1_UserId(courseId, userId);
    }

    public CourseAccount getByCourseIdAndApprUserId2(long courseId, String userId) {
        return courseAccountRepository.findByCourse_IdAndApprUserId2_UserId(courseId, userId);
    }

    public List<CourseAccount> getCourseAccountByCourseId(long courseId) {
        return courseAccountRepository.findByCourse_Id(courseId);
    }

    public List<CourseAccount> getCourseAccountByUserId(String userId) {
        return courseAccountRepository.findByAccount_UserId(userId);
    }


    public List<CourseAccount> getCourseAccountIsCommitByUserId(String userId, String isCommit) {
        return courseAccountRepository.findAllByAccount_UserIdAndIsCommit(userId, isCommit);
    }

    // 내신청함
    public Page<CourseAccount> getListUserId(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserId(userId, pageable);
    }

    // 1차결재자(팀장/부서장)
    public Page<CourseAccount> getListApprUserId1(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByApprUserId1_UserId(userId, pageable);
    }

    // 1차결재자(팀장/부서장) 미결, 완결 구분
    public Page<CourseAccount> getListApprUserId1AndIsAppr1(String isTeamApproval, String userId, String isAppr1, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findAllByIsTeamMangerApprovalAndApprUserId1_UserIdAndIsAppr1(isTeamApproval, userId, isAppr1, pageable);
    }

    //진행중 문서
    public Page<CourseAccount> getCustomByUserIdAndIsCommit(Pageable pageable, String userId, String isCommit) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.getCustomListByUserIdAndIsCommit(userId, isCommit, pageable);
    }

    //상태값으로 조회(내가 결재라인에 속한 경우)
    public Page<CourseAccount> getCustomByUserIdAndStatus(Pageable pageable, String userId, ApprovalStatusType status) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.getCustomListByUserIdAndStatus(userId, status, pageable);
    }

    //반려 조회(내가 결재라인에 속한 경우)
    public Page<CourseAccount> getCustomByUserIdAndReject(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.getCustomListByUserIdAndReject(userId, pageable);
    }

    //완결 조회(기각 제외)
    public Page<CourseAccount> getCustomByUserCommit(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.getCustomListByUserCommit(userId, pageable);
    }


    public List<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit) {

        return courseAccountRepository.getCustomListByUserIdAndIsCommit(userId, isCommit);
    }

    public Page<CourseAccount> getCustomListByUserIdAndIsCommit(String userId, String isCommit, Pageable pageable) {

        return courseAccountRepository.getCustomListByUserIdAndIsCommit(userId, isCommit, pageable);
    }

    public List<CourseAccount> getCustomListTop5ByUserIdAndIsCommit(String userId, String isCommit) {

        List<CourseAccount> returnList = new ArrayList<>();

        int i = 0;
        for(CourseAccount courseAccount : getCustomListByUserIdAndIsCommit(userId, isCommit)) {

            if(i >= 5) break;
            returnList.add(courseAccount);
            i++;
        }

        return returnList;
    }

    // 1차 미결문서 5개 조회 (홈에서 사용)
    public List<CourseAccount> getListTop5ByApprUserId1AndIsAppr1(String userId, String isAppr1) {

        return courseAccountRepository.findTOp5ByApprUserId1_UserIdAndIsAppr1(userId, isAppr1);
    }

    // 1차 미결건 조회
    public List<CourseAccount> getListByAppr1Process(String isTeamManagerApproval, String userId, String isAppr1) {

        return courseAccountRepository.findAllByIsTeamMangerApprovalAndApprUserId1_UserIdAndIsAppr1(isTeamManagerApproval, userId, isAppr1);
    }

    // 2차 교육결재 미결건 조회
    public List<CourseAccount> getListByAppr2Process(String isCourseMangerApproval, String userId, String isAppr2, String isAppr1) {

        return courseAccountRepository.findAllByIsCourseMangerApprovalAndApprUserId2_UserIdAndIsAppr2AndIsAppr1(isCourseMangerApproval, userId, isAppr2, isAppr1);
    }



    // 2차결재자(과정 관리자) 전체
    public Page<CourseAccount> getListApprUserId2(Pageable pageable, String userId) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByApprUserId2_UserId(userId, pageable);
    }

    // 2차결재자(과정 관리자) 미결, 완결 구분
    public Page<CourseAccount> getListApprUserId2AndIsAppr2(String isManagerApproval,  String userId, String isAppr2, String isAppr1, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findAllByIsCourseMangerApprovalAndApprUserId2_UserIdAndIsAppr2AndIsAppr1(isManagerApproval, userId, isAppr2, isAppr1, pageable);
    }
}
