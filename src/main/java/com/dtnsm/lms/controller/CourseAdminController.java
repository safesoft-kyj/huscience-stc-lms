package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.DTO.CourseSimple;
import com.dtnsm.lms.report.CourseReportRepository;
import com.dtnsm.lms.report.JdbcReportController;
import com.dtnsm.lms.repository.CourseRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.querydsl.core.BooleanBuilder;
import org.docx4j.openpackaging.parts.PresentationML.PresentationPropertiesPart;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/course")
public class CourseAdminController {



//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseAdminController.class);

    @Autowired
    UserServiceImpl userService;

    @Autowired
    MailService mailService;

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseSectionService courseSectionService;

    @Autowired
    CourseQuizService courseQuizService;

    @Autowired
    CourseSurveyService courseSurveyService;

    @Autowired
    private CourseFileService courseFileService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private CourseFileService fileService;

    @Autowired
    LmsNotificationService lmsNotificationService;

    @Autowired
    CourseReportRepository courseReportRepository;

    @Autowired
    private DocumentService documentAccountService;

    private PageInfo pageInfo = new PageInfo();

    private CourseMaster courseMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public CourseAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정");

        //courseMaster = courseMasterService.getById("A01");
    }
    //시험퀴즈문제 기본양식 다운로드
    @GetMapping("/quizDownload")
    public void quizDownload(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String fileName = request.getParameter("fileName");
        String filePath = "D:\\UploadFiles\\lms\\";
        File dFile = new File(filePath, fileName);
        //참조 파일 찾기
        InputStream is = new FileInputStream(dFile);
        Context context = new Context(); //참조 파일에 넣을 Context 생성
//      context.putVar("selflist", selfTrainingList); //Context 안에 참조 파일에서 사용할 리스트 선언

        //파일이름 지정 및 저장
        response.setHeader("Content-Disposition", "attachment; filename=\"시험_Sample"+".xlsx\"");

        //JxlsHelper를 통해서 inputstream 내용에 context 반영하여 outputstream에 지정.
        JxlsHelper.getInstance().processTemplate(is, response.getOutputStream(), context);
    }



    @GetMapping("/{typeId}")
    public String listPage(@PathVariable("typeId") String typeId
            , @RequestParam(value = "title", defaultValue = "") String title
            , @RequestParam(value = "active", defaultValue = "") String active
            , @RequestParam(value = "status", defaultValue = "") String status
            , @PageableDefault Pageable pageable
            , Model model) {

        // 초기생성만 되고 타이틀이 없는 경우 삭제
        //courseService.deleteBlankCourse();

        String courseName = courseMasterService.getById(typeId).getCourseName();
        pageInfo.setPageTitle(courseName);

//        Page<Course> courses;
//        if(searchType.equals("all") && searchText.equals("")) {
//            courses = courseService.getPageList(typeId, -1, pageable);
//        } else if(searchType.equals("all") && !searchText.equals("")) {
//            courses = courseService.getPageListByTitleLikeOrContentLike(typeId, searchText, searchText, -1, pageable);
//        } else if (searchType.equals("subject")) {
//            courses = courseService.getPageListByTitleLike(typeId, searchText, -1, pageable);
//        } else {
//            courses = courseService.getPageListByContentLike(typeId, searchText, -1, pageable);
//        }

//        BooleanBuilder builder = new BooleanBuilder();
//        QCourse course = QCourse.course;
//        builder.and(course.courseMaster.id.like("%"));
//        builder.and(course.active.gt(-1));
//
//        if(searchType.equals("all")) {
//            builder.and(course.title.contains(searchText).or(course.content.contains(searchText)));
//        } else if (searchType.equals("subject")) {
//            builder.and(course.title.contains(searchText));
//        } else if (searchType.equals("content")) {
//            builder.and(course.content.contains(searchText));
//        }
//
//        Page<Course> courses = courseRepository.findAll(builder, pageable);


        // jdbc Template 쿼리
        Page<CourseSimple> courses = courseReportRepository.findCourseSimpleByPage(typeId, title.trim(), active.trim(), status.trim(), pageable);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courses);
        model.addAttribute("typeId", typeId);

        return "admin/course/list";
    }


    @GetMapping("/{typeId}/view/{id}")
    public String noticeView(@PathVariable("typeId") String typeId, @PathVariable("id") long id, Model model) {

        Course oldCourse = courseService.getCourseById(id);
        oldCourse.setViewCnt(oldCourse.getViewCnt() + 1);

        Course course= courseService.save(oldCourse);

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName());

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("typeId", typeId);

        return "admin/course/view";
    }


    //  부서별 교육신청서를 조회한다.
    @GetMapping("/{typeId}/popupDocument/{id}")
    public String popupCourse(@PathVariable("typeId") String typeId, @PathVariable("id") int id, Model model, Pageable pageable) {

        Page<Document> documentPorcessList = documentAccountService.getAllByDocument_Template_IdAndIsCommit(id, "0", pageable);
        Page<Document> documentComplteList = documentAccountService.getAllByDocument_Template_IdAndIsCommit(id, "1", pageable);

        for(Document document : documentComplteList) {
//            logger.info(document.getTitle());
        }

        for(Document document : documentPorcessList) {
//            logger.info(document.getTitle());
        }

        model.addAttribute(pageInfo);
        model.addAttribute("processList", documentPorcessList);
        model.addAttribute("completeList", documentComplteList);

        return "admin/course/popupDocument";
    }

    // On Line 교육 등록(self 교육)
    @GetMapping("/{typeId}/addOnline")
    public String addOnLine(@PathVariable("typeId") String typeId, Model model) {

        CourseMaster courseMaster = courseMasterService.getById(typeId);

        Course course = new Course();

        course.setCourseMaster(courseMaster);
        course.setDay(courseMaster.getDay());
        course.setHour(courseMaster.getHour());

        //Course course = courseService.save(new Course("", "", this.courseMaster));

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName());

        // 기본 설문을 가지고 온다.
        List<Survey> surveys = surveyService.getAllByIsActive(1);

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", typeId);
        model.addAttribute("surveys", surveys);
        model.addAttribute("typeId", typeId);

        return "admin/course/addOnLine";
    }

    // Off Line 교육 등록(class, 부서별, 외부 교육)
    @GetMapping("/{typeId}/addOffLine")
    public String addOffLine(@PathVariable("typeId") String typeId, Model model) {

        CourseMaster courseMaster = courseMasterService.getById(typeId);

        Course course = new Course();

        course.setCourseMaster(courseMaster);
        course.setDay(courseMaster.getDay());
        course.setHour(courseMaster.getHour());

        //Course course = courseService.save(new Course("", "", this.courseMaster));

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName());

        // 기본 설문을 가지고 온다.
        List<Survey> surveys = surveyService.getAllByIsActive(1);

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("typeId", typeId);
        model.addAttribute("surveys", surveys);


        return "admin/course/addOffLine";
    }


    @PostMapping("/{typeId}/add-post")
    public String noticeAddPost(@Valid Course course
            , @PathVariable("typeId") String typeId
            , @RequestParam(value = "activeSurvey", required = false, defaultValue = "") Long surveyId
            , @RequestParam(value = "documentId", required = false, defaultValue = "0") long documentId
            , @RequestParam(value = "passCount", required = false, defaultValue = "0") int passCount
            , @RequestParam(value = "examHour", required = false, defaultValue = "0") float examHour
            , @RequestParam("files") MultipartFile[] files
            , @RequestParam(value = "section_file", required = false) MultipartFile section_file
            , @RequestParam(value = "quiz_file", required = false) MultipartFile quiz_file
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , BindingResult result) {
        if(result.hasErrors()) {
            return "redirect:/admin/course/list/" + typeId;
        }

        course.setCourseMaster(courseMasterService.getById(typeId));

        if(course.getCourseMaster().getId().equalsIgnoreCase("BC0101")) {
            if(section_file.getName().equals("") || section_file.getSize() == 0) {
                return String.format("redirect:/admin/course/%s/addOnline", typeId);
            }
        }

        // isAlways : 1:상시, 2:기간 => 상시인 경우 오늘부터 최대일자로 기간을 설정한다.
        if(course.getIsAlways().equals("1")) {
            course.setRequestFromDate(DateUtil.getTodayString());
            course.setRequestToDate(DateUtil.getStringDateAddDay(course.getRequestFromDate(), course.getDay()));

            course.setFromDate(DateUtil.getTodayString());
            course.setToDate(DateUtil.getStringDateAddDay(course.getFromDate(), course.getDay()));
        }

        // 부서별 교육(BC0103), 외부교육(BC0104) 은 신청기간이 없음으로 1900-01-01 로 설정한다.
        if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103") || course.getCourseMaster().getId().equalsIgnoreCase("BC0104")) {
            course.setRequestFromDate("1900-01-01");
            course.setRequestToDate("1900-01-01");

            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0104")) {
                // 외부교육은 바로 신청할 수 있도록 상태를 신청가능 상태로 변경한다(신청가능상태 : 2)
                course.setStatus(2);
            }

            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103")) {
                // 부서별 교육은 신청 프로세스가 없음으로 0
                course.setStatus(0);
            }
        }

        if(course.getPlace() == null || course.getTeam() == null) {
            course.setPlace("");
            course.setTeam("");
        }

        Course course1 = courseService.save(course);

        // 기본적인 강의를 1개 생성한다.
        courseSectionService.CreateAutoSection(course1, section_file);

        // 기본적인 시험 1개 생성한다.(course의 isQuiz가 Y인 경우만 생성된다)
        courseQuizService.CreateAutoQuiz(course1, quiz_file, passCount, examHour);

        // 기본적인 설문 1개 생성한다.(course의 isSurve가 Y인 경우만 생성된다)
        courseSurveyService.CreateAutoSurvey(course1, surveyId);


        Arrays.asList(files)
                .stream()
                .map(file -> courseFileService.storeFile(file, course1))
                .collect(Collectors.toList());


        //  부서별 교육이면 부서별 교육신청서를 등록한다.(2019/11/25 회의시 부서별 교육신청 프로세스를 정상프로세서로 변경 협의하여 주석 처리:임상무님, 이미주 대리)
