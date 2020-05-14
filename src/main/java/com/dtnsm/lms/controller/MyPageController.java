package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.component.CourseScheduler;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.domain.constant.SurveyStatusType;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.repository.CourseTrainingLogRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {
    @Autowired
    CourseService courseService;
    @Autowired
    BinderLogService binderLogService;
    @Autowired
    CourseCertificateService courseCertificateService;
    @Autowired
    CourseTraingLogService courseTraingLogService;
    @Autowired
    CourseTrainingLogRepository courseTrainingLogRepository;
    @Autowired
    CourseAccountService courseAccountService;
    @Autowired
    CourseAccountRepository courseAccountRepository;
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
    CourseQuizActionAnswerService courseQuizActionAnswerService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapperService userMapperService;
    @Autowired
    private CourseSectionFileService courseSectionFileService;
    @Autowired
    private SignatureRepository signatureRepository;
    @Autowired
    CourseMasterService courseMasterService;
    @Autowired
    CodeService codeService;

    @Autowired
    JobDescriptionFileService jobDescriptionFileService;

    @Autowired
    JPAQueryFactory queryFactory;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();

    public MyPageController() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("마이페이지");
    }

//    @GetMapping("/main")
//    public String main(Model model
//            , @RequestParam(value="typeId",  defaultValue = "%") String typeId
//            , @RequestParam(value="courseStepStatusId",  defaultValue = "%") String courseStepStatusId
//            , @RequestParam(value="title",  defaultValue = "%") String title
//            , Pageable pageable) {
//
//        pageInfo.setPageId("m-mypage-main");
//        pageInfo.setPageTitle("교육현황");
//
//        //CustomUserDetails userDetails = SessionUtil.getUserDetail();
//        Account account = userService.getAccountByUserId(SessionUtil.getUserId());
//
//        Page<CourseAccount> courseAccountList;
//        if (courseStepStatusId.equals("%")) {
//            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", pageable);
//        } else {
//            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", CourseStepStatus.valueOf(courseStepStatusId), pageable);
//        }
//
//        Account parentAccount = userService.getAccountByUserId(account.getParentUserId());
//
////        UserVO userVO = userMapperService.getUserById(account.getUserId());
//
//        model.addAttribute(pageInfo);
//        model.addAttribute("courseAccountList", courseAccountList);
//        model.addAttribute("account", account);
//        model.addAttribute("parentAccount", parentAccount);
//        model.addAttribute("accountList", userService.getAccountList());
//        model.addAttribute("courseMasterList", courseMasterService.getList());
//        model.addAttribute("courseTypeList", codeService.getMinorList("BC01")); // onLine, offLine 구분
//        model.addAttribute("courseStepStatus", CourseStepStatus.class.getEnumConstants()); // 교육상태
//
//        return "content/mypage/main";
//    }

    @GetMapping("/main")
    public String main(Model model
            , @RequestParam(value="typeId",  defaultValue = "%") String typeId
            , @RequestParam(value="courseStepStatusId",  defaultValue = "%") String courseStepStatusId
            , @RequestParam(value="title",  defaultValue = "%") String title
            , Pageable pageable) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("교육현황");

        //CustomUserDetails userDetails = SessionUtil.getUserDetail();
//        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        Page<CourseAccount> courseAccountList;
//        QCourseAccount account = QCourseAccount.courseAccount;
//        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(account.account.userId.contains(SessionUtil.getUserId()));

        if (courseStepStatusId.equals("%")) {
            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", pageable);

        } else {
            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", CourseStepStatus.valueOf(courseStepStatusId), pageable);

//            builder.and(account.course.courseMaster.id.contains(typeId));
//            builder.and(account.course.title.contains(title));
//            builder.and(account.courseStatus.eq(CourseStepStatus.valueOf(courseStepStatusId)));
        }

//        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
//        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "fromDate", "isCommit"));
//        courseAccountList = courseAccountRepository.findAll(builder, pageable);

//        Account parentAccount = userService.getAccountByUserId(account.getParentUserId());

