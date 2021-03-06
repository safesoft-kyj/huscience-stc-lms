package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.LmsAlarmType;
import com.dtnsm.lms.repository.CourseAccountOrderRepository;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.repository.CourseCertificateInfoRepository;
import com.dtnsm.lms.util.DateUtil;
import com.querydsl.core.BooleanBuilder;
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
    CourseService courseService;

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

    @Autowired
    BinderLogService binderLogService;

    @Autowired
    CourseCertificateService courseCertificateService;

    @Autowired
    LmsNotificationService lmsNotificationService;

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


    // 교육상태별 조회
    public List<CourseAccount> getByCourseStatus(CourseStepStatus courseStepStatus) {
        return courseAccountRepository.findByCourseStatus(courseStepStatus);
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

    // 교육을 신청한 적이 있는지 확인
    public CourseAccount getByCourseIdAndUserIdAndRequestType(long courseId, String userId, String requestType) {
        return courseAccountRepository.findByCourse_IdAndAccount_UserIdAndRequestType(courseId, userId, requestType);
    }


    // 교육필수대상자로 지정된 상태에서 교육신청대기중이 건.
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

    public List<CourseAccount> getAllByTestFail(boolean status) {

        return courseAccountRepository.findByIsTestFail(status);
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

    //교육결재 내부결재(not in)
    public Page<CourseAccount> getAllByAccount_UserIdAndCourse_CourseMaster_IdInAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(String userId, List<String> typeId, String isApproval, String status, String requestType, String isReport, String reportStatus, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return courseAccountRepository.findByAccount_UserIdLikeAndCourse_CourseMaster_IdInAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(userId, typeId, isApproval, status, requestType, isReport, reportStatus, pageable);
    }

    // 교육결재 count
    public long countByCourseRequest2(String userId, List<String> typeId, String isApproval, String status, String requestType, String isReport, String reportStatus) {
        return courseAccountRepository.countByAccount_UserIdLikeAndCourse_CourseMaster_IdInAndIsApprovalAndFnStatusLikeAndRequestTypeLikeAndIsReportLikeAndReportStatusLike(userId, typeId, isApproval, status, requestType, isReport, reportStatus);
    }


    //교육결재 내부결재(Like)
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
        if (account.getParentUserId().trim().isEmpty()) {
            lmsNotificationService.sendAlarm(LmsAlarmType.ParentUser, account);
            return 1;
        }

        // 과정 관리자가 등록되어 있지 않은 경우
        if (courseManagerService.getCourseManager() == null || courseManagerService.getCourseManager().getUserId().isEmpty()) {
            lmsNotificationService.sendAlarm(LmsAlarmType.Manager, account);
            return 2;
        }

        //  수료증 기준정보가 등록되지 않은 경우
        if (courseCertificateInfoRepository.findByIsActive(1) == null) return 3;

        return 9;
    }

    /**
     * 관리자가 교육수강생 지정시 체크 루틴
     * @param
     * @return
     * @exception
     * @see
     */
    public int accountVerification(String userId, Course coures) {

        Account account = userService.findByUserId(userId);

        // 계정이 존재하지 않으면
        if(account == null) return 0;

        // 정원 체크
        if(!isCourseAssignCapacity(coures.getId(), userId)){
            return 11;
        }

        // 상위결재권자가 지정되지 않았으면
        // 사용자가 신청시 체크하므로 주석 처리
//        if (account.getParentUserId().trim().isEmpty()) {
//            lmsNotificationService.sendAlarm(LmsAlarmType.ParentUser, account, coures);
//            return 1;
//        }

        // 과정 관리자가 등록되어 있지 않은 경우
//        if (courseManagerService.getCourseManager() == null || courseManagerService.getCourseManager().getUserId().isEmpty()) {
//            lmsNotificationService.sendAlarm(LmsAlarmType.Manager, account, coures);
//            return 2;
//        }

        //  수료증 기준정보가 등록되지 않은 경우
//        if (courseCertificateInfoRepository.findByIsActive(1) == null) return 3;

        return 9;
    }



    /**
     * 2020-05-14 SelfTraining 교육수강생 지정후 시험이나 설문이 지정되어 시험이나 설문이 없는 경우 수동으로 종료 시킨다.
     *
     * @param courseAccount
     * @return
     * @exception
     * @see
     */
    public boolean courseAccountManualCommit(CourseAccount courseAccount) {

        courseAccount.setCourseStatus(CourseStepStatus.complete);
        courseAccount.setIsCommit("1");

        // 수료증 처리
        if(courseAccount.getCourse().getIsCerti().equals("Y") && !courseAccount.getCourse().getCertiHead().equals("")){
            String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
            courseAccount.setCertificateNo(certificateNo);
        }

        // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
        // 강의별로 로그를 생성시킨다.
        binderLogService.createTrainingLog(courseAccount);

        this.save(courseAccount);
        return true;
    }

    /**
     * 교육 수강생 신청시 정원수 체크 : true 면 신청 가능
     * @param courseId, userId
     * @return boolean
     * @exception
     * @see
     */
    public boolean isCourseRequestCapacity(Long courseId, String userId) {
        boolean isBool = false;

        Course course = courseService.getCourseById(courseId);
        int cnt = course.getCnt();

        // cnt가 0이면 정원이 없으므로 신청할 수 있다.
        if (cnt == 0) {
            isBool = true;
        } else {    // cnt가 0이 아니면 교육정원 체크를 해야 한다.

            CourseAccount courseAccount = getByCourseIdAndUserId(courseId, userId);

            // 교육과정에 내가 지정되었거나 신청한 내역이 없으면
            if (courseAccount == null) {
                // 교육관리자의 지정 없이 바로 신청하는 것이므로 정원수보다 작아야 한다.
                if (course.getCourseAccountList().size() < course.getCnt()) {
                    isBool = true;
                }
            } else {
                // 교육수강생으로 지정된 경우 true 린턴
                if (courseAccount.getRequestType().equals("0")) {
                    isBool = true;
                }
            }
        }

        return isBool;
    }

    /**
     * 교육 수강생 지정시 정원수 체크 : true 면 지정 가능
     * @param courseId, userId
     * @return boolean
     * @exception
     * @see
     */
    public boolean isCourseAssignCapacity(Long courseId, String userId) {
        boolean isBool = false;
        Course course = courseService.getCourseById(courseId);
        int cnt = course.getCnt();

        // cnt가 0이면 정원이 지정할 수 있다.
        if (cnt == 0) {
            isBool = true;
        } else {    // cnt가 0이 아니면 교육정원 체크를 해야 한다.

            // 지정한 교육자가 이미 포함 되어 있는지 체크
            CourseAccount courseAccount = getByCourseIdAndUserId(courseId, userId);

            // 내가 수강자 명단에 없으면
            if(courseAccount == null) {
                // 정원수 체크한다.
                if (course.getCourseAccountList().size() < course.getCnt()) {
                    isBool = true;
                }
            }
        }

        return isBool;

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