//        if (course1.getCourseMaster().getId().equals("BC0103")) {
//            Document document = documentService.getById(documentId);
//            if (document != null) {
//
//                for(DocumentCourseAccount documentCourseAccount : document.getDocumentCourseAccountList()) {
////                    approvalCourseProcessService.courseRequestProcess(documentCourseAccount.getAccount(), course1, "0", course1.getFromDate(), course1.getToDate());
//
//                    // 팀장/부서장 승인여부
//                    String isAppr1 = course.getCourseMaster().getIsTeamMangerApproval();
//                    // 관리자 승인여부
//                    String isAppr2 = course.getCourseMaster().getIsCourseMangerApproval();
//
//                    // 교육신청
//                    CourseAccount courseAccount = new CourseAccount();
//                    courseAccount.setCourse(course1);
//                    courseAccount.setAccount(documentCourseAccount.getAccount());
//                    courseAccount.setRequestDate(DateUtil.getTodayString());
//                    courseAccount.setFWdate(DateUtil.getToday());
//                    courseAccount.setRequestType("0");        // 교육신청(0:관리자지정, 1:사용자 신청)
//                    courseAccount.setCourseStatus(CourseStepStatus.complete);
//                    courseAccount.setFStatus("1");
//                    courseAccount.setFCurrSeq(1);
//                    courseAccount.setFromDate(course1.getFromDate());
//                    courseAccount.setToDate(course1.getToDate());
//
//                    // 결재자수 Max 설정
//                    if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
//                        courseAccount.setFFinalCount(2);
//                        courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
//                    } else if(isAppr1.equals("Y")) {
//                        courseAccount.setFFinalCount(1);
//                        courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
//                    } else {
//                        courseAccount.setFFinalCount(0);
//                        courseAccount.setIsApproval("0");   // 전자결재유무 0:없음, 1:있음
//                        courseAccount.setFStatus("1");      // 전자결재가 없으면 완료한것으로 처리한다.
//                        courseAccount.setCourseStatus(CourseStepStatus.complete);
//                    }
//
//                    CourseAccount saveCourseAccount = courseAccountService.save(courseAccount);
//
//                }
//            }
//        }

        return String.format("redirect:/admin/course/%s?page=%s", course1.getCourseMaster().getId(), page);
    }

    // 첨부파일 업로드
