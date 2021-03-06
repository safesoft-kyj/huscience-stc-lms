package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.auth.PasswordEncoding;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.domain.constant.SurveyStatusType;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.*;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
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
    private PasswordEncoding passwordEncoder;
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

    @Autowired
    CourseSectionSetupRepository courseSectionSetupRepository;

    @Value("${my.status}")
    private String mypageStatus;

    @Value("${my.log-update}")
    private String mypageLogUpdate;

    @Value("${my.log}")
    private String mypageLog;

    @Value("${my.user-info}")
    private String mypageUserInfo;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();

    public MyPageController() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("???????????????");
    }

    @GetMapping("/main")
    public String main(Model model
            , @RequestParam(value="typeId",  defaultValue = "%") String typeId
            , @RequestParam(value="courseStepStatusId",  defaultValue = "%") String courseStepStatusId
            , @RequestParam(value="title",  defaultValue = "%") String title
            , Pageable pageable) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle(mypageStatus);

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        Page<CourseAccount> courseAccountList;
        if (courseStepStatusId.equals("%")) {
            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", pageable);
        } else {
            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", CourseStepStatus.valueOf(courseStepStatusId), pageable);
        }

        Account parentAccount = userService.getAccountByUserId(account.getParentUserId());

//        UserVO userVO = userMapperService.getUserById(account.getUserId());

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccountList", courseAccountList);
        model.addAttribute("account", account);
        model.addAttribute("parentAccount", parentAccount);
        model.addAttribute("accountList", userService.getAccountList());
        model.addAttribute("courseMasterList", courseMasterService.getList());
        model.addAttribute("courseTypeList", codeService.getMinorList("BC01")); // onLine, offLine ??????
        model.addAttribute("courseStepStatus", CourseStepStatus.class.getEnumConstants()); // ????????????

        return "content/mypage/main";
    }

//    /**
//     *  2020/08/11 ???????????? ????????? Type??? ?????? ?????? (U ????????????: /mypage/myInfoStd, O ???????????????:/mypage/myInfoOther)
//     * @param
//     * @return
//     * @exception
//     * @see
//     */
//    @GetMapping("/main")
//    public String main(Model model) {
//        // ???????????? ????????? ?????? ??????????????? ??????
//        if (SessionUtil.getUserDetail().getUser().getUserType().equals("U") || SessionUtil.getUserId().equals("admin")) {
//            return "redirect:/mypage/mainStd";
//        } else {
//            return "redirect:/mypage/mainOther";
//        }
//
//    }

    /**
     * 2020/08/11 ??????????????? ????????? Type??? ?????? ?????? (U ????????????: /mypage/myInfoStd, O ???????????????:/mypage/myInfoOther)
     * @param
     * @return
     * @exception
     * @see
     */
    @GetMapping("/myInfo")
    public String myInfo() {

        // ???????????? ????????? ?????? ??????????????? ??????
        if (SessionUtil.getUserDetail().getUser().getUserType().equals("U") || SessionUtil.getUserId().equals("admin")) {
            return "redirect:/mypage/myInfoStd";
        } else {
            return "redirect:/mypage/myInfoOther";
        }
    }

//    @GetMapping("/mainStd")
//    public String mainStd(Model model
//            , @RequestParam(value="typeId",  defaultValue = "%") String typeId
//            , @RequestParam(value="courseStepStatusId",  defaultValue = "%") String courseStepStatusId
//            , @RequestParam(value="title",  defaultValue = "%") String title
//            , Pageable pageable) {
//
//        pageInfo.setPageId("m-mypage-main");
//        pageInfo.setPageTitle("????????????");
//
//        //CustomUserDetails userDetails = SessionUtil.getUserDetail();
////        Account account = userService.getAccountByUserId(SessionUtil.getUserId());
//
//        Page<CourseAccount> courseAccountList;
////        QCourseAccount account = QCourseAccount.courseAccount;
////        BooleanBuilder builder = new BooleanBuilder();
////        builder.and(account.account.userId.contains(SessionUtil.getUserId()));
//
//        if (courseStepStatusId.equals("%")) {
//            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", pageable);
//
//        } else {
//            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", CourseStepStatus.valueOf(courseStepStatusId), pageable);
//
////            builder.and(account.course.courseMaster.id.contains(typeId));
////            builder.and(account.course.title.contains(title));
////            builder.and(account.courseStatus.eq(CourseStepStatus.valueOf(courseStepStatusId)));
//        }
//
////        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
////        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "fromDate", "isCommit"));
////        courseAccountList = courseAccountRepository.findAll(builder, pageable);
//
////        Account parentAccount = userService.getAccountByUserId(account.getParentUserId());
//
////        UserVO userVO = userMapperService.getUserById(account.getUserId());
//
//        model.addAttribute(pageInfo);
//        model.addAttribute("courseAccountList", courseAccountList);
////        model.addAttribute("account", account);
////        model.addAttribute("parentAccount", parentAccount);
////        model.addAttribute("accountList", userService.getAccountList());
//        model.addAttribute("courseMasterList", courseMasterService.getList());
////        model.addAttribute("courseTypeList", codeService.getMinorList("BC01")); // onLine, offLine ??????
//        model.addAttribute("courseStepStatus", CourseStepStatus.class.getEnumConstants()); // ????????????
//
//        return "content/mypage/main";
//    }