//        UserVO userVO = userMapperService.getUserById(account.getUserId());

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccountList", courseAccountList);
//        model.addAttribute("account", account);
//        model.addAttribute("parentAccount", parentAccount);
//        model.addAttribute("accountList", userService.getAccountList());
        model.addAttribute("courseMasterList", courseMasterService.getList());
//        model.addAttribute("courseTypeList", codeService.getMinorList("BC01")); // onLine, offLine 구분
        model.addAttribute("courseStepStatus", CourseStepStatus.class.getEnumConstants()); // 교육상태

        return "content/mypage/main";
    }

    @GetMapping("/myInfo")
    public String myInfo(Model model) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("사용자정보");

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());
//        List<CourseAccount> courseAccountList = courseAccountService.getCourseAccountByUserId(SessionUtil.getUserId());

//        Account parentAccount = userService.getAccountByUserId(account.getParentUserId());

        // 나의 서명을 가지고 온다.
        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
        String sign = optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "";

        model.addAttribute(pageInfo);
//        model.addAttribute("courseAccountList", courseAccountList);
        model.addAttribute("account", account);
//        model.addAttribute("parentAccount", parentAccount);
        model.addAttribute("accountList", userService.getAccountList());
        model.addAttribute("sign", sign);

        return "content/mypage/myinfo";
    }

    @GetMapping("/signPopup")
    public String signPopup(Model model) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("메인");

        return "content/mypage/signPopup";
    }

    // Lms Traing Log 조회
    @GetMapping("/courseTraingLog")
    public String lmsTraingLog(Model model, Pageable pageable) {

        pageInfo.setPageId("m-training-log");
        pageInfo.setPageTitle("Employee Training Log");

        Page<CourseTrainingLog> courseTrainingLogs = courseTraingLogService.getAllByAccount_UserId(SessionUtil.getUserId(), pageable);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseTrainingLogs);

        return "content/mypage/courseTrainingLog";
    }


    // 초기 교육 자료를 업로드 한다.
    @GetMapping("/uploadTrainingLog")
    public String uploadTraingLog(Model model) {

        pageInfo.setPageId("m-training-log-upload");
        pageInfo.setPageTitle("Training Log Upload");

        List<CourseTrainingLog> courseTrainingLogs = courseTrainingLogRepository.findAllByAccount_UserIdAndIsUpload(SessionUtil.getUserId(), "1");

        model.addAttribute(pageInfo);
        model.addAttribute("courseTrainingLogs", courseTrainingLogs);

        return "content/mypage/uploadTrainingLog";
    }


    // 초기 교육 자료를 업로드 한다.
    @PostMapping("uploadTrainingLog")
    public String uploadTraingLogPost( @RequestParam(value="isUploadDataDelete",  defaultValue = "0") boolean isUploadDataDelete
            , @RequestParam("file") MultipartFile multipartFile) {

        if(multipartFile.isEmpty()) return "redirect:/mypage/uploadTrainingLog";

        boolean isUpload = true;

        isUpload = binderLogService.uploadTrainingLog(SessionUtil.getUserId(), multipartFile, isUploadDataDelete);

        return "redirect:/mypage/uploadTrainingLog";
    }

    // 업로드한 초기 교육자료를 삭제한다.
    @GetMapping("uploadTrainingLogDelete")
    public String uploadTraingLogDelete() {

        binderLogService.uploadTriningLogDelete(SessionUtil.getUserId());

        return "redirect:/mypage/uploadTrainingLog";
    }



    // 상위결재자 설정
    @PostMapping("/teamManager/add-post")
    public String courseManagerAddPost(@RequestParam("parentUserId") String parentUserId) {

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());
        account.setParentUserId(parentUserId);

        userRepository.save(account);

        return "redirect:/mypage/myInfo";
    }

    @GetMapping("/quiz")
    public String quiz(Model model) {

        pageInfo.setPageId("m-mypage-quiz");
        pageInfo.setPageTitle("시험/시험결과/수료증발급");
        model.addAttribute(pageInfo);

        return "content/mypage/main";
    }

    @GetMapping("/certificate/view")
    public String certificateView(Model model, Pageable pageable) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("수료증발급");

        Page<CourseAccount> courseAccountList = courseAccountService.getAllByAccount_UserIdAndIsCommitAndCourse_IsCerti(SessionUtil.getUserId(), "1", "Y", pageable);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountList);

        return "content/mypage/certificate/view";
    }

    @GetMapping("/certificate/print/{id}")
    public String certificatePrint(@PathVariable("id") Long docId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("수료증발급");

        CourseAccount courseAccount = courseAccountService.getById(docId);

        courseAccount.setCertificateBindDate(DateUtil.getTodayDate());
        courseAccount = courseAccountService.save(courseAccount);

        CourseCertificateLog courseCertificateLog = courseCertificateService.getCourseCertificateLog(docId);


//        Course course = courseService.getCourseById(courseId);

//        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());

//        String certiNo = courseCertificateService.newCertificateNumber(course.getCertiHead(), courseAccount.getToDate().substring(0, 4)).getFullNumber();

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccount", courseAccount);
        model.addAttribute("courseCertificateLog", courseCertificateLog);
//        model.addAttribute("sign1", optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
//        model.addAttribute("sign2", optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
//        model.addAttribute("userName1", "Lim, Hyunjin");
//        model.addAttribute("userDepart1", " / QMO of Dt&SanoMedics");
//        model.addAttribute("userName2", "Kim, Kwangho");
//        model.addAttribute("userDepart2", " / Registered Director of");
//        model.addAttribute("userDepart21", "Dt&SanoMedics");

        return "content/mypage/certificate/print";
    }

    @GetMapping("/certificate/printView/{id}")
    public String certificatePrintView(@PathVariable("id") Long docId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("수료증발급");

        CourseAccount courseAccount = courseAccountService.getById(docId);

        CourseCertificateLog courseCertificateLog = courseCertificateService.getCourseCertificateLog(docId);

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccount", courseAccount);
        model.addAttribute("courseCertificateLog", courseCertificateLog);


        return "content/mypage/certificate/printView";
    }


    @GetMapping("/classroom/view/{id}")
    public String myClassroomView(@PathVariable("id") Long docId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("강의목차");

        CourseAccount courseAccount = courseAccountService.getById(docId);

        List<CourseSectionAction> courseSectionActions = courseAccount.getCourseSectionActions();
        List<CourseQuizAction> courseQuizActions = courseAccount.getCourseQuizActions();
        List<CourseSurveyAction> courseSurveyActions = courseAccount.getCourseSurveyActions();

        List<CourseQuizAction> newCourseQuizActionList = new ArrayList<>();
        for(CourseQuizAction  courseQuizAction : courseQuizActions) {
            if (courseQuizAction.getStatus() == QuizStatusType.COMPLETE) {
                newCourseQuizActionList.add(courseQuizAction);
            }
        }

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccount", courseAccount);
        model.addAttribute("courseSectionActions", courseSectionActions);
        model.addAttribute("quizActions", newCourseQuizActionList.size() > 0 ? newCourseQuizActionList : courseQuizActions);
        model.addAttribute("surveyActions", courseSurveyActions);

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

        if(isNew.equals("Y")) {
            // isNew가 Y인경우는 재응시 이므로 이전 시험을 Fail로 처리한다.
            quizAction.setStatus(QuizStatusType.FAIL);
            quizAction.setIsActive("0");
            quizAction = courseQuizActionService.saveQuizAction(quizAction);

            CourseQuiz courseQuiz = quizAction.getQuiz();

            CourseQuizAction newQuizAction = new CourseQuizAction();
            newQuizAction.setAccount(account);
            newQuizAction.setExecuteDate(DateUtil.getTodayString());
            newQuizAction.setRunCount(0);
            newQuizAction.setScore(0);
            newQuizAction.setTotalUseSecond(0);
            newQuizAction.setQuiz(courseQuiz);
            newQuizAction.setCourseAccount(quizAction.getCourseAccount());
            newQuizAction.setIsActive("1");
            newQuizAction.setQuestionCount(quizAction.getQuestionCount());
            quizAction = courseQuizActionService.saveQuizAction(newQuizAction);
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
            questionAnswer.setQuestionId(question.getId());
            questionAnswer.setQuizAction(courseQuizAction);

            // 정답일 경우 1, 오답일 경우 0으로 설정(정답갯수는 isAnswer 의 합임)
            if(questionAnswer.getAnswer().equals(questionAnswer.getUserAnswer())) questionAnswer.setAnswerCount(1);
            else questionAnswer.setAnswerCount(0);

            resultAnswer = courseQuizActionService.saveQuizQuestionAnswer(questionAnswer);
        }

        int answer_count = 0;

        for(CourseQuizActionAnswer answer : courseQuizAction.getActionAnswers()) {
            answer_count +=  answer.getAnswerCount();
        }

        courseQuizAction.setScore(answer_count);
        courseQuizAction.setExecuteDate(DateUtil.getTodayString());

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


            CourseAccount courseAccount = courseQuizAction1.getCourseAccount();

            // 교육과정의 설문이 있는지 확인
            if (courseAccount.getCourseSurveyActions().size() > 0) {
//            if (courseAccount.getCourse().getIsSurvey().equals("Y")) {

//                for (CourseQuizAction courseQuizAction2 : courseAccount.getCourseQuizActions()) {
//
//                    if (courseQuizAction2.getStatus().equals(QuizStatusType.COMPLETE)) {
//                        completeCount++;
//                    }
//
//                }
//
//                // 2.  시험의  COMPLETE 수가 같으면 설문을 ONGOING 상태로 변경한다.
//                if (courseAccount.getCourseQuizActions().size() == completeCount) {
                for (CourseSurveyAction courseSurveyAction : courseAccount.getCourseSurveyActions()) {
                    courseSurveyAction.setStatus(SurveyStatusType.ONGOING);
                    courseSurveyActionService.saveSurveyAction(courseSurveyAction);
                }
//                }
            } else {  // 설문 여부가 N인 경우 CourseAccount 의 상태값을 완료로 변경하고 디지털 바인더 로그를 발생시킨다.

                courseAccount.setCourseStatus(CourseStepStatus.complete);
                courseAccount.setIsAttendance("1");
                courseAccount.setIsCommit("1");

                if (courseAccount.getCourse().getIsCerti().equals("Y")) {
                    String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
                    courseAccount.setCertificateNo(certificateNo);
                }

                // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
                binderLogService.createTrainingLog(courseAccountService.save(courseAccount));
            }
        }

