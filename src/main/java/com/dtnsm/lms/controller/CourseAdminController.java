package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/course")
public class CourseAdminController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseAdminController.class);

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
    private DocumentService documentAccountService;

    private PageInfo pageInfo = new PageInfo();

    private CourseMaster courseMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public CourseAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정");

        //courseMaster = courseMasterService.getById("A01");
    }


    // region # 공지사항
    @GetMapping("/list/{typeId}")
    public String listPage(@PathVariable("typeId") String typeId
            , @RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @PageableDefault Pageable pageable
            , Model model) {

        // 초기생성만 되고 타이틀이 없는 경우 삭제
        //courseService.deleteBlankCourse();

        String courseName = courseMasterService.getById(typeId).getCourseName();
        pageInfo.setPageTitle(courseName + "조회");

        Page<Course> courses;
        if(searchType.equals("all") && searchText.equals("")) {
            courses = courseService.getPageList(typeId, -1, pageable);
        } else if(searchType.equals("all") && !searchText.equals("")) {
            courses = courseService.getPageListByTitleLikeOrContentLike(typeId, searchText, searchText, -1, pageable);
        } else if (searchType.equals("subject")) {
            courses = courseService.getPageListByTitleLike(typeId, searchText, -1, pageable);
        } else {
            courses = courseService.getPageListByContentLike(typeId, searchText, -1, pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courses);
        model.addAttribute("typeId", typeId);

        return "admin/course/list";
    }


    @GetMapping("/view/{id}")
    public String noticeView(@PathVariable("id") long id, Model model) {

        Course oldCourse = courseService.getCourseById(id);
        oldCourse.setViewCnt(oldCourse.getViewCnt() + 1);

        Course course= courseService.save(oldCourse);

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);

        return "admin/course/view";
    }


    //  부서별 교육신청서를 조회한다.
    @GetMapping("/popupDocument/{id}")
    public String popupCourse(@PathVariable("id") int id, Model model, Pageable pageable) {

        Page<Document> documentPorcessList = documentAccountService.getAllByDocument_Template_IdAndIsCommit(id, "0", pageable);
        Page<Document> documentComplteList = documentAccountService.getAllByDocument_Template_IdAndIsCommit(id, "1", pageable);

        for(Document document : documentComplteList) {
            logger.info(document.getTitle());
        }

        for(Document document : documentPorcessList) {
            logger.info(document.getTitle());
        }

        model.addAttribute(pageInfo);
        model.addAttribute("processList", documentPorcessList);
        model.addAttribute("completeList", documentComplteList);

        return "admin/course/popupDocument";
    }


    @GetMapping("/add/{typeId}")
    public String noticeAdd(@PathVariable("typeId") String typeId, Model model) {

        this.courseMaster = courseMasterService.getById(typeId);
        Course course = new Course();
        course.setCourseMaster(this.courseMaster);
        course.setDay(1);

        //Course course = courseService.save(new Course("", "", this.courseMaster));

        pageInfo.setPageTitle(courseMaster.getCourseName() + " 등록");

        Survey survey = surveyService.getByIsActive(1);

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", typeId);
        model.addAttribute("survey", survey);

        String formName = "add";

        if(typeId.equals("BC0101")) {   // Self training
            formName = "add_self";
        } else if(typeId.equals("BC0102")) {    // class Training
            formName = "add_class";
        } else if (typeId.equals("BC0103")) {   // 부서별 교육
            formName = "add_class";
        } else if (typeId.equals("BC0104")) {   // 외부 교육
            formName = "add_class";
        }

        return "admin/course/" + formName;
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid Course course
            , @RequestParam(value = "documentId", required = false, defaultValue = "0") long documentId
            , @RequestParam("files") MultipartFile[] files
            , @RequestParam(value = "section_file", required = false) MultipartFile section_file
            , @RequestParam(value = "quiz_file", required = false) MultipartFile quiz_file
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/add";
        }

        course.setCourseMaster(courseMasterService.getById(course.getCourseMaster().getId()));

        // self 교육인 경우 신청일자 및 교육일자가 별도로 없음으로 기본값으로 셋팅한다.
        // RequestType : 1:상시, 2:기간
//        if(course.getCourseMaster().getRequestType().equals("1")) {
//            course.setRequestFromDate("1900-01-01");
//            course.setRequestToDate("2999-12-31");
//            course.setFromDate("1900-01-01");
//            course.setToDate("2999-12-31");
//        }

        Course course1 = courseService.save(course);

        // 기본적인 강의를 1개 생성한다.
        courseSectionService.CreateAutoSection(course1, section_file);

        // 기본적인 시험 1개 생성한다.(course의 isQuiz가 Y인 경우만 생성된다)
        courseQuizService.CreateAutoQuiz(course1, quiz_file);

        // 기본적인 설문 1개 생성한다.(course의 isSurve가 Y인 경우만 생성된다)
        courseSurveyService.CreateAutoSurvey(course1);


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

        return "redirect:/admin/course/list/" + course1.getCourseMaster().getId();
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

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        Course course = courseService.getCourseById(id);
        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", course.getId());

        String typeId = course.getCourseMaster().getId();
        String formName = "edit";

        if(typeId.equals("BC0101")) {   // Self training
            formName = "edit_self";
        } else if(typeId.equals("BC0102")) {    // class Training
            formName = "edit_class";
        } else if (typeId.equals("BC0103")) {   // 부서별 교육
            formName = "edit_class";
        } else if (typeId.equals("BC0104")) {   // 외부 교육
            formName = "edit_class";
        }

        return "admin/course/" + formName;
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid Course course
            , @RequestParam(value = "documentId", required = false, defaultValue = "0") long documentId
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(id);
            return "/admin/course/list/" + course.getCourseMaster().getId();
        }

        Course oldCourse = courseService.getCourseById(id);

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

        return "redirect:/admin/course/list/" + course1.getCourseMaster().getId();
    }


    @GetMapping("/updateActive/{id}")
    public String updateActive(@PathVariable("id") long id) {

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

        course = courseService.save(course);

        String courseMastId = course.getCourseMaster().getId();

        return "redirect:/admin/course/list/" + courseMastId;
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Course course = courseService.getCourseById(id);

        String courseMastId = course.getCourseMaster().getId();

        // 과정 신청된 내역이 있으면 삭제 하지 않는다.
        if (course.getCourseAccountList().size() <= 0) {

            courseService.delete(course);
        }

        return "redirect:/admin/course/list/" + courseMastId;
    }


    @GetMapping("/delete-file/{course_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable long course_id, @PathVariable long file_id, HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        return "redirect:/admin/course/edit/" + course_id;

    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        CourseFile courseSection = fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(courseSection.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(courseSection.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, courseSection.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +  newFileName + "\"")
                .body(resource);
    }



}