//    @PostMapping("/file-post")
//    public String filePost(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
//
//        Course course = courseService.getCourseById(id);
//
//        courseSectionService.storeFile(file, course);
//
//        return "redirect:/admin/course/list/" + course.getCourseMaster().getId();
//    }

    @GetMapping("/{typeId}/editOnLine/{id}")
    public String editOnLine(@PathVariable("typeId") String typeId
            , @PathVariable("id") long id
            , Model model
            , RedirectAttributes attributes) {

        Course course = courseService.getCourseById(id);
        pageInfo.setPageTitle(course.getCourseMaster().getCourseName());

        if(course.getCourseAccountList().size() > 0) {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", "수강자가 있을 경우 교육과정을 변경할 수 없습니다.");
            return "redirect:/admin/course/" + typeId;
        }

        // isAlways : 1:상시, 2:기간 => 상시인 경우 오늘부터 최대일자로 기간을 설정한다.
        if(course.getIsAlways().equals("1")) {
            course.setRequestFromDate(DateUtil.getTodayString());
            course.setRequestToDate("2999-12-31");
            course.setFromDate(DateUtil.getTodayString());
            course.setToDate("2999-12-31");
        }

        // 기본 설문을 가지고 온다.
        List<Survey> surveys = surveyService.getAllByIsActive(1);

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", course.getId());
        model.addAttribute("surveys", surveys);
        model.addAttribute("typeId", typeId);

        return "admin/course/editOnLine";
    }

    @GetMapping("/{typeId}/editOffLine/{id}")
    public String editOffLine(@PathVariable("typeId") String typeId
            , @PathVariable("id") long id
            , Model model
            , RedirectAttributes attributes) {

        Course course = courseService.getCourseById(id);
        pageInfo.setPageTitle(course.getCourseMaster().getCourseName());

        if(course.getCourseAccountList().size() > 0) {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", "수강자가 있을 경우 교육과정을 변경할 수 없습니다.");
            return "redirect:/admin/course/" + typeId;
        }

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", course.getId());
        model.addAttribute("typeId", typeId);

        return "admin/course/editOffLine";
    }

    @PostMapping("/{typeId}/edit-post/{id}")
    public String noticeEditPost(@PathVariable("typeId") String typeId
            , @PathVariable("id") long id
            , @Valid Course course
            , @RequestParam(value = "documentId", required = false, defaultValue = "0") long documentId
            , @RequestParam("files") MultipartFile[] files
            , @RequestParam(value = "page", required = false) String page
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(id);
            return "redirect:/admin/course/list/" + typeId;
        }

        Course oldCourse = courseService.getCourseById(id);

        // isAlways : 1:상시, 2:기간 => 상시인 경우 오늘부터 최대일자로 기간을 설정한다.
        if(oldCourse.getIsAlways().equals(course.getIsAlways())) {
            if(course.getIsAlways().equals("1")) {
                course.setRequestFromDate(DateUtil.getTodayString());
                course.setRequestToDate("2999-12-31");
                course.setFromDate(DateUtil.getTodayString());
                course.setToDate("2999-12-31");
            }
        }

        // 부서별 교육(BC0103), 외부교육(BC0104) 은 신청기간이 없음으로 1900-01-01 로 설정한다.
        if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103") || course.getCourseMaster().getId().equalsIgnoreCase("BC0104")) {
            course.setRequestFromDate("1900-01-01");
            course.setRequestToDate("1900-01-01");

            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0104")) {
                // 외부교육은 바로 신청할 수 있도록 상태를 신청가능 상태로 변경한다(신청가능상태 : 2)
                course.setStatus(2);
            }

            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103")) {
                // 부서별 교육은 참석자 등록을 바로 해야 함으로 상태를 종료상태로 변경한다.
                course.setStatus(5);
            }
        }

        if(course.getPlace() == null || course.getTeam() == null) {
            course.setPlace("");
            course.setTeam("");
        }

        course.setActive(oldCourse.getActive());
        course.setSurveys(oldCourse.getSurveys());
        course.setQuizzes(oldCourse.getQuizzes());
        course.setSections(oldCourse.getSections());
        course.setCourseAccountList(oldCourse.getCourseAccountList());
        course.setCourseMaster(oldCourse.getCourseMaster());
        course.setCourseFiles(oldCourse.getCourseFiles());