//    @GetMapping("/mainOther")
//    public String mainOther(Model model
//            , @RequestParam(value="typeId",  defaultValue = "%") String typeId
//            , @RequestParam(value="courseStepStatusId",  defaultValue = "%") String courseStepStatusId
//            , @RequestParam(value="title",  defaultValue = "%") String title
//            , Pageable pageable) {
//
//        pageInfo.setPageId("m-mypage-main");
//        pageInfo.setPageTitle("????????????");
//
//        //CustomUserDetails userDetails = SessionUtil.getUserDetail();
////        Account account = userService.getAccountByUserId(SessionUtil.getUserId());
//
//        Page<CourseAccount> courseAccountList;
////        QCourseAccount account = QCourseAccount.courseAccount;
////        BooleanBuilder builder = new BooleanBuilder();
////        builder.and(account.account.userId.contains(SessionUtil.getUserId()));
//
//        if (courseStepStatusId.equals("%")) {
//            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", pageable);
//
//        } else {
//            courseAccountList = courseAccountService.getListUserId(SessionUtil.getUserId(), typeId + "%", "%" + title + "%", CourseStepStatus.valueOf(courseStepStatusId), pageable);
//
////            builder.and(account.course.courseMaster.id.contains(typeId));
////            builder.and(account.course.title.contains(title));
////            builder.and(account.courseStatus.eq(CourseStepStatus.valueOf(courseStepStatusId)));
//        }
//
////        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
////        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "fromDate", "isCommit"));
////        courseAccountList = courseAccountRepository.findAll(builder, pageable);
//
////        Account parentAccount = userService.getAccountByUserId(account.getParentUserId());
//
////        UserVO userVO = userMapperService.getUserById(account.getUserId());
//
//        model.addAttribute(pageInfo);
//        model.addAttribute("courseAccountList", courseAccountList);
////        model.addAttribute("account", account);
////        model.addAttribute("parentAccount", parentAccount);
////        model.addAttribute("accountList", userService.getAccountList());
//        model.addAttribute("courseMasterList", courseMasterService.getList());
////        model.addAttribute("courseTypeList", codeService.getMinorList("BC01")); // onLine, offLine ??????
//        model.addAttribute("courseStepStatus", CourseStepStatus.class.getEnumConstants()); // ????????????
//
//        return "content/mypage/main";
//    }


    @GetMapping("/changepwd")
    public String changePwd(Model model) {
        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("???????????? ??????");

        model.addAttribute(pageInfo);
        return "content/mypage/change-password";
    }

    @PostMapping("/changepwd")
    public String changePwd(@RequestParam("password") String password
            , @RequestParam("npassword") String npassword
            , RedirectAttributes attributes) {

        Optional<Account> optionalAccount = userRepository.findById(SessionUtil.getUserId());
        if (optionalAccount.isPresent()) {
            Account user = optionalAccount.get();

            // ?????? ??????????????? ????????? ??????
            // passwordEncoder.matches(????????? ?????? ?????? ????????????, ????????? ????????? ????????????)
            boolean isMatch = passwordEncoder.matches(password, user.getPassword());

            // ?????? ??????????????? ?????? ??????
            if (isMatch) {
                // ?????? ?????? ??????
                user.setPassword(passwordEncoder.encode(npassword));
                userRepository.save(user);
                attributes.addFlashAttribute("type", "success");
                attributes.addFlashAttribute("msg", "??????????????? ?????? ???????????????.");
            } else {
                attributes.addFlashAttribute("type", "error");
                attributes.addFlashAttribute("msg", "?????? ??????????????? ?????? ????????????.");
            }
        } else {
            attributes.addFlashAttribute("type", "error");
            attributes.addFlashAttribute("msg", "??????????????? ?????? ?????? ???????????????..");
        }

        return "redirect:/mypage/changepwd";
    }

    @GetMapping("/myInfoStd")
    public String myInfoStd(Model model) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle(mypageUserInfo);

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        // ?????? ????????? ????????? ??????.
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

    @GetMapping("/myInfoOther")
    public String myInfoOther(Model model) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("???????????????");

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        model.addAttribute(pageInfo);
        model.addAttribute("account", account);

        return "content/mypage/myinfoOther";
    }


    @GetMapping("/signPopup")
    public String signPopup(Model model) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("??????");

        return "content/mypage/signPopup";
    }

    // Lms Traing Log ??????
    @GetMapping("/courseTraingLog")
    public String lmsTraingLog(Model model, Pageable pageable) {

        pageInfo.setPageId("m-training-log");
        pageInfo.setPageTitle(mypageLog);

        Page<CourseTrainingLog> courseTrainingLogs = courseTraingLogService.getAllByAccount_UserId(SessionUtil.getUserId(), pageable);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseTrainingLogs);

        return "content/mypage/courseTrainingLog";
    }

    // Lms Traing Log ??????
