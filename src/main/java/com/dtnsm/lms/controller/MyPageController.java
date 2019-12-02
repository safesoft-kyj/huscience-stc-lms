package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.common.utils.Base64Utils;
import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.domain.constant.SurveyStatusType;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.*;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.validator.CurriculumVitaeValidator;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.dtnsm.lms.xdocreport.dto.*;
import com.querydsl.core.BooleanBuilder;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mypage")
@SessionAttributes({"pageInfo", "draftCV", "phaseList", "indicationList"})
public class MyPageController {
    @Autowired
    CourseService courseService;
    @Autowired
    BinderLogService binderLogService;
    @Autowired
    CourseCertificateService courseCertificateService;
    @Autowired
    CourseTrainingLogRepository courseTrainingLogRepository;
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
    private UserJobDescriptionRepository userJobDescriptionRepository;
    @Autowired
    private CurriculumVitaeRepository curriculumVitaeRepository;
    @Autowired
    private CurriculumVitaeValidator curriculumVitaeValidator;
    @Autowired
    private CurriculumVitaeService curriculumVitaeService;
    @Autowired
    private CVIndicationRepository indicationRepository;
    @Autowired
    private CVPhaseRepository phaseRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private CurriculumVitaeReportService curriculumVitaeReportService;

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
        try {
            isUpload = binderLogService.uploadTrainingLog(SessionUtil.getUserId(), multipartFile, isUploadDataDelete);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public String certificatePrint(@PathVariable("id") Long courseId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("수료증발급");

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, SessionUtil.getUserId());

        Course course = courseService.getCourseById(courseId);

        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());

//        String certiNo = courseCertificateService.newCertificateNumber(course.getCertiHead(), courseAccount.getToDate().substring(0, 4)).getFullNumber();

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccount", courseAccount);
        model.addAttribute("sign1", optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
        model.addAttribute("sign2", optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
        model.addAttribute("userName1", "Lim, Hyunjin");
        model.addAttribute("userDepart1", " / QMO of Dt&SanoMedics");
        model.addAttribute("userName2", "Kim, Kwangho");
        model.addAttribute("userDepart2", " / Registered Director of");
        model.addAttribute("userDepart21", "Dt&SanoMedics");

        return "content/mypage/certificate/print";
    }

    @GetMapping("/classroom/view/{id}")
    public String myInfo(@PathVariable("id") Long docId, Model model) {

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
        model.addAttribute("course", courseAccount.getCourse());
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
            questionAnswer.setQuestion(question);
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

            // 교육과정의 설문 여부가 Y인 경우만 실행
            if (courseAccount.getCourse().getIsSurvey().equals("Y")) {

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
                String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
                courseAccount.setCertificateNo(certificateNo);

                courseAccountService.save(courseAccount);

                // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
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
            logger.info(String.valueOf(iUserAnswerSum));
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
            String certificateNo = courseCertificateService.newCertificateNumber(courseAccount1.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount1).getFullNumber();
            courseAccount1.setCertificateNo(certificateNo);
            courseAccountService.save(courseAccount1);


            // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang

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

    @GetMapping("/cv")
    public String cv() {
        String userId = SessionUtil.getUserId();
        long current = countCV(userId, CurriculumVitaeStatus.CURRENT);
        if(current > 0) {
            return "redirect:/mypage/cv/current";
        } else {
            long count = countCV(userId);

            if(count == 0) {
                return "redirect:/mypage/cv/edit";
            } else {
                Optional<CurriculumVitae> optionalCurriculumVitae = getCV(userId, CurriculumVitaeStatus.TEMP);
                if(optionalCurriculumVitae.isPresent()) {
                    return "redirect:/mypage/cv/edit?id=" + optionalCurriculumVitae.get().getId();
                } else {
                    return "redirect:/mypage/cv/review";
                }
            }
        }
    }

    @GetMapping({"/cv/edit", "/cv/edit/{id}"})
    public String cv(@RequestParam(value = "id", required = false) Integer id, Model model) {
        pageInfo.setPageId("m-mypage-cv");
        pageInfo.setPageTitle("Curriculum Vitae");

        model.addAttribute(pageInfo);

        model.addAttribute("indicationList", indicationRepository.findAll(QCVIndication.cVIndication.indication.asc()));
        model.addAttribute("phaseList", phaseRepository.findAll(QCVPhase.cVPhase.phase.asc()));
        CurriculumVitae cv;
        if(ObjectUtils.isEmpty(id)) {
            cv = new CurriculumVitae();
            cv.setInitial(true);
            cv.getEducations().add(new CVEducation());

            CVCareerHistory history = new CVCareerHistory();
            history.setPresent(true);
            history.setCityCountry("Seoul, Korea");
            history.setClinicalTrialExperience(true);
            history.setCompanyName("Dt&SanoMedics");
            Account account = SessionUtil.getUserDetail().getUser();
            if(!ObjectUtils.isEmpty(account.getIndate())) {
                history.setStartDate(DateUtil.getStringToDate(account.getIndate()));
            }
            cv.getCareerHistories().add(history);
            cv.getLicenses().add(new CVLicense());
            cv.getCertifications().add(new CVCertification());
            cv.getMemberships().add(new CVMembership());
            cv.getLanguages().add(new CVLanguage());
            cv.getComputerKnowledges().add(new CVComputerKnowledge());
            cv.getExperiences().add(new CVExperience());
        } else {
            cv = getCV(id).get();
        }

        model.addAttribute("cv", cv);

        return "content/mypage/cv/edit";
    }

    @Transactional
    @GetMapping("/cv/view/{id}")
    public void get(@PathVariable("id") Integer id, HttpServletResponse response) throws Exception {
        CurriculumVitae savedCV = curriculumVitaeRepository.findById(id).get();
        CV dto = new CV();
        dto.setEngName(savedCV.getAccount().getEngName());
        dto.setSignDate(DateUtil.getDateToString(savedCV.getSignDate(), "dd-MMM-yyyy").toUpperCase());
        if (!StringUtils.isEmpty(savedCV.getBase64sign())) {
            dto.setSign(new ByteArrayImageProvider(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(savedCV.getBase64sign()))));
        }

        dto.setEducations(savedCV.getEducations().stream().map(e ->
                EducationDTO.builder()
                        .startDate(DateUtil.getDateToString(e.getStartDate(), "MMM yyyy"))
                        .endDate(e.isPresent() ? "Present" : DateUtil.getDateToString(e.getEndDate(), "MMM yyyy"))
                        .nameOfUniversity(e.getNameOfUniversity())
                        .degree(e.getDegree())
                        .cityCountry(e.getCityCountry())
                        .thesisTitle(e.getThesisTitle())
                        .nameOfSupervisor(e.getNameOfSupervisor()).build())
                .collect(Collectors.toList()));

        dto.setCareerHistories(savedCV.getCareerHistories().stream().map(c ->
                CareerHistoryDTO.builder()
                        .companyName(c.getCompanyName())
                        .cityCountry(c.getCityCountry())
                        .startDate(DateUtil.getDateToString(c.getStartDate(), "MMM yyyy"))
                        .endDate(c.isPresent() ? "Present" : DateUtil.getDateToString(c.getEndDate(), "MMM yyyy"))
                        .position(c.getPosition())
                        .teamDepartment(c.getTeamDepartment())
                        .build()
        ).collect(Collectors.toList()));

        dto.setLicenses(savedCV.getLicenses().stream().map(i ->
                LicenseDTO.builder()
                        .licenseNo(i.getLicenseNo())
                        .licenseInCountry(i.getLicenseInCountry())
                        .build()
        ).collect(Collectors.toList()));

        dto.setCertifications(savedCV.getCertifications().stream().map(i ->
                CertificationDTO.builder()
                        .nameOfCertification(i.getNameOfCertification())
                        .organizers(i.getOrganizers())
                        .issueDate(DateUtil.getDateToString(i.getIssueDate(), "MMM YYYY").toUpperCase())
                        .build()
        ).collect(Collectors.toList()));

        dto.setMemberships(savedCV.getMemberships().stream().map(i ->
                MembershipDTO.builder()
                        .name(i.getMembershipName())
                        .startYear(i.getStartYear())
                        .endYear(i.getEndYear())
                        .build()
        ).collect(Collectors.toList()));

        dto.setLanguages(savedCV.getLanguages().stream().map(i ->
                LanguageDTO.builder()
                        .language(i.getLanguage())
                        .level(i.getLevel().getLabel())
                        .certificateProgramList(i.getLanguageCertifications().stream().map(c -> c.getCertificateProgram()).collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList()));

        dto.setComputerKnowledges(savedCV.getComputerKnowledges().stream().map(i ->
                ComputerKnowledgeDTO.builder()
                        .name(i.getProgramName())
                        .level(i.getLevel().getLabel())
                        .certificateProgramList(i.getComputerCertifications().stream().map(c -> c.getCertificateProgram()).collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList()));

        dto.setExperiences(savedCV.getExperiences().stream().map(i ->
                ExperienceDTO.builder()
                        .indication(i.getIndication().getIndication())
                        .phase(i.getPhase().getPhase())
                        .role(i.getRole())
                        .globalOrLocal(i.getGlobalOrLocal().getLabel())
                        .workingDetails(i.getWorkingDetails())
                        .build()
        ).collect(Collectors.toList()));
        response.setHeader("Content-Disposition", "attachment; filename=\"cv.pdf\"");
        response.setContentType("application/pdf");
        curriculumVitaeReportService.generateReport(dto, response);
    }

    @Transactional
    @PostMapping("/cv/edit")
    public String cv(@ModelAttribute("cv") CurriculumVitae cv, BindingResult result, SessionStatus sessionStatus, HttpServletRequest request, Model model) throws Exception {
        boolean isAdd = WebUtils.hasSubmitParameter(request, "add");
        boolean isRemove = WebUtils.hasSubmitParameter(request, "remove");

        if(isAdd) {
            String target = ServletRequestUtils.getStringParameter(request,"add");
            int index = -1;
            if(target.indexOf(":") != -1) {
                String[] s = target.split(":");
                target = s[0];
                index = Integer.parseInt(s[1]);
            }

            model.addAttribute("target", target);
            switch (target) {
                case "education":
                    cv.getEducations().add(new CVEducation());
                    break;
                case "careerHistory":
                    cv.getCareerHistories().add(new CVCareerHistory());
                    break;
                case "license":
                    cv.getLicenses().add(new CVLicense());
                    break;
                case "certification":
                    cv.getCertifications().add(new CVCertification());
                    break;
                case "membership":
                    cv.getMemberships().add(new CVMembership());
                    break;
                case "language":
                    cv.getLanguages().add(new CVLanguage());
                    break;
                case "languageCertification":
                    cv.getLanguages().get(index).getLanguageCertifications().add(new CVLanguageCertification());
                    break;
                case "computerKnowledge":
                    cv.getComputerKnowledges().add(new CVComputerKnowledge());
                    break;
                case "computerCertification":
                    cv.getComputerKnowledges().get(index).getComputerCertifications().add(new CVComputerCertification());
                    break;
                case "experience":
                    cv.getExperiences().add(new CVExperience());
                    break;
            }

            return "content/mypage/cv/edit";
        } else if(isRemove) {
            String target = ServletRequestUtils.getStringParameter(request,"remove");
            String[] s = target.split(":");
            switch(s[0]) {
                case "education":
                    cv.getEducations().remove(Integer.parseInt(s[1]));
                    break;
                case "careerHistory":
                    cv.getCareerHistories().remove(Integer.parseInt(s[1]));
                    break;
                case "license":
                    cv.getLicenses().remove(Integer.parseInt(s[1]));
                    break;
                case "certification":
                    cv.getCertifications().remove(Integer.parseInt(s[1]));
                    break;
                case "membership":
                    cv.getMemberships().remove(Integer.parseInt(s[1]));
                    break;
                case "language":
                    cv.getLanguages().remove(Integer.parseInt(s[1]));
                    break;
                case "languageCertification":
                    String[] index = s[1].split("\\.");
                    cv.getLanguages().get(Integer.parseInt(index[0])).getLanguageCertifications().remove(Integer.parseInt(index[1]));
                    break;
                case "computerKnowledge":
                    cv.getComputerKnowledges().remove(Integer.parseInt(s[1]));
                    break;
                case "computerCertification":
                    String[] cindex = s[1].split("\\.");
                    cv.getComputerKnowledges().get(Integer.parseInt(cindex[0])).getComputerCertifications().remove(Integer.parseInt(cindex[1]));
                    break;
                case "experience":
                    cv.getExperiences().remove(Integer.parseInt(s[1]));
                    break;
            }

            model.addAttribute("target", s[0]);

            return "content/mypage/cv/edit";
        }

        boolean isSubmit = WebUtils.hasSubmitParameter(request, "_submit");

        if(isSubmit) {
            String stringStatus = ServletRequestUtils.getStringParameter(request, "_submit");
            CurriculumVitaeStatus status = CurriculumVitaeStatus.valueOf(stringStatus.toUpperCase());
            curriculumVitaeValidator.validate(cv, result);
            if (result.hasErrors()) {
                return "content/mypage/cv/edit";
            }

            cv.setStatus(status);
            cv.setAccount(SessionUtil.getUserDetail().getUser());
            if (ObjectUtils.isEmpty(cv.getId())) {
                cv.setInitial(countCV(SessionUtil.getUserId()) == 0 ? true : false);
            }

            if (status != CurriculumVitaeStatus.TEMP) {
                Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
                cv.setBase64sign(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
                cv.setSignDate(new Date());
            }

            CurriculumVitae savedCV = curriculumVitaeService.save(cv);

            new Thread(() -> {
                try {
                    CV dto = new CV();
                    dto.setEngName(savedCV.getAccount().getEngName());
                    dto.setSignDate(DateUtil.getDateToString(savedCV.getSignDate(), "dd-MMM-yyyy").toUpperCase());
                    if (!StringUtils.isEmpty(savedCV.getBase64sign())) {
                        dto.setSign(new ByteArrayImageProvider(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(savedCV.getBase64sign()))));
                    }

                    dto.setEducations(savedCV.getEducations().stream().map(e ->
                            EducationDTO.builder()
                                    .startDate(DateUtil.getDateToString(e.getStartDate(), "MMM yyyy"))
                                    .endDate(e.isPresent() ? "Present" : DateUtil.getDateToString(e.getEndDate(), "MMM yyyy"))
                                    .nameOfUniversity(e.getNameOfUniversity())
                                    .degree(e.getDegree())
                                    .thesisTitle(e.getThesisTitle())
                                    .nameOfSupervisor(e.getNameOfSupervisor()).build())
                            .collect(Collectors.toList()));

                    dto.setCareerHistories(savedCV.getCareerHistories().stream().map(c ->
                            CareerHistoryDTO.builder()
                                    .companyName(c.getCompanyName())
                                    .cityCountry(c.getCityCountry())
                                    .startDate(DateUtil.getDateToString(c.getStartDate(), "MMM yyyy"))
                                    .endDate(c.isPresent() ? "Present" : DateUtil.getDateToString(c.getEndDate(), "MMM yyyy"))
                                    .position(c.getPosition())
                                    .teamDepartment(c.getTeamDepartment())
                                    .build()
                    ).collect(Collectors.toList()));

                    dto.setLicenses(savedCV.getLicenses().stream().map(i ->
                            LicenseDTO.builder()
                                    .licenseNo(i.getLicenseNo())
                                    .licenseInCountry(i.getLicenseInCountry())
                                    .build()
                    ).collect(Collectors.toList()));

                    dto.setCertifications(savedCV.getCertifications().stream().map(i ->
                            CertificationDTO.builder()
                                    .nameOfCertification(i.getNameOfCertification())
                                    .organizers(i.getOrganizers())
                                    .issueDate(DateUtil.getDateToString(i.getIssueDate(), "MMM YYYY").toUpperCase())
                                    .build()
                    ).collect(Collectors.toList()));

                    dto.setMemberships(savedCV.getMemberships().stream().map(i ->
                            MembershipDTO.builder()
                                    .name(i.getMembershipName())
                                    .startYear(i.getStartYear())
                                    .endYear(i.getEndYear())
                                    .build()
                    ).collect(Collectors.toList()));

                    dto.setExperiences(savedCV.getExperiences().stream().map(i ->
                            ExperienceDTO.builder()
                                    .indication(i.getIndication().getIndication())
                                    .phase(i.getPhase().getPhase())
                                    .globalOrLocal(i.getGlobalOrLocal().getLabel())
                                    .workingDetails(i.getWorkingDetails())
                                    .build()
                    ).collect(Collectors.toList()));


                } catch (Exception e) {

                }
            }).run();

            sessionStatus.setComplete();
            return "redirect:/mypage/cv";
        } else {
            throw new RuntimeException("잘못된 요청 입니다.");
        }
    }

    protected Optional<CurriculumVitae> getCV(Integer id) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(qCurriculumVitae.account.userId.eq(userId));
//        builder.and(qCurriculumVitae.status.eq(status));
        builder.and(qCurriculumVitae.id.eq(id));
        return curriculumVitaeRepository.findOne(builder);
    }
    protected Optional<CurriculumVitae> getCV(String userId, CurriculumVitaeStatus status) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        builder.and(qCurriculumVitae.status.eq(status));
        return curriculumVitaeRepository.findOne(builder);
    }
    protected long countCV(String userId, CurriculumVitaeStatus status) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        builder.and(qCurriculumVitae.status.eq(status));
        return curriculumVitaeRepository.count(builder);
    }

    protected long countCV(String userId) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        return curriculumVitaeRepository.count(builder);
    }