//        List<CourseFile> courseFile= oldCourse.getCourseFiles();
//        course.setCourseFiles(courseFile);
//
//        List<CourseAccount> courseAccountList = courseService.getCourseById(id).getCourseAccountList();
//        course.setCourseAccountList(courseAccountList);

        // 교육대상자 수정처리는 별도로 진행한다.(2019/11/17)

//        //  교육 필수대상자 수정 처리
//
//        if(!mails[0].equals("0")) {
//
//            // 교육대상자 알림 메일 보내기
//            for (String userId : mails) {
//
//                Account account = userService.getAccountByUserId(userId);
//                if(account != null) {
//                    CourseAccount courseAccount = new CourseAccount();
//
//                    courseAccount.setCourse(course);
//                    courseAccount.setAccount(account);
//                    courseAccount.setRequestDate(DateUtil.getTodayString());
//                    courseAccount.setRequestType(CourseRequestType.SPECIFY);        // 교육신청유형(관리자지정, 사용자 신청)
//                    courseAccount.setApprovalStatus(ApprovalStatusType.REQUEST_DONE);    // 신청완료(팀장승인진행중)
//                    courseAccount.setApprUserId1(userService.getAccountByUserId(account.getParentUserId()));
//                    courseAccount.setApprUserId2(userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId()));
//                    courseAccount.setIsTeamMangerApproval(course.getCourseMaster().getIsTeamMangerApproval());
//                    courseAccount.setIsCourseMangerApproval(course.getCourseMaster().getIsCourseMangerApproval());
//                    courseAccountList.add(courseAccount);
//                }
//            }
//        }