//    @GetMapping("/courseTraingLogAll")
//    public String lmsTraingLogAll(Model model, Pageable pageable) {
//
//        pageInfo.setPageId("m-training-log");
//        pageInfo.setPageTitle("Employee Training Log");
//
//        Page<CourseTrainingLog> courseTrainingLogs = courseTraingLogService.getAll(pageable);
//
//        model.addAttribute(pageInfo);
//        model.addAttribute("borders", courseTrainingLogs);
//
//        return "content/mypage/courseTrainingLog";
//    }


    // ?????? ?????? ????????? ????????? ??????.
    @GetMapping("/uploadTrainingLog")
    public String uploadTraingLog(Model model) {

        pageInfo.setPageId("m-training-log-upload");
        pageInfo.setPageTitle(mypageLogUpdate);

        List<CourseTrainingLog> courseTrainingLogs = courseTrainingLogRepository.findAllByAccount_UserIdAndIsUpload(SessionUtil.getUserId(), "1");

        model.addAttribute(pageInfo);
        model.addAttribute("courseTrainingLogs", courseTrainingLogs);

        return "content/mypage/uploadTrainingLog";
    }
    @GetMapping("/trainingLogDownload")
    public void quizDownload(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String fileName2 = request.getParameter("fileName");
        String filePath = "D:\\UploadFiles\\lms\\";
        String path = filePath+fileName2;

        File file = new File(path);

        String userAgent = request.getHeader("User-Agent");
        boolean ie = userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("rv:11") > -1;
        String fileName = null;

        if (ie) {
            fileName = URLEncoder.encode(file.getName(), "utf-8");
        } else {
            fileName = new String(file.getName().getBytes("utf-8"),"iso-8859-1");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition","attachment;filename=\"" +fileName+"\";");

        FileInputStream fis=new FileInputStream(file);
        BufferedInputStream bis=new BufferedInputStream(fis);
        ServletOutputStream so=response.getOutputStream();
        BufferedOutputStream bos=new BufferedOutputStream(so);
    //??????????????????2
        byte[] data=new byte[2048];
        int input=0;
        while((input=bis.read(data))!=-1){
            bos.write(data,0,input);
            bos.flush();
        }
        //??????????????????
        if(bos!=null) bos.close();
        if(bis!=null) bis.close();
        if(so!=null) so.close();
        if(fis!=null) fis.close();


    }


    // ?????? ?????? ????????? ????????? ??????.
    @PostMapping("/uploadTrainingLog")
    public String uploadTraingLogPost( @RequestParam(value="isUploadDataDelete",  defaultValue = "0") boolean isUploadDataDelete
            , @RequestParam("file") MultipartFile multipartFile, RedirectAttributes attributes) {

        if(multipartFile.isEmpty()) return "redirect:/mypage/uploadTrainingLog";

        String message = binderLogService.uploadTrainingLog(SessionUtil.getUserId(), multipartFile, isUploadDataDelete);
        if(!message.isEmpty())
        {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", message);
        }

        return "redirect:/mypage/uploadTrainingLog";
    }

    // ???????????? ?????? ??????????????? ????????????.
    @GetMapping("uploadTrainingLogDelete")
    public String uploadTraingLogDelete() {

        binderLogService.uploadTriningLogDelete(SessionUtil.getUserId());

        return "redirect:/mypage/uploadTrainingLog";
    }



    // ??????????????? ??????
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
        pageInfo.setPageTitle("??????/????????????/???????????????");
        model.addAttribute(pageInfo);

        return "content/mypage/main";
    }

    @GetMapping("/certificate/view")
    public String certificateView(Model model, Pageable pageable) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("???????????????");

        Page<CourseAccount> courseAccountList = courseAccountService.getAllByAccount_UserIdAndIsCommitAndCourse_IsCerti(SessionUtil.getUserId(), "1", "Y", pageable);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseAccountList);

        return "content/mypage/certificate/view";
    }

    @GetMapping("/certificate/print/{id}")
    public String certificatePrint(@PathVariable("id") Long docId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("???????????????");

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
        pageInfo.setPageTitle("???????????????");

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
        pageInfo.setPageTitle("????????????");

        CourseAccount courseAccount = courseAccountService.getById(docId);

        List<CourseSectionAction> courseSectionActions = courseAccount.getCourseSectionActions();
        List<CourseQuizAction> courseQuizActions = courseAccount.getCourseQuizActions();
        List<CourseSurveyAction> courseSurveyActions = courseAccount.getCourseSurveyActions();

        // PDF ????????? ????????? ?????? ????????? ????????????.
        // targetWidth =>????????? ?????? ??????(??????:pixcel)
        // smallWidthRate =>????????? ????????? %
        // bigWidthRate =>????????? ????????? %
        Optional<CourseSectionSetup> optSetup = courseSectionSetupRepository.findById(1);
        CourseSectionSetup courseSectionSetup = null;
        if (optSetup.isPresent()) {
            courseSectionSetup = optSetup.get();
        } else {
            courseSectionSetup = new CourseSectionSetup();
            courseSectionSetup.setName("???????????? PDF Width ??????");
            courseSectionSetup.setTargetWidth(3000);
            courseSectionSetup.setSmallWidthRate(50);
            courseSectionSetup.setBigWidthRate(75);
            courseSectionSetup = courseSectionSetupRepository.save(courseSectionSetup);
        }

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
        model.addAttribute("courseSectionSetup", courseSectionSetup);

        return "content/mypage/classroom/view";
    }


    // ?????? View
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

    // ?????? View
    @GetMapping("/classroom/quizview/{quizActionId}/{isNew}")
    public String quizView(@PathVariable("quizActionId") Long quizId
            , @PathVariable("isNew") String isNew
            , Model model) {

        CourseQuizAction quizAction = courseQuizActionService.getCourseQuizActionById(quizId);

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(quizAction.getQuiz().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("quizAction", quizAction);

        return "content/mypage/classroom/quizview";
    }

    // ?????? View
    @PostMapping("/classroom/quizview-post/{quizActionId}")
    public String quizPost(@PathVariable("quizActionId") Long quizActionId
            , HttpServletRequest request
            , Model model
            , RedirectAttributes attributes) {

        // ????????? ?????? ?????? ????????????
        CourseQuizAction courseQuizAction = courseQuizActionService.getCourseQuizActionById(quizActionId);

        // ????????? ?????? ??????
        if(courseQuizAction.getCourseAccount().getCourse().getIsCerti().equals("Y")) {
            if(!courseCertificateService.getCourseCertificateActive()) {
                attributes.addFlashAttribute("type", "error");
                attributes.addFlashAttribute("msg", "????????? ????????? ?????? ????????? ??? ????????????. ????????????????????? ???????????????.");
                return "redirect:/mypage/classroom/quizCommitMessage/" + quizActionId;
            }
        }

        // 210712 KJH POST ?????????, ????????? quizAction ??? ??????????????? ??????
        Account account = userService.getAccountByUserId(SessionUtil.getUserId());
        if(courseQuizAction.getStatus() == QuizStatusType.FAIL) {
            // ????????? ????????? ?????? ????????? active??? false??? ????????????.
            courseQuizAction.setIsActive("0");
            CourseQuizAction oldQuizAction = courseQuizActionService.saveQuizAction(courseQuizAction);

            // ?????? ????????? ??????
            CourseQuiz courseQuiz = courseQuizAction.getQuiz();

            // ????????? QuizAction ??????
            CourseQuizAction newQuizAction = new CourseQuizAction();
            newQuizAction.setAccount(account);
            newQuizAction.setExecuteDate(DateUtil.getTodayString());
            newQuizAction.setRunCount(0);
            newQuizAction.setScore(0);
            newQuizAction.setTotalUseSecond(0);
            newQuizAction.setQuiz(courseQuiz);
            newQuizAction.setCourseAccount(courseQuizAction.getCourseAccount());
            newQuizAction.setIsActive("1");
            newQuizAction.setQuestionCount(courseQuizAction.getQuestionCount());
            courseQuizAction = courseQuizActionService.saveQuizAction(newQuizAction);
            quizActionId = courseQuizAction.getId();
        }

        // ????????? ?????? ??? ?????? ??????
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
            courseQuizAction.addQuizActionAnswer(questionAnswer); // 210721 KJH

            // ????????? ?????? 1, ????????? ?????? 0?????? ??????(??????????????? isAnswer ??? ??????)
            if(questionAnswer.getAnswer().equals(questionAnswer.getUserAnswer())) {
                questionAnswer.setAnswerCount(1);
            } else {
                questionAnswer.setAnswerCount(0);
            }

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

        // ????????? ???????????? ?????? ?????? ????????? ?????????????????? ??????
        // ?????? ???????????? ????????? ????????? ONGOING ?????? ???????????? ????????? ????????? ??? ????????? ??????.
        if (courseQuizAction1.getStatus().equals(QuizStatusType.COMPLETE)) {

            int completeCount = 0;

            CourseAccount courseAccount = courseQuizAction1.getCourseAccount();

            // ??????????????? ????????? ????????? ??????
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
//                // 2.  ?????????  COMPLETE ?????? ????????? ????????? ONGOING ????????? ????????????.
//                if (courseAccount.getCourseQuizActions().size() == completeCount) {
                for (CourseSurveyAction courseSurveyAction : courseAccount.getCourseSurveyActions()) {
                    courseSurveyAction.setStatus(SurveyStatusType.ONGOING);
                    courseSurveyActionService.saveSurveyAction(courseSurveyAction);
                }
//                }
            } else {  // ?????? ????????? N??? ?????? CourseAccount ??? ???????????? ????????? ???????????? ????????? ????????? ????????? ???????????????.

                if(courseAccount.getCourseStatus() != CourseStepStatus.complete) {

                    courseAccount.setCourseStatus(CourseStepStatus.complete);
                    courseAccount.setIsAttendance("1");
                    courseAccount.setIsCommit("1");

                    if (courseAccount.getCourse().getIsCerti().equals("Y")) {
                        String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
                        courseAccount.setCertificateNo(certificateNo);
                    }

                    // TODO: 2019/11/12 Digital Binder Employee Training Log ?????? -ks Hwang
                    binderLogService.createTrainingLog(courseAccountService.save(courseAccount));
                } else {
                    attributes.addFlashAttribute("type", "error");
                    attributes.addFlashAttribute("msg", "?????? ???????????? ????????? Complete ????????????, ????????? ???????????? ???????????????.");
                }
            }
        }
        else {
            CourseAccount courseAccount = courseQuizAction1.getCourseAccount();

            if(courseAccount.getCourse().getCourseMaster().getId().equals("BC0102")) // class training
            {
                courseAccount.setTestFail(true);
                courseAccountService.save(courseAccount);
            }
        }

//        pageInfo.setPageId("m-mypage-myinfo");
//        pageInfo.setPageTitle(courseQuizAction.getQuiz().getName());
//
//        model.addAttribute(pageInfo);
//        model.addAttribute("quizAction", courseQuizActionService.getCourseQuizActionById(quizActionId));

        return "redirect:/mypage/classroom/quizCommitMessage/" + quizActionId;
    }

    // ?????? View
    @GetMapping("/classroom/surveyview/{surveyActionId}")
    public String surveyView(@PathVariable("surveyActionId") Long surveyActionId, Model model) {

        CourseSurveyAction surveyAction = courseSurveyActionService.getCourseSurveyActionById(surveyActionId);

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(surveyAction.getCourseSurvey().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("surveyAction", surveyAction);

        return "content/mypage/classroom/surveyview";
    }

    // ?????? ??????
    @PostMapping("/classroom/surveyview-post/{surveyActionId}")
    public String surveyPost(@PathVariable("surveyActionId") Long surveyActionId
            , HttpServletRequest request
            , Model model
            , RedirectAttributes attributes) {

        // ????????? ?????? ?????? ????????????
        CourseSurveyAction courseSurveyAction = courseSurveyActionService.getCourseSurveyActionById(surveyActionId);

        // ????????? ?????? ??????
        if(courseSurveyAction.getCourseAccount().getCourse().getIsCerti().equals("Y")) {
            if(!courseCertificateService.getCourseCertificateActive()) {
                attributes.addFlashAttribute("type", "error");
                attributes.addFlashAttribute("msg", "????????? ????????? ?????? ????????? ??? ????????????. ????????????????????? ???????????????.");
                return "redirect:/mypage/classroom/surveyCommitMessage/" + surveyActionId;
            }
        }

        // ??????????????? ????????? ?????? ????????? ????????????.
        if (courseSurveyAction.getCourseAccount().getCourseStatus() != CourseStepStatus.complete) {
            // ?????? ?????? ??????
            for(CourseSurveyActionAnswer tmpAnswer : courseSurveyAction.getActionAnswers()) {
                courseSurveyActionService.deleteSurveyActionAnswer(tmpAnswer);
            }

            // ????????? ?????? ?????? ??????
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

            courseSurveyAction.setScore(iUserAnswerSum);
            courseSurveyAction.setExecuteDate(DateUtil.getTodayString());

            courseSurveyAction.setStatus(SurveyStatusType.COMPLETE);

            CourseSurveyAction courseQuizAction1 = courseSurveyActionService.saveSurveyAction(courseSurveyAction);

            // CourseAccount ????????? ??????
            CourseAccount courseAccount1 = courseQuizAction1.getCourseAccount();
            courseAccount1.setCourseStatus(CourseStepStatus.complete);
            courseAccount1.setIsCommit("1");

            if (courseAccount1.getCourse().getIsCerti().equals("Y")) {
                String certificateNo = courseCertificateService.newCertificateNumber(courseAccount1.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount1).getFullNumber();
                courseAccount1.setCertificateNo(certificateNo);
            }

            // TODO: YSH - ?????? ?????? ??? ?????? ?????? ?????? ?????? ?????? ?????? ?????? ??????
            // TODO: 2019/11/12 Digital Binder Employee Training Log ?????? -ks Hwang
            binderLogService.createTrainingLog(courseAccountService.save(courseAccount1));
        } else {
            attributes.addFlashAttribute("type", "error");
            attributes.addFlashAttribute("msg", "?????? ???????????? ????????? Complete ?????????????????????. ???????????? ???????????????.");
        }

        return "redirect:/mypage/classroom/surveyCommitMessage/" + surveyActionId;
    }

    // ??????????????? ?????????
    @GetMapping("/classroom/quizCommitMessage/{quizActionId}")
    public String quizCommitMessage(@PathVariable("quizActionId") Long quizActionId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("?????? ??????");

        CourseQuizAction courseQuizAction = courseQuizActionService.getCourseQuizActionById(quizActionId);

        model.addAttribute(pageInfo);
        model.addAttribute("courseQuizAction", courseQuizAction);

        return "content/mypage/classroom/quizCommitMessage";
    }

    // ??????????????? ?????????
    @GetMapping("/classroom/surveyCommitMessage/{surveyActionId}")
    public String surveyCommitMessage(@PathVariable("surveyActionId") Long surveyActionId, Model model) {
        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("?????? ??????");

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

        // ??????????????? ?????? ?????? ??????
        String newFileName = FileUtil.getNewFileName(request, courseSectionFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }

    // employee metrix ??????
    @GetMapping("/pdfview/{id}")
    public String pdfView(@PathVariable("id") Long fileId, Model model) {

//        Schedule schedule =  scheduleService.getTop1BySctypeOrderByCreatedDateDesc(ScheduleType.MATRIX);

        CourseSectionFile courseSectionFile =  courseSectionFileService.getUploadFile(fileId);

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseSectionFile.getCourseSection().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("title", courseSectionFile.getFileName());
        model.addAttribute("fileId", courseSectionFile.getId());

        return "content/mypage/sectionPdfView";
    }


}

