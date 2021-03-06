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
        pageInfo.setParentTitle("????????????");

        //courseMaster = courseMasterService.getById("A01");
    }
    //?????????????????? ???????????? ????????????
    @GetMapping("/quizDownload")
    public void quizDownload(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String fileName = request.getParameter("fileName");
        String filePath = "D:\\UploadFiles\\lms\\";
        File dFile = new File(filePath, fileName);
        //?????? ?????? ??????
        InputStream is = new FileInputStream(dFile);
        Context context = new Context(); //?????? ????????? ?????? Context ??????
//      context.putVar("selflist", selfTrainingList); //Context ?????? ?????? ???????????? ????????? ????????? ??????

        //???????????? ?????? ??? ??????
        response.setHeader("Content-Disposition", "attachment; filename=\"??????_Sample"+".xlsx\"");

        //JxlsHelper??? ????????? inputstream ????????? context ???????????? outputstream??? ??????.
        JxlsHelper.getInstance().processTemplate(is, response.getOutputStream(), context);
    }



    @GetMapping("/{typeId}")
    public String listPage(@PathVariable("typeId") String typeId
            , @RequestParam(value = "title", defaultValue = "") String title
            , @RequestParam(value = "active", defaultValue = "") String active
            , @RequestParam(value = "status", defaultValue = "") String status
            , @PageableDefault Pageable pageable
            , Model model) {

        // ??????????????? ?????? ???????????? ?????? ?????? ??????
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


        // jdbc Template ??????
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


    //  ????????? ?????????????????? ????????????.
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

    // On Line ?????? ??????(self ??????)
    @GetMapping("/{typeId}/addOnline")
    public String addOnLine(@PathVariable("typeId") String typeId, Model model) {

        CourseMaster courseMaster = courseMasterService.getById(typeId);

        Course course = new Course();

        course.setCourseMaster(courseMaster);
        course.setDay(courseMaster.getDay());
        course.setHour(courseMaster.getHour());

        //Course course = courseService.save(new Course("", "", this.courseMaster));

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName());

        // ?????? ????????? ????????? ??????.
        List<Survey> surveys = surveyService.getAllByIsActive(1);

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", typeId);
        model.addAttribute("surveys", surveys);
        model.addAttribute("typeId", typeId);

        return "admin/course/addOnLine";
    }

    // Off Line ?????? ??????(class, ?????????, ?????? ??????)
    @GetMapping("/{typeId}/addOffLine")
    public String addOffLine(@PathVariable("typeId") String typeId, Model model) {

        CourseMaster courseMaster = courseMasterService.getById(typeId);

        Course course = new Course();

        course.setCourseMaster(courseMaster);
        course.setDay(courseMaster.getDay());
        course.setHour(courseMaster.getHour());

        //Course course = courseService.save(new Course("", "", this.courseMaster));

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName());

        // ?????? ????????? ????????? ??????.
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

        // isAlways : 1:??????, 2:?????? => ????????? ?????? ???????????? ??????????????? ????????? ????????????.
        if(course.getIsAlways().equals("1")) {
            course.setRequestFromDate(DateUtil.getTodayString());
            course.setRequestToDate(DateUtil.getStringDateAddDay(course.getRequestFromDate(), course.getDay()));

            course.setFromDate(DateUtil.getTodayString());
            course.setToDate(DateUtil.getStringDateAddDay(course.getFromDate(), course.getDay()));
        }

        // ????????? ??????(BC0103), ????????????(BC0104) ??? ??????????????? ???????????? 1900-01-01 ??? ????????????.
        if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103") || course.getCourseMaster().getId().equalsIgnoreCase("BC0104")) {
            course.setRequestFromDate("1900-01-01");
            course.setRequestToDate("1900-01-01");

            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0104")) {
                // ??????????????? ?????? ????????? ??? ????????? ????????? ???????????? ????????? ????????????(?????????????????? : 2)
                course.setStatus(2);
            }

            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103")) {
                // ????????? ????????? ?????? ??????????????? ???????????? 0
                course.setStatus(0);
            }
        }

        if(course.getPlace() == null || course.getTeam() == null) {
            course.setPlace("");
            course.setTeam("");
        }

        Course course1 = courseService.save(course);

        // ???????????? ????????? 1??? ????????????.
        courseSectionService.CreateAutoSection(course1, section_file);

        // ???????????? ?????? 1??? ????????????.(course??? isQuiz??? Y??? ????????? ????????????)
        courseQuizService.CreateAutoQuiz(course1, quiz_file, passCount, examHour);

        // ???????????? ?????? 1??? ????????????.(course??? isSurve??? Y??? ????????? ????????????)
        courseSurveyService.CreateAutoSurvey(course1, surveyId);


        Arrays.asList(files)
                .stream()
                .map(file -> courseFileService.storeFile(file, course1))
                .collect(Collectors.toList());


        //  ????????? ???????????? ????????? ?????????????????? ????????????.(2019/11/25 ????????? ????????? ???????????? ??????????????? ????????????????????? ?????? ???????????? ?????? ??????:????????????, ????????? ??????)