    @GetMapping("/jd/{status}")
    public String jd(@PathVariable(value = "status") String stringStatus, Model model) {
        pageInfo.setPageId("m-mypage-jd");
        pageInfo.setPageTitle("Job Description");

        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserJobDescription.username.eq(SessionUtil.getUserId()));
        JobDescriptionStatus status = JobDescriptionStatus.valueOf(stringStatus.toUpperCase());
        if(status == JobDescriptionStatus.SUPERSEDED) {
            builder.and(qUserJobDescription.status.in(Arrays.asList(JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED)));
        } else {
            builder.and(qUserJobDescription.status.in(Arrays.asList(JobDescriptionStatus.APPROVED, JobDescriptionStatus.AGREE, JobDescriptionStatus.ASSIGNED)));
        }
        Iterable<UserJobDescription> userJobDescriptions = userJobDescriptionRepository.findAll(builder, qUserJobDescription.createdDate.desc());

        model.addAttribute(pageInfo);
        model.addAttribute("userJobDescriptions", userJobDescriptions);
        return "content/mypage/jd/list";
    }

    @PostMapping("/jd")
    public String agreeJd(@RequestParam("id") Integer id) {
        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findById(id);
        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
        if(optionalUserJobDescription.isPresent()) {
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setAgreeDate(new Date());
            userJobDescription.setStatus(JobDescriptionStatus.AGREE);
            userJobDescription.setAgreeSign(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");

            userJobDescriptionRepository.save(userJobDescription);

            //TODO Job Description (동의 알림)
        }

        return "redirect:/mypage/jd";
    }


}