//        pageInfo.setPageId("m-mypage-myinfo");
//        pageInfo.setPageTitle(courseQuizAction.getQuiz().getName());
//
//        model.addAttribute(pageInfo);
//        model.addAttribute("quizAction", courseQuizActionService.getCourseQuizActionById(quizActionId));

        return "redirect:/mypage/classroom/quizCommitMessage/" + quizActionId;
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

        // 이전 설문 삭제
        for(CourseSurveyActionAnswer tmpAnswer : courseSurveyAction.getActionAnswers()) {
            courseSurveyActionService.deleteSurveyActionAnswer(tmpAnswer);
        }

        // 사용자 설문 객체 생성
        CourseSurveyActionAnswer questionAnswer;
        CourseSurveyActionAnswer resultAnswer = null;
        String question_id, userAnswer;
        int iUserAnswerSum = 0;
        for(CourseSurveyQuestion question : courseSurveyAction.getCourseSurvey().getQuestions()) {

            question_id = Long.toString(question.getId());
            userAnswer = request.getParameter("question-" + question_id.toString());

            if (question.getSurveyGubun().equals("S")) {

                if(userAnswer == null) userAnswer = "";

            } else {

                if(userAnswer == null) userAnswer = "0";

                iUserAnswerSum += Integer.parseInt(userAnswer);

//                userAnswer = String.valueOf(Integer.parseInt(userAnswer));

            }
//            log.info(String.valueOf(iUserAnswerSum));
            questionAnswer = new CourseSurveyActionAnswer();
            questionAnswer.setUserAnswer(userAnswer);
            questionAnswer.setQuestion(question);
            questionAnswer.setSurveyGubun(question.getSurveyGubun());
            questionAnswer.setSurveyAction(courseSurveyAction);

             resultAnswer = courseSurveyActionService.saveSurveyActionAnswer(questionAnswer);
        }

