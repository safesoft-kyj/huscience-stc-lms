package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    CourseService courseService;

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseSectionService courseSectionService;

    @Autowired
    CourseQuizService courseQuizService;

    @Autowired
    CourseQuizActionService courseQuizActionService;

    @Autowired
    CourseSurveyService courseSurveyService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    private CourseSectionFileService courseSectionFileService;


    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseAdminController.class);


    public MyPageController() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("마이페이지");
    }

    @GetMapping("/main")
    public String main(Model model) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("메인");

        CustomUserDetails userDetails = SessionUtil.getUserDetail();
        Account account = userService.getAccountByUserId(userDetails.getUserId());
        List<CourseAccount> courseAccountList = courseAccountService.getCourseAccountByUserId(userDetails.getUserId());

        Account parentAccount = userService.getAccountByUserId(account.getParentUserId());

//        UserVO userVO = userMapperService.getUserById(account.getUserId());

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccountList", courseAccountList);
        model.addAttribute("account", account);
        model.addAttribute("parentAccount", parentAccount);
        model.addAttribute("accountList", userService.getAccountList());

        return "content/mypage/main";
    }

    // 상위결재자 설정
    @PostMapping("/teamManager/add-post")
    public String courseManagerAddPost(@RequestParam("parentUserId") String parentUserId) {

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());
        account.setParentUserId(parentUserId);

        userRepository.save(account);

        return "redirect:/mypage/main";
    }

    @GetMapping("/quiz")
    public String quiz(Model model) {

        pageInfo.setPageId("m-mypage-quiz");
        pageInfo.setPageTitle("시험/시험결과/수료증발급");
        model.addAttribute(pageInfo);

        return "content/mypage/main";
    }

    @GetMapping("/certificate/view")
    public String certificateView(Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("수료증발급");

        List<CourseAccount> courseAccountList = courseAccountService.getCourseAccountByUserId(SessionUtil.getUserId());

        List<Course> courseList = new ArrayList<>();
        for(CourseAccount courseAccount : courseAccountList) {
            courseList.add(courseAccount.getCourse());
        }

        model.addAttribute(pageInfo);
        model.addAttribute("courseList", courseList);

        return "content/mypage/certificate/view";
    }

    @GetMapping("/certificate/print/{id}")
    public String certificatePrint(@PathVariable("id") Long courseId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("수료증발급");

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, SessionUtil.getUserId());

        Course course = courseService.getCourseById(courseId);

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccount", courseAccount);

        return "content/mypage/certificate/print";
    }

    @GetMapping("/classroom/view/{id}")
    public String myInfo(@PathVariable("id") Long courseId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("강의목차");

        CustomUserDetails userDetails = SessionUtil.getUserDetail();
        Account account = userService.getAccountByUserId(userDetails.getUserId());
//        List<CourseAccount> courseAccountList = courseAccountService.getCourseAccountByUserId(userDetails.getUserId());

        Course course = courseService.getCourseById(courseId);

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);

        return "content/mypage/classroom/view";
    }


    // 강의 View
    @GetMapping("/classroom/pdfview/{id}")
    public String pdfView(@PathVariable("id") Long fileId, Model model) {

        CourseSectionFile courseSectionFile = courseSectionFileService.getUploadFile(fileId);

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseSectionFile.getCourseSection().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("courseSectionFile", courseSectionFile);

        return "content/mypage/classroom/pdfview";
    }

    // 퀴즈 View
    @GetMapping("/classroom/quizview/{id}")
    public String quizView(@PathVariable("id") Long quizId, Model model) {

        CourseQuiz courseQuiz = courseQuizService.getCourseQuizById(quizId);

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        CourseQuizAction quizAction = new CourseQuizAction();
        quizAction.setAccount(account);
        quizAction.setExamDate(DateUtil.getTodayString());
        quizAction.setRunCount(0);
        quizAction.setMinute(0);
        quizAction.setScore(0);
        quizAction.setTotalUseSecond(0);
        quizAction.setQuiz(courseQuiz);
        CourseQuizAction quizAction1 = courseQuizActionService.saveQuizAction(quizAction);


        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseQuiz.getName());

        model.addAttribute(pageInfo);
        model.addAttribute("quizAction", quizAction1);

        return "content/mypage/classroom/quizview";
    }

    // 퀴즈 View
    @PostMapping("/classroom/quizview-post/{id}")
    public String quizPost(@PathVariable("id") Long quizActionId
            , HttpServletRequest request
            , Model model) {


        CourseQuizAction courseQuizAction = courseQuizActionService.getCourseQuizActionById(quizActionId);

        CourseQuizActionAnswer questionAnswer;
        String question_id, userAnswer;
        for(CourseQuizQuestion question : courseQuizAction.getQuiz().getQuizQuestions()) {

            question_id = Long.toString(question.getId());
            userAnswer = request.getParameter(question_id);

            questionAnswer = new CourseQuizActionAnswer();
            questionAnswer.setAnswer(question.getAnswer());
            questionAnswer.setUserAnswer(userAnswer);
            questionAnswer.setQuestion(question);
            questionAnswer.setQuizAction(courseQuizAction);

            // 정답일 경우 1, 오답일 경우 0으로 설정(정답갯수는 isAnswer 의 합임)
            if(questionAnswer.getAnswer().equals(questionAnswer.getUserAnswer())) questionAnswer.setAnswerCount(1);
            else questionAnswer.setAnswerCount(0);

            courseQuizActionService.saveQuizQuestionAnswer(questionAnswer);
        }

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseQuizAction.getQuiz().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("quizAction", courseQuizActionService.getCourseQuizActionById(quizActionId));

        return "content/mypage/classroom/quizview";
    }

    // 시험 View
    @GetMapping("/classroom/surveyview/{id}")
    public String surveyView(@PathVariable("id") Long surveyId, Model model) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(surveyId);

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseSurvey.getName());

        model.addAttribute(pageInfo);
        model.addAttribute("courseSurvey", courseSurvey);

        return "content/mypage/classroom/surveyview";
    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        CourseSectionFile courseSectionFile =  courseSectionFileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = courseSectionFileService.loadFileAsResource(courseSectionFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(courseSectionFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, courseSectionFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }
}

