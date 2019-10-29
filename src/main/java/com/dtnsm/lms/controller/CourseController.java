package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("/course")
public class CourseController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    private CourseSectionService sectionService;



    @Autowired
    private CourseSectionActionService sectionActionService;

    @Autowired
    private CourseSurveyService surveyService;

    @Autowired
    private CourseQuizService quizService;

    @Autowired
    private CourseQuizActionService quizActionService;

    @Autowired
    private CourseFileService fileService;

    @Autowired
    private CourseManagerService courseManagerService;

    private PageInfo pageInfo = new PageInfo();

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public CourseController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("공지사항");
    }

    @GetMapping("/list/{typeId}")
    public String listPage(@PathVariable("typeId") String typeId
                            , @RequestParam("searchType") String searchType
                            , @RequestParam("searchText") String searchText
                            , @PageableDefault Pageable pageable
                            , Model model) {

        // 초기생성만 되고 타이틀이 없는 경우 삭제
        //courseService.deleteBlankCourse();

        String courseName = courseMasterService.getById(typeId).getCourseName();
        pageInfo.setPageTitle(courseName + "조회");

        Page<Course> courses;
        if(searchType.equals("all") && searchText.equals("")) {
            courses = courseService.getPageList(typeId, pageable);
        } else if(searchType.equals("all") && !searchText.equals("")) {
            courses = courseService.getPageListByTitleLikeOrContentLike(typeId, searchText, searchText, pageable);
        } else if (searchType.equals("subject")) {
            courses = courseService.getPageListByTitleLike(typeId, searchText, pageable);
        } else {
            courses = courseService.getPageListByContentLike(typeId, searchText, pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courses);

        return "content/course/list";
    }


    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        Course oldCourse = courseService.getCourseById(id);

        oldCourse.setViewCnt(oldCourse.getViewCnt() + 1);

        Course course= courseService.save(oldCourse);

        pageInfo.setPageId("self");
        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 상세");

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());


        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("account", account);

        return "content/course/view";
    }

    // 결재현황
    @GetMapping("/approval/{id}")
    public String approval(@PathVariable("id") long id, Model model) {

        Course oldCourse = courseService.getCourseById(id);

        oldCourse.setViewCnt(oldCourse.getViewCnt() + 1);

        Course course= courseService.save(oldCourse);

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(id, SessionUtil.getUserId());

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("courseAccount", courseAccount);

        return "content/course/approval";
    }

    // 교육신청 1차 결재 승인
    @GetMapping("/approvalAppr1/{id}/{userId}")
    public String approvalAppr1(@PathVariable("id") long id
            , @PathVariable("userId") String userId
            , Model model) {

        Course oldCourse = courseService.getCourseById(id);

        oldCourse.setViewCnt(oldCourse.getViewCnt() + 1);

        Course course= courseService.save(oldCourse);

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(id, userId);

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("courseAccount", courseAccount);

        return "content/course/approvalAppr1";
    }

    // 교육신청 2차 결재 승인
    @GetMapping("/approvalAppr2/{id}/{userId}")
    public String approvalAppr2(@PathVariable("id") long id
            , @PathVariable("userId") String userId
            , Model model) {

        Course oldCourse = courseService.getCourseById(id);

        oldCourse.setViewCnt(oldCourse.getViewCnt() + 1);

        Course course= courseService.save(oldCourse);

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(id, userId);

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("courseAccount", courseAccount);

        return "content/course/approvalAppr2";
    }

    @GetMapping("/request/{id}")
    public String request(@PathVariable("id") long id, Model model) {

        CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        logger.info("유저아이디:" + userDetails.getUserId());

        Course course = courseService.getCourseById(id);
        Account account = userService.getAccountByUserId(userDetails.getUserId());

        List<CourseAccount> courseAccountList = courseService.getCourseById(id).getCourseAccountList();


        // 교육신청
        CourseAccount courseAccount = new CourseAccount();
        courseAccount.setCourse(course);
        courseAccount.setAccount(account);
        courseAccount.setRequestDate(DateUtil.getTodayString());
        courseAccount.setRequestType(CourseRequestType.SPECIFY);        // 교육신청유형(관리자지정, 사용자 신청)
        courseAccount.setStatus(ApprovalStatusType.REQUEST_DONE);    // 신청완료(팀장승인진행중)
        courseAccount.setApprUserId1(userService.getAccountByUserId(account.getParentUserId()));
        courseAccount.setApprUserId2(userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId()));
        courseAccount.setIsTeamMangerApproval(course.getCourseMaster().getIsTeamMangerApproval());
        courseAccount.setIsCourseMangerApproval(course.getCourseMaster().getIsCourseMangerApproval());

        courseAccountList.add(courseAccount);
        course.setCourseAccountList(courseAccountList);

        course = courseService.save(course);

        // 강의 생성
        if(course.getSections().size() > 0) {
            for (CourseSection courseSection : course.getSections()) {
                CourseSectionAction courseSectionAction = new CourseSectionAction();
                courseSectionAction.setAccount(account);
                courseSectionAction.setCourseSection(courseSection);
                courseSectionAction.setTotalUseSecond(0);
                courseSectionAction.setRunCount(0);
                sectionActionService.save(courseSectionAction);
            }
        }


        // 시험 생성
        if(course.getIsQuiz().equals("Y") && course.getQuizzes().size() > 0) {
            for (CourseQuiz courseQuiz : course.getQuizzes()) {
                CourseQuizAction courseQuizAction = new CourseQuizAction();
                courseQuizAction.setAccount(account);
                courseQuizAction.setQuiz(courseQuiz);
                courseQuizAction.setTotalUseSecond(0);
                courseQuizAction.setRunCount(0);
                quizActionService.saveQuizAction(courseQuizAction);
            }
        }

        // 시험 생성
//        if(course.getIsSurvey().equals("Y") && course.getSurveys().size() > 0) {
//            for (CourseSurvey courseSurvey : course.getSurveys()) {
//                CourseQuizAction courseQuizAction = new CourseQuizAction();
//                courseQuizAction.setAccount(account);
//                courseQuizAction.setQuiz(courseQuiz);
//                quizActionService.saveQuizAction(courseQuizAction);
//            }
//        }


        return "redirect:/course/request/commit/" + id;
    }

    @GetMapping("/request/commit/{id}")
    public String requestCommit(@PathVariable("id") long id, Model model) {

        Course course = courseService.getCourseById(id);

        pageInfo.setPageTitle("교육신청 완료");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);

        return "content/course/requestCommit";
    }


    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        CourseFile courseFile =  fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(courseFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(courseFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + courseFile.getFileName() + "\"")
                .body(resource);
    }


}