//        // 설문 결과를 저장한다.
//        if (resultAnswer != null) {


//            for(CourseSurveyActionAnswer answer : courseSurveyAction.getActionAnswers()) {
//                if(answer.getQuestion().getSurveyGubun().equals("M")) {
//
//                    if(answer.getUserAnswer().isEmpty()) {
//
//                        answer_count += Integer.parseInt(answer.getUserAnswer());
//
//                        logger.info(String.valueOf(answer_count));
//                    }
//                }
//            }

            courseSurveyAction.setScore(iUserAnswerSum);
            courseSurveyAction.setExecuteDate(DateUtil.getTodayString());

            courseSurveyAction.setStatus(SurveyStatusType.COMPLETE);

            CourseSurveyAction courseQuizAction1 = courseSurveyActionService.saveSurveyAction(courseSurveyAction);

            // CourseAccount 상태값 처리
            CourseAccount courseAccount1 = courseQuizAction1.getCourseAccount();
            courseAccount1.setCourseStatus(CourseStepStatus.complete);
            courseAccount1.setIsCommit("1");

            if (courseAccount1.getCourse().getIsCerti().equals("Y")) {
                String certificateNo = courseCertificateService.newCertificateNumber(courseAccount1.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount1).getFullNumber();
                courseAccount1.setCertificateNo(certificateNo);
            }

            // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
