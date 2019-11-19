package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.domain.constant.SurveyStatusType;
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
    CourseSectionActionService courseSectionActionService;

    @Autowired
    CourseQuizService courseQuizService;

    @Autowired
    CourseQuizActionService courseQuizActionService;


    @Autowired
    CourseSurveyService courseSurveyService;

    @Autowired
    CourseSurveyActionService courseSurveyActionService;

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

    @GetMapping("/myInfo")
    public String myInfo(Model model) {

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

        return "content/mypage/myInfo";
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

        List<CourseSectionAction> courseSectionActions = courseSectionActionService.getAllByUserId(SessionUtil.getUserId());
        CourseQuizAction courseQuizAction = courseQuizActionService.getTop1ByUserId(SessionUtil.getUserId());
        CourseSurveyAction courseSurveyAction = courseSurveyActionService.getTop1ByUserId(SessionUtil.getUserId());

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("courseSectionActions", courseSectionActions);
        model.addAttribute("quizAction", courseQuizAction);
        model.addAttribute("surveyAction", courseSurveyAction);

        return "content/mypage/classroom/view";
    }


    // 강의 View
    @GetMapping("/classroom/pdfview/{id}/{sectionActionId}")
    public String pdfView(@PathVariable("id") Long fileId
            ,@PathVariable("sectionActionId") Long sectionActionId
            , Model model) {

        CourseSectionFile courseSectionFile = courseSectionFileService.getUploadFile(fileId);
        CourseSectionAction sectionAction = courseSectionActionService.getById(sectionActionId);

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseSectionFile.getCourseSection().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("courseSectionFile", courseSectionFile);
        model.addAttribute("sectionAction", sectionAction);

        return "content/mypage/classroom/pdfview";
    }

    // 퀴즈 View
    @GetMapping("/classroom/quizview/{quizActionId}/{isNew}")
    public String quizView(@PathVariable("quizActionId") Long quizId
            , @PathVariable("isNew") String isNew
            , Model model) {

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        CourseQuizAction quizAction = courseQuizActionService.getCourseQuizActionById(quizId);

        quizAction.setStatus(QuizStatusType.FAIL);



        if(isNew.equals("Y")) {
            // isNew가 Y인경우는 재응시 이므로 이전 시험을 Fail로 처리한다.
            quizAction = courseQuizActionService.saveQuizAction(quizAction);

            CourseQuiz courseQuiz = quizAction.getQuiz();

            quizAction = new CourseQuizAction();
            quizAction.setAccount(account);
            quizAction.setExamDate(DateUtil.getTodayString());
            quizAction.setRunCount(0);
            quizAction.setScore(0);
            quizAction.setTotalUseSecond(0);
            quizAction.setQuiz(courseQuiz);
            quizAction = courseQuizActionService.saveQuizAction(quizAction);
        }


        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(quizAction.getQuiz().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("quizAction", quizAction);

        return "content/mypage/classroom/quizview";
    }

    // 퀴즈 View
    @PostMapping("/classroom/quizview-post/{quizActionId}")
    public String quizPost(@PathVariable("quizActionId") Long quizActionId
            , HttpServletRequest request
            , Model model) {


        // 사용자 시험 정보 가져오기
        CourseQuizAction courseQuizAction = courseQuizActionService.getCourseQuizActionById(quizActionId);

        // 사용자 시험 답 객체 생성
        CourseQuizActionAnswer questionAnswer;
        CourseQuizActionAnswer resultAnswer = null;
        String question_id, userAnswer;
        for(CourseQuizQuestion question : courseQuizAction.getQuiz().getQuizQuestions()) {

            question_id = Long.toString(question.getId());
            userAnswer = request.getParameter("question-" + question_id.toString());
            if(userAnswer == null) userAnswer = "0";

            questionAnswer = new CourseQuizActionAnswer();
            questionAnswer.setAnswer(question.getAnswer());
            questionAnswer.setUserAnswer(userAnswer);
            questionAnswer.setQuestion(question);
            questionAnswer.setQuizAction(courseQuizAction);

            // 정답일 경우 1, 오답일 경우 0으로 설정(정답갯수는 isAnswer 의 합임)
            if(questionAnswer.getAnswer().equals(questionAnswer.getUserAnswer())) questionAnswer.setAnswerCount(1);
            else questionAnswer.setAnswerCount(0);

            resultAnswer = courseQuizActionService.saveQuizQuestionAnswer(questionAnswer);
        }

        // 시험 결과를 저장한다.
        if (resultAnswer != null) {

            int answer_count = 0;
            for(CourseQuizActionAnswer answer : courseQuizAction.getActionAnswers()) {
                answer_count +=  answer.getAnswerCount();
            }

            courseQuizAction.setScore(answer_count);

            if(courseQuizAction.getQuiz().getPassCount() > answer_count) {
                courseQuizAction.setStatus(QuizStatusType.FAIL);
            }
            else {
                courseQuizAction.setStatus(QuizStatusType.COMPLETE);
            }

            CourseQuizAction courseQuizAction1 = courseQuizActionService.saveQuizAction(courseQuizAction);

            // 시험이 완료되면 이후 다른 시험이 완료되었는지 확인
            // 완료 되었다면 설문의 상태를 ONGOING 으로 변경하여 설문에 참여할 수 있도록 한다.
            if (courseQuizAction1.getStatus().equals(QuizStatusType.COMPLETE)) {

                int completeCount = 0;


                Course course = courseQuizAction1.getQuiz().getCourse();

                // 교육과정의 설문 여부가 Y인 경우만 실행
                if (course.getIsSurvey().equals("Y")) {

                    // 1. 다른 시험이 있는지 확인
                    for (CourseQuiz courseQuiz : course.getQuizzes()) {

                        for (CourseQuizAction courseQuizAction2 : courseQuiz.getQuizActions()) {

                            if (courseQuizAction2.getStatus().equals(QuizStatusType.COMPLETE)) {
                                completeCount++;
                            }
                        }
                    }

                    // 2.  시험의  COMPLETE 수가 같으면 설문을 ONGOING 상태로 변경한다.
                    if (course.getQuizzes().size() == completeCount) {
                        for (CourseSurvey courseSurvey : course.getSurveys()) {
                            for (CourseSurveyAction courseSurveyAction : courseSurvey.getSurveyActions()) {
                                courseSurveyAction.setStatus(SurveyStatusType.ONGOING);
                                courseSurveyActionService.saveSurveyAction(courseSurveyAction);
                            }
                        }
                    }
                } else {  // 설문 여부가 N인 경우 CourseAccount 의 상태값을 완료로 변경하고 디지털 바인더 로그를 발생시킨다.

                    // CourseAccount 상태값 처리
                    for(CourseAccount courseAccount : course.getCourseAccountList()) {
                        courseAccount.setCourseStatus(CourseStepStatus.complete);
                        courseAccount.setIsCommit("1");

                        courseAccountService.save(courseAccount);
                    }

                    // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
                }
            }
        }

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseQuizAction.getQuiz().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("quizAction", courseQuizActionService.getCourseQuizActionById(quizActionId));

        return "content/mypage/classroom/quizview";
    }

    // 설문 View
    @GetMapping("/classroom/surveyview/{surveyActionId}")
    public String surveyView(@PathVariable("surveyActionId") Long surveyActionId, Model model) {

        CourseSurveyAction surveyAction = courseSurveyActionService.getCourseSurveyActionById(surveyActionId);

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(surveyAction.getCourseSurvey().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("surveyAction", surveyAction);

        return "content/mypage/classroom/surveyview";
    }

    // 설문 처리
    @PostMapping("/classroom/surveyview-post/{surveyActionId}")
    public String surveyPost(@PathVariable("surveyActionId") Long surveyActionId
            , HttpServletRequest request
            , Model model) {


        // 사용자 설문 정보 가져오기
        CourseSurveyAction courseSurveyAction = courseSurveyActionService.getCourseSurveyActionById(surveyActionId);

        // 사용자 설문 객체 생성
        CourseSurveyActionAnswer questionAnswer;
        CourseSurveyActionAnswer resultAnswer = null;
        String question_id, userAnswer;
        for(CourseSurveyQuestion question : courseSurveyAction.getCourseSurvey().getQuestions()) {

            question_id = Long.toString(question.getId());
            userAnswer = request.getParameter("question-" + question_id.toString());

            if (question.getSurveyGubun().equals("S")) {

                if(userAnswer == null) userAnswer = "";

            } else {

                if(userAnswer == null) userAnswer = "0";

                userAnswer = String.valueOf(Integer.parseInt(userAnswer));

            }
            logger.info(userAnswer);
            questionAnswer = new CourseSurveyActionAnswer();
            questionAnswer.setUserAnswer(userAnswer);
            questionAnswer.setQuestion(question);
            questionAnswer.setSurveyGubun(question.getSurveyGubun());
            questionAnswer.setSurveyAction(courseSurveyAction);

            resultAnswer = courseSurveyActionService.saveSurveyQuestionAnswer(questionAnswer);
        }

        // 설문 결과를 저장한다.
        if (resultAnswer != null) {

            int answer_count = 0;
            for(CourseSurveyActionAnswer answer : courseSurveyAction.getActionAnswers()) {
                if(answer.getQuestion().getSurveyGubun().equals("M")) {

                    if(answer.getUserAnswer().isEmpty()) {
                        logger.info(answer.getUserAnswer());
                        answer_count += Integer.parseInt(answer.getUserAnswer());
                    }
                }
            }

            courseSurveyAction.setScore(answer_count);
            courseSurveyAction.setStatus(SurveyStatusType.COMPLETE);

            CourseSurveyAction courseQuizAction1 = courseSurveyActionService.saveSurveyAction(courseSurveyAction);

            Course course = courseQuizAction1.getCourseSurvey().getCourse();

            // CourseAccount 상태값 처리
            for(CourseAccount courseAccount : course.getCourseAccountList()) {
                courseAccount.setCourseStatus(CourseStepStatus.complete);
                courseAccount.setIsCommit("1");

                courseAccountService.save(courseAccount);
            }

            // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
        }

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseSurveyAction.getCourseSurvey().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("quizAction", courseQuizActionService.getCourseQuizActionById(surveyActionId));

        return "redirect:/mypage/classroom/view/" + courseSurveyAction.getCourseSurvey().getCourse().getId();
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

