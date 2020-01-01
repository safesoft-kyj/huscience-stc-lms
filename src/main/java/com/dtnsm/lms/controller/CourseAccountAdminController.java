package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.TrainingType;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.groupdocs.conversion.internal.c.a.cd.fileformats.cad.cadobjects.CadThumbnailImage;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/admin/course")
public class CourseAccountAdminController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseAccountAdminController.class);

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseCertificateService courseCertificateService;

    @Autowired
    ApprovalCourseProcessService approvalCourseProcessService;

    @Autowired
    CourseService courseService;

    @Autowired
    BinderLogService binderLogService;

    @Autowired
    LmsNotificationService lmsNotificationService;

    private PageInfo pageInfo = new PageInfo();

    public CourseAccountAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정");

        //courseMaster = courseMasterService.getById("A01");
    }

    @GetMapping("/{typeId}/{courseId}/account")
    public String list(@PathVariable("typeId") String typeId, @PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "수강생"));

        model.addAttribute(pageInfo);
        model.addAttribute("borders", course.getCourseAccountList());
        model.addAttribute("typeId", course.getCourseMaster().getId());

        return "admin/course/account/list";
    }

    @GetMapping("/{typeId}/{courseId}/account/add")
    public String courseAccountAdd(@PathVariable("typeId") String typeId, @PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);
        pageInfo.setPageTitle(String.format("<a href='/admin/course/%s/'>%s</a> > %s", typeId, course.getCourseMaster().getCourseName(), "수강생"));

        model.addAttribute(pageInfo);
        model.addAttribute("courseId", courseId);
        model.addAttribute("userId", SessionUtil.getUserId());
        model.addAttribute("mailList", userService.getAccountList());

        return "admin/course/account/add";
    }

    @PostMapping("/{typeId}/{courseId}/account/add-post")
    public String courseAccountAddPost(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails) {

        Course course = courseService.getCourseById(courseId);

        // 교육대상자 등록
        for(String userId : mails) {

            CourseAccount tmpCourseAccount = courseAccountService.getByCourseIdAndUserId(courseId, userId);

            // 등록되지 않은 사용자만 등록한다.
            if (tmpCourseAccount == null) {

                // 결재 신청에 필요한 기본 검증을 진행한다.
                // 0: 계정이 존재하지 않음
                // 1: 상위결재권자가 지정되지 않음
                // 2: 관리자가 지정되지 않음
                // 9: 과정 신청 가능
                int result = courseAccountService.accountVerification(userId);
                Account account = userService.findByUserId(userId);

                List<Account> accountList = new ArrayList<>();

                // 과정 신청이 가능하면
                if (result == 9) {

                    // 요청 프로세서를 실행(요청 결재 및 강의를 생성한다)
                    // 교육 신청 처리(requestType 0:관리자 지정, 1:신청)
                    approvalCourseProcessService.courseRequestProcess(account, course, "0");

                    // 교육수강생으로 등록되 사용자에게 메일 및 알림 서비스 실행


                } else {
                    // TODO : 과정신청 실패시 메세지 처리 필요
                    // 실패된 사용자에 대한 처리
                    accountList.add(account);
                }
            }
        }

        return String.format("redirect:/admin/course/%s/%s/account"
                , typeId
                , courseId);
    }


    @GetMapping("/{typeId}/{courseId}/account/delete/{id}")
    public String noticeDelete(@PathVariable("typeId") String typeId
            , @PathVariable("courseId") Long courseId
            , @PathVariable("id") long docId, HttpServletRequest request) {


        try {

            CourseAccount courseAccount = courseAccountService.getById(docId);
            courseAccountService.delete(courseAccount);

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
    }


    // 교육참석처리
    @GetMapping("/{typeId}/{courseId}/account/updateAttendance/{id}")
    public String updateAttendance(@PathVariable("id") long docId, HttpServletRequest request) {

        CourseAccount courseAccount = courseAccountService.getById(docId);

        if (courseAccount.getIsAttendance().equals("0")) {

            // 교육참석으로 처리
            courseAccount.setIsAttendance("1");

            if(courseAccount.getCourse().getCourseMaster().getId().equals("BC0103")) {   // 부서별 교육이면 교육종료 시킨다.
                courseAccount.setCourseStatus(CourseStepStatus.complete);
                courseAccount.setIsCommit("1");

                // TODO : Training Log 발생

                // 강의별로 로그를 생성시킨다.
                binderLogService.createTrainingLog(courseAccount);

            } else if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0102")) {   // Class 교육이면 시험, 설문, 수료증을 체크하여 종료 처리한다.

                if(courseAccount.getCourse().getIsQuiz().equals("N") && courseAccount.getCourse().getIsSurvey().equals("N")) {
                    courseAccount.setCourseStatus(CourseStepStatus.complete);
                    courseAccount.setIsCommit("1");

                    if (courseAccount.getCourse().getIsCerti().equals("Y")) {
                        String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
                        courseAccount.setCertificateNo(certificateNo);
                    }

                    // TODO : Training Log 발생

                    // 강의별로 로그를 생성시킨다.
                    binderLogService.createTrainingLog(courseAccount);
                }
            }
        }

        courseAccount = courseAccountService.save(courseAccount);

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
    }


//
//    @GetMapping("/edit/{id}")
//    public String noticeEdit(@PathVariable("id") long id, Model model) {
//
//        Course course = courseService.getCourseById(id);
//
//        List<Account> accountList = new ArrayList<>();
//        for(CourseAccount courseAccount : course.getCourseAccountList()) {
//            accountList.add(courseAccount.getAccount());
//        }
//
//        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 수정");
//
//        model.addAttribute(pageInfo);
//        model.addAttribute("course", course);
//        model.addAttribute("id", course.getId());
//        model.addAttribute("accounts", accountList);
////        model.addAttribute("mailList", userMapperService.getUserAll());
//        model.addAttribute("mailList", userService.getAccountList());
//
//        return "admin/course/edit";
//    }
//
//    @PostMapping("/edit-post/{id}")
//    public String noticeEditPost(@PathVariable("id") long id
//            , @Valid Course course
//            , @RequestParam("files") MultipartFile[] files
//            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
//            , BindingResult result) {
//        if(result.hasErrors()) {
//            course.setId(id);
//            return "/admin/course/list/" + course.getCourseMaster().getId();
//        }
//
//        List<CourseFile> courseFile= courseService.getCourseById(id).getCourseFiles();
//        course.setCourseFiles(courseFile);
//
//        // self 교육인 경우 신청일자 및 교육일자가 별도로 없음으로 기본값으로 셋팅한다.
//        // RequestType : 1:상시, 2:기간
//        if(course.getCourseMaster().getRequestType().equals("1")) {
//            course.setRequestFromDate("1900-01-01");
//            course.setRequestToDate("1900-01-01");
//            course.setFromDate("1900-01-01");
//            course.setToDate("1900-01-01");
//        }
//
//        Course course1 = courseService.save(course);
//
//        Arrays.asList(files)
//                .stream()
//                .map(file -> courseFileService.storeFile(file, course1))
//                .collect(Collectors.toList());
//
//        boolean isMail = mails[0].equals("0") ? false : true;
//
//        if(isMail) {
//
//            // 교육대상자 알림 메일 보내기
//            for (String userId : mails) {
//
////                Account account = userService.findByUserId(userId);
////                CourseAccount courseAccount = new CourseAccount();
////                courseAccount.setCourse(course);
////                courseAccount.setAccount(account);
////                courseAccountService.save(courseAccount);
//            }
//        }
//
//        return "redirect:/admin/course/edit/" + id;
//    }
}