//            courseAccount1 = courseAccountService.save(courseAccount1);
            binderLogService.createTrainingLog(courseAccountService.save(courseAccount1));

            // binderLogService.createTrainingLog 에서 처리
//            for(CourseSectionAction courseSectionAction : courseAccount1.getCourseSectionActions()) {
//                CourseSectionAction courseSectionAction1 = courseSectionActionService.getById(courseSectionAction.getId());
//                courseSectionAction1.setLogApplyDate(DateUtil.getTodayDate());
//                courseSectionAction1.setIsLogApply("1");
//                courseSectionActionService.save(courseSectionAction1);
//            }
//        }

//        pageInfo.setPageId("m-mypage-myinfo");
//        pageInfo.setPageTitle(courseSurveyAction.getCourseSurvey().getName());

//        model.addAttribute(pageInfo);
//        model.addAttribute("quizAction", courseQuizActionService.getCourseQuizActionById(surveyActionId));

        //return "redirect:/mypage/classroom/view/" + courseSurveyAction.getCourseSurvey().getCourse().getId();
        return "redirect:/mypage/classroom/surveyCommitMessage/" + surveyActionId;
    }

    // 시험종료후 메세지
    @GetMapping("/classroom/quizCommitMessage/{quizActionId}")
    public String quizCommitMessage(@PathVariable("quizActionId") Long quizActionId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("시험 완료");

        CourseQuizAction courseQuizAction = courseQuizActionService.getCourseQuizActionById(quizActionId);

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuizAction", courseQuizAction);

        return "content/mypage/classroom/quizCommitMessage";
    }

    // 설문종료후 메세지
    @GetMapping("/classroom/surveyCommitMessage/{surveyActionId}")
    public String surveyCommitMessage(@PathVariable("surveyActionId") Long surveyActionId, Model model) {
        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("설문 완료");

        CourseSurveyAction courseSurveyAction = courseSurveyActionService.getCourseSurveyActionById(surveyActionId);

        model.addAttribute(pageInfo);
        model.addAttribute("courseSurveyAction", courseSurveyAction);

        return "content/mypage/classroom/surveyCommitMessage";
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

