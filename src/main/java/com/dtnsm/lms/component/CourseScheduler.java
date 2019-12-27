package com.dtnsm.lms.component;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.constant.LmsAlarmCourseType;
import com.dtnsm.lms.domain.constant.LmsAlarmType;
import com.dtnsm.lms.mybatis.dto.UserVO;
import com.dtnsm.lms.mybatis.service.CourseAccountMapperService;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.service.LmsNotificationService;
import com.dtnsm.lms.service.SignatureService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class CourseScheduler {
    /*
        0 0 * * * *" = the top of every hour of every day.
        10 * * * * *" = 매 10초마다 실행한다.
        0 0 8-10 * * *" = 매일 8, 9, 10시에 실행한다
        0 0 6,19 * * *" = 매일 오전 6시, 오후 7시에 실행한다.
        0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.
        0 0 9-17 * * MON-FRI" = 오전 9시부터 오후 5시까지 주중(월~금)에 실행한다.
        0 0 0 25 12 ?" = every Christmas Day at midnight
     */

    @Autowired
    CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    CourseAccountMapperService courseAccountMapperService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LmsNotificationService lmsNotificationService;

    @Autowired
    private SignatureService signatureService;

//    @Scheduled(cron = "10 * * * * *")
//    public void run() {
//        log.info("hello2...................................");
//    }


//    @Scheduled(cron = "0 0/50 11 * * *")

    // 매일 오전 2시 10분에 실행
    @Scheduled(cron = "0 10 2 * * *")
    public void updateStatus() {

        log.info("================================================");
        log.info("================= Course Update ================");
        log.info("================================================");

        List<Course> courseList = courseService.getList();

        for(Course course : courseList) {

            // 부서별 교육(BC0103), 외부교육(BC0104) 은 신청기간이 없음으로 처리하지 않는다.
            if(course.getCourseMaster().getId().equals("BC0101") || course.getCourseMaster().getId().equals("BC0102")) {

                Date requestFromDate = DateUtil.getStringToDate(course.getRequestFromDate());
                Date requestToDate = DateUtil.getStringToDate(course.getRequestToDate());
                Date fromDate = DateUtil.getStringToDate(course.getFromDate());
                Date toDate = DateUtil.getStringToDate(course.getToDate());
                Date toDay = DateUtil.getToday();


                int todayReqFromCompare = toDay.compareTo(requestFromDate);  // 1 : 현재일이 크다, -1 : 요청시작일이 크다
                int todayReqToCompare = toDay.compareTo(requestToDate);      // 1 : 현재일이 크다ㅣ
                int todayFromCompare = toDay.compareTo(fromDate);
                int todayToCompare = toDay.compareTo(toDate);

                int status = 0;

                if (todayReqFromCompare < 0)
                    status = 1; // 신청기간이전 : 신청대기
                else if (todayReqFromCompare >= 0 && todayReqToCompare <= 0) {
                    status = 2; // 요청기간중 : 교육신청
                } else if (todayReqToCompare > 0 && todayFromCompare < 0) {
                    status = 3; // 신청이후 교육 대기 : 교육대기
                } else if (todayFromCompare >= 0 && todayToCompare <= 0) {
                    status = 4; // 교육기간중 : 교육중
                } else if (todayToCompare > 0) {
                    status = 5; // 교육종료
                }

                // 요청시작일자 이전이면 신청대기
                // 요청일자 사이이면 교육신청
                // 요청종료일 이후 이면서 교육시작일자 이전이면 교육대기
                // 교육일자 사이이면 교육중
                // 교육일자 이후이면 교육종료

                if (course.getStatus() != status) {

                    course.setStatus(status);
                    course = courseService.save(course);
                }
            }else {
                // 부서별 교육(BC0103), 외부교육(BC0104) 은 신청기간이 없음


//                Date fromDate = DateUtil.getStringToDate(course.getFromDate());
//                Date toDate = DateUtil.getStringToDate(course.getToDate());
//                Date toDay = DateUtil.getToday();
//
//                int todayFromCompare = toDay.compareTo(fromDate);// 1 : 현재일이 크다, -1 : 요청시작일이 크다
//                int todayToCompare = toDay.compareTo(toDate);  // 1 : 현재일이 크다ㅣ
//
//                int status = 0;
//
//                if (todayFromCompare < 0) {
//                    status = 2; // 요청기간중 : 교육신청
//                } else if (todayFromCompare >= 0 && todayToCompare <= 0) {
//                    status = 4; // 교육기간중 : 교육중
//                } else if (todayToCompare > 0) {
//                    status = 5; // 교육종료
//                }
//
//                // 요청시작일자 이전이면 신청대기
//                // 요청일자 사이이면 교육신청
//                // 요청종료일 이후 이면서 교육시작일자 이전이면 교육대기
//                // 교육일자 사이이면 교육중
//                // 교육일자 이후이면 교육종료
//
//                if (course.getStatus() != status) {
//
//                    course.setStatus(status);
//                    course = courseService.save(course);
//                }
            }
        }
    }


    /*
    0 0 * * * *" = the top of every hour of every day.
    10 * * * * *" = 매 10초마다 실행한다.
    0 0 8-10 * * *" = 매일 8, 9, 10시에 실행한다
    0 0 6,19 * * *" = 매일 오전 6시, 오후 7시에 실행한다.
    0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.
    0 0 9-17 * * MON-FRI" = 오전 9시부터 오후 5시까지 주중(월~금)에 실행한다.
    0 0 0 25 12 ?" = every Christmas Day at midnight
 */
    // 매일 오전 2시 20분에 실행한다.
    // 그룹웨어 사용자를 추가한다.
    @Scheduled(cron = "0 20 2 * * *")
    public void updateGroupwareUser() {

        // 그룹웨어 사용자 정보로 Account 계정의 정보를 생성하거나 업데이트 한다.
        for(UserVO userVO : userMapperService.getUserAll()) {
            userService.updateAccountByGroupwareInfo(userVO.getUserId());
        }
    }

    // 매일 2시 30분에 실행
    // 상위결재권자나 사인 미지정자
//    @Scheduled(cron = "0 30 2 * * *")
    public void updateAlarm() {

        for (Account account : userService.getAccountList()) {

            if (account.getParentUserId() == null || account.getParentUserId().trim().isEmpty()) {

                // 상위결재권자 미지정
                lmsNotificationService.sendAlarm(LmsAlarmType.ParentUser, account);
            }

            if (signatureService.getSign(account.getUserId()).trim().isEmpty()) {
                // 상위결재권자 미지정
                lmsNotificationService.sendAlarm(LmsAlarmType.Sign, account);
            }
        }
    }

    // 매일 2시 30분에 실행
    // 외부교육 ToDate 익일 새벽에 외부교육참석보고서 작성 Alarm 발송 대상자 조회
    @Scheduled(cron = "0 40 2 * * *")
    public void sendCourseToDateAlarm() {

        // 교육일이 종료되고 교육완료보고서를 작성해야하는 사용자에게 알림 발송(외부교육만)
        for (CourseAccount courseAccount : courseAccountMapperService.getCourseReportAlarm("BC0104", -1)) {
//            MessageUtil.sendNotificationMessage(LmsAlarmCourseType.CourseReportApproach, courseAccount.getAccount(), courseAccount.getCourse());
            MessageUtil.sendNotification(LmsAlarmCourseType.CourseReportApproach, courseAccount.getAccount(), courseAccount.getCourse());
        }

        // 교육일이 7일로 임박한 사용자에게 알림 발송(Self 교육만)
        for (CourseAccount courseAccount : courseAccountMapperService.getCourseToDateAlarm("BC0101", -7)) {
//            MessageUtil.sendNotificationMessage(LmsAlarmCourseType.CourseToDateApproach, courseAccount.getAccount(), courseAccount.getCourse());
            MessageUtil.sendNotification(LmsAlarmCourseType.CourseToDateApproach, courseAccount.getAccount(), courseAccount.getCourse());
        }
    }
}