//        if(!mails2[0].equals("0")) {
//
//            // 교육대상자 알림 메일 보내기
//            for (String userId : mails2) {
//                Account account = userService.getAccountByUserId(userId);
//                if(account != null) {
//                    CourseAccount courseAccount = new CourseAccount();
//
//                    courseAccount.setCourse(course);
//                    courseAccount.setAccount(account);
//                    courseAccount.setRequestDate(DateUtil.getTodayString());
//                    courseAccount.setRequestType(CourseRequestType.SPECIFY);        // 교육신청유형(관리자지정, 사용자 신청)
//                    courseAccount.setApprovalStatus(ApprovalStatusType.REQUEST_DONE);    // 신청완료(팀장승인진행중)
//                    courseAccount.setApprUserId1(userService.getAccountByUserId(account.getParentUserId()));
//                    courseAccount.setApprUserId2(userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId()));
//                    courseAccount.setIsTeamMangerApproval(course.getCourseMaster().getIsTeamMangerApproval());
//                    courseAccount.setIsCourseMangerApproval(course.getCourseMaster().getIsCourseMangerApproval());
//                    courseAccountList.add(courseAccount);
//                }
//            }
//        }
//
//        course.setCourseAccountList(courseAccountList);

        // self 교육인 경우 신청일자 및 교육일자가 별도로 없음으로 기본값으로 셋팅한다.
        // RequestType : 1:상시, 2:기간
//        if(course.getCourseMaster().getRequestType().equals("1")) {
//            course.setRequestFromDate("1900-01-01");
//            course.setRequestToDate("1900-01-01");
//            course.setFromDate("1900-01-01");
//            course.setToDate("1900-01-01");
//        }

        //  교육필수 대상자 수정 처리 완료