//        if (course1.getCourseMaster().getId().equals("BC0103")) {
//            Document document = documentService.getById(documentId);
//            if (document != null) {
//
//                for(DocumentCourseAccount documentCourseAccount : document.getDocumentCourseAccountList()) {
////                    approvalCourseProcessService.courseRequestProcess(documentCourseAccount.getAccount(), course1, "0", course1.getFromDate(), course1.getToDate());
//
//                    // ??????/????????? ????????????
//                    String isAppr1 = course.getCourseMaster().getIsTeamMangerApproval();
//                    // ????????? ????????????
//                    String isAppr2 = course.getCourseMaster().getIsCourseMangerApproval();
//
//                    // ????????????
//                    CourseAccount courseAccount = new CourseAccount();
//                    courseAccount.setCourse(course1);
//                    courseAccount.setAccount(documentCourseAccount.getAccount());
//                    courseAccount.setRequestDate(DateUtil.getTodayString());
//                    courseAccount.setFWdate(DateUtil.getToday());
//                    courseAccount.setRequestType("0");        // ????????????(0:???????????????, 1:????????? ??????)
//                    courseAccount.setCourseStatus(CourseStepStatus.complete);
//                    courseAccount.setFStatus("1");
//                    courseAccount.setFCurrSeq(1);
//                    courseAccount.setFromDate(course1.getFromDate());
//                    courseAccount.setToDate(course1.getToDate());
//
//                    // ???????????? Max ??????
//                    if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
//                        courseAccount.setFFinalCount(2);
//                        courseAccount.setIsApproval("1");   // ?????????????????? 0:??????, 1:??????
//                    } else if(isAppr1.equals("Y")) {
//                        courseAccount.setFFinalCount(1);
//                        courseAccount.setIsApproval("1");   // ?????????????????? 0:??????, 1:??????
//                    } else {
//                        courseAccount.setFFinalCount(0);
//                        courseAccount.setIsApproval("0");   // ?????????????????? 0:??????, 1:??????
//                        courseAccount.setFStatus("1");      // ??????????????? ????????? ?????????????????? ????????????.
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

    // ???????????? ?????????
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
            attributes.addFlashAttribute("msg", "???????????? ?????? ?????? ??????????????? ????????? ??? ????????????.");
            return "redirect:/admin/course/" + typeId;
        }

        // isAlways : 1:??????, 2:?????? => ????????? ?????? ???????????? ??????????????? ????????? ????????????.
        if(course.getIsAlways().equals("1")) {
            course.setRequestFromDate(DateUtil.getTodayString());
            course.setRequestToDate("2999-12-31");
            course.setFromDate(DateUtil.getTodayString());
            course.setToDate("2999-12-31");
        }

        // ?????? ????????? ????????? ??????.
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
            attributes.addFlashAttribute("msg", "???????????? ?????? ?????? ??????????????? ????????? ??? ????????????.");
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

        // isAlways : 1:??????, 2:?????? => ????????? ?????? ???????????? ??????????????? ????????? ????????????.
        if(oldCourse.getIsAlways().equals(course.getIsAlways())) {
            if(course.getIsAlways().equals("1")) {
                course.setRequestFromDate(DateUtil.getTodayString());
                course.setRequestToDate("2999-12-31");
                course.setFromDate(DateUtil.getTodayString());
                course.setToDate("2999-12-31");
            }
        }

        // ????????? ??????(BC0103), ????????????(BC0104) ??? ??????????????? ???????????? 1900-01-01 ??? ????????????.
        if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103") || course.getCourseMaster().getId().equalsIgnoreCase("BC0104")) {
            course.setRequestFromDate("1900-01-01");
            course.setRequestToDate("1900-01-01");

            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0104")) {
                // ??????????????? ?????? ????????? ??? ????????? ????????? ???????????? ????????? ????????????(?????????????????? : 2)
                course.setStatus(2);
            }

            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103")) {
                // ????????? ????????? ????????? ????????? ?????? ?????? ????????? ????????? ??????????????? ????????????.
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

        // ??????????????? ??????????????? ????????? ????????????.(2019/11/17)