//        Course course1 = courseService.save(course);

        Course course1 = courseService.save(course);

        Arrays.asList(files)
                .stream()
                .map(file -> courseFileService.storeFile(file, course1))
                .collect(Collectors.toList());

        return String.format("redirect:/admin/course/%s?page=%s", typeId, page);
    }


    @GetMapping("/{typeId}/updateActive/{id}")
    public String updateActive(@PathVariable("typeId") String typeId
            , @RequestParam(value = "page", required = false) String page
            , @RequestParam(value = "active", required = false, defaultValue = "") String active
            , @RequestParam(value = "status", required = false, defaultValue = "") String status
            , @RequestParam(value = "title", required = false, defaultValue = "") String title
            , @PathVariable("id") long id) {

        Course course = courseService.getCourseById(id);

        if (course.getActive() == 0) {
            // 교육과정을 신청할 수 있는 상태로 변경한다.
            course.setActive(1);
        } else if (course.getActive() == 1) {

            // 직원이 신청한 내역이 있으면 Active를 변경할 수 없다.
//            if(course.getCourseAccountList().size() <= 0) {
//                course.setActive(0);
//            }

            course.setActive(0);
        }

        courseService.save(course);

        return String.format("redirect:/admin/course/%s?page=%s&active=%s&status=%s&title=%s", typeId, page, active, status, title);
    }

    @GetMapping("/{typeId}/delete/{id}")
    public String noticeDelete(@PathVariable("typeId") String typeId
            , @PathVariable("id") long courseId
            , @RequestParam(value = "page", required = false, defaultValue = "") String page
            , @RequestParam(value = "active", required = false, defaultValue = "") String active
            , @RequestParam(value = "status", required = false, defaultValue = "") String status
            , @RequestParam(value = "title", required = false, defaultValue = "") String title
            , HttpServletRequest request, RedirectAttributes attributes) {

        Course course = courseService.getCourseById(courseId);

        if(course.getCourseAccountList().size() > 0) {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", "수강자가 있을 경우 교육과정을 변경할 수 없습니다.");
            return "redirect:/admin/course/" + typeId;
        }

        // 과정 신청된 내역이 있으면 삭제 하지 않는다.
        if (course.getCourseAccountList().size() <= 0) {
            try {
                // 과정을 삭제할때는 로그까지 함께 삭제한다.
                for (LmsNotification lmsNotification : lmsNotificationService.getAllByCourseIdNotification(courseId)) {
                    lmsNotificationService.delete(lmsNotification);
                }
                courseService.delete(course);

            } catch(Exception ex) {
                ex.printStackTrace();
            }
        } else {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", "해당 과정을 수강중인 사용자가 있습니다.");
        }

        // 이전 URL를 리턴한다.
//        String refUrl = request.getHeader("referer");
//        return "redirect:" +  refUrl;
        return String.format("redirect:/admin/course/%s?page=%s&active=%s&status=%s&title=%s", typeId, page, active, status, title);
    }


    @GetMapping("/{typeId}/delete-file/{course_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable("typeId") String typeId
            , @PathVariable long course_id
            , @PathVariable long file_id
            , @RequestParam(value = "page", required = false) String page
            , HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        Course course = courseService.getCourseById(course_id);

        // 이전 URL를 리턴한다.
        String refUrl = request.getHeader("referer");

        return "redirect:" + refUrl;
    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        CourseFile courseFile = fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(courseFile.getSaveName());

        // Try to determine file's content type
//        String contentType = mimeTypesMap.getContentType(courseFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        String contentType = courseFile.getMimeType();

        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, courseFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + courseFile.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + newFileName + "\"")
                .body(resource);
    }

    @GetMapping("/{typeId}/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable("typeId") String typeId, @PathVariable long id, HttpServletRequest request){

        CourseFile courseSection = fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(courseSection.getSaveName());

        // Try to determine file's content type
        String contentType = courseSection.getMimeType();
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, courseSection.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +  newFileName + "\"")
                .body(resource);
    }
}