//        //  ?????? ??????????????? ?????? ??????
//
//        if(!mails[0].equals("0")) {
//
//            // ??????????????? ?????? ?????? ?????????
//            for (String userId : mails) {
//
//                Account account = userService.getAccountByUserId(userId);
//                if(account != null) {
//                    CourseAccount courseAccount = new CourseAccount();
//
//                    courseAccount.setCourse(course);
//                    courseAccount.setAccount(account);
//                    courseAccount.setRequestDate(DateUtil.getTodayString());
//                    courseAccount.setRequestType(CourseRequestType.SPECIFY);        // ??????????????????(???????????????, ????????? ??????)
//                    courseAccount.setApprovalStatus(ApprovalStatusType.REQUEST_DONE);    // ????????????(?????????????????????)
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
//            // ??????????????? ?????? ?????? ?????????
//            for (String userId : mails2) {
//                Account account = userService.getAccountByUserId(userId);
//                if(account != null) {
//                    CourseAccount courseAccount = new CourseAccount();
//
//                    courseAccount.setCourse(course);
//                    courseAccount.setAccount(account);
//                    courseAccount.setRequestDate(DateUtil.getTodayString());
//                    courseAccount.setRequestType(CourseRequestType.SPECIFY);        // ??????????????????(???????????????, ????????? ??????)
//                    courseAccount.setApprovalStatus(ApprovalStatusType.REQUEST_DONE);    // ????????????(?????????????????????)
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

        // self ????????? ?????? ???????????? ??? ??????????????? ????????? ???????????? ??????????????? ????????????.
        // RequestType : 1:??????, 2:??????
//        if(course.getCourseMaster().getRequestType().equals("1")) {
//            course.setRequestFromDate("1900-01-01");
//            course.setRequestToDate("1900-01-01");
//            course.setFromDate("1900-01-01");
//            course.setToDate("1900-01-01");
//        }

        //  ???????????? ????????? ?????? ?????? ??????
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
            // ??????????????? ????????? ??? ?????? ????????? ????????????.
            course.setActive(1);
        } else if (course.getActive() == 1) {

            // ????????? ????????? ????????? ????????? Active??? ????????? ??? ??????.
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
            attributes.addFlashAttribute("msg", "???????????? ?????? ?????? ??????????????? ????????? ??? ????????????.");
            return "redirect:/admin/course/" + typeId;
        }

        // ?????? ????????? ????????? ????????? ?????? ?????? ?????????.
        if (course.getCourseAccountList().size() <= 0) {
            try {
                // ????????? ??????????????? ???????????? ?????? ????????????.
                for (LmsNotification lmsNotification : lmsNotificationService.getAllByCourseIdNotification(courseId)) {
                    lmsNotificationService.delete(lmsNotification);
                }
                courseService.delete(course);

            } catch(Exception ex) {
                ex.printStackTrace();
            }
        } else {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", "?????? ????????? ???????????? ???????????? ????????????.");
        }

        // ?????? URL??? ????????????.
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

        // db??? ?????? ??????
        fileService.deleteFile(file_id);

        Course course = courseService.getCourseById(course_id);

        // ?????? URL??? ????????????.
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

        // ??????????????? ?????? ?????? ??????
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

        // ??????????????? ?????? ?????? ??????
        String newFileName = FileUtil.getNewFileName(request, courseSection.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +  newFileName + "\"")
                .body(resource);
    }
}
