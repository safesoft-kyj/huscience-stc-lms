package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
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

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    CourseService courseService;

//    @Autowired
//    CourseAccountService courseAccountService;

    @Autowired
    private CourseAccountService courseAccountService;

    @Autowired
    private CourseSectionService courseSectionService;

    @Autowired
    private CourseFileService courseFileService;

    @Autowired
    private CourseFileService fileService;


    @Autowired
    private CourseManagerService courseManagerService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private UserMapperService userMapperService;

    private PageInfo pageInfo = new PageInfo();

    private CourseMaster courseMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public CourseAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("공지사항");

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

    @GetMapping("/add/{typeId}")
    public String noticeAdd(@PathVariable("typeId") String typeId, Model model) {

        this.courseMaster = courseMasterService.getById(typeId);
        Course course = new Course();
        course.setCourseMaster(this.courseMaster);

        //Course course = courseService.save(new Course("", "", this.courseMaster));

        pageInfo.setPageTitle(courseMaster.getCourseName() + " 등록");




        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", typeId);



        model.addAttribute("mailList", userService.getAccountList());

        return "admin/course/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid Course course
            , @RequestParam("files") MultipartFile[] files
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/add";
        }

        course.setCourseMaster(courseMasterService.getById(course.getCourseMaster().getId()));

        boolean isMail = mails[0].equals("0") ? false : true;

        // self 교육인 경우 신청일자 및 교육일자가 별도로 없음으로 기본값으로 셋팅한다.
        // RequestType : 1:상시, 2:기간
//        if(course.getCourseMaster().getRequestType().equals("1")) {
//            course.setRequestFromDate("1900-01-01");
//            course.setRequestToDate("1900-01-01");
//            course.setFromDate("1900-01-01");
//            course.setToDate("1900-01-01");
//        }

        if (isMail) {
            course.setMailSender(Arrays.toString(mails));
        }
        Course course1 = courseService.save(course);

        Arrays.asList(files)
                .stream()
                .map(file -> courseFileService.storeFile(file, course1))
                .collect(Collectors.toList());

        if(isMail) {

            // 교육대상자 등록
            for(String userId : mails) {

                Account account = userService.findByUserId(userId);
                CourseAccount courseAccount = new CourseAccount();
                courseAccount.setCourse(course1);
                courseAccount.setAccount(account);
                courseAccount.setRequestDate(DateUtil.getTodayString());
                courseAccount.setRequestType(CourseRequestType.SPECIFY);
                courseAccount.setApprUserId1(userService.getAccountByUserId(account.getParentUserId()));
                courseAccount.setApprUserId2(userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId()));
                courseAccount.setIsTeamMangerApproval(course1.getCourseMaster().getIsTeamMangerApproval());
                courseAccount.setIsCourseMangerApproval(course1.getCourseMaster().getIsCourseMangerApproval());

                courseAccountService.save(courseAccount);

//                Account account = userService.findByUserId(userId);
//                CourseAccount courseAccount = new CourseAccount();
//                courseAccount.setCourse(course);
//                courseAccount.setAccount(account);
//                courseAccountService.save(courseAccount);
            }

            // 메일 보내기
//            for(String userId : mails) {
//
//                String email = userMapperService.getUserById(userId).getEmail();
//
//                // 메일보내기(서비스 전까지는 메일 보내지 않음
//                Mail mail = new Mail();
//                //mail.setEmail(email);
//                mail.setEmail("ks.hwang@safesoft.co.kr");
//                mail.setMessage(course1.getContent());
//                mail.setObject(course1.getTitle());
//
//                mailService.send(mail);
//            }
        }

        return "redirect:/admin/course/edit/" + course1.getId();
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

        List<Account> accountList = new ArrayList<>();
        for(CourseAccount courseAccount : course.getCourseAccountList()) {
            accountList.add(courseAccount.getAccount());
        }

        pageInfo.setPageTitle(course.getCourseMaster().getCourseName() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("id", course.getId());
        model.addAttribute("accounts", accountList);
//        model.addAttribute("mailList", userMapperService.getUserAll());
        model.addAttribute("mailList", userService.getAccountList());

        return "admin/course/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid Course course
            , @RequestParam("files") MultipartFile[] files
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , @RequestParam(value = "mailList2", required = false, defaultValue = "0") String[] mails2
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(id);
            return "/admin/course/list/" + course.getCourseMaster().getId();
        }

        course.setSurveys(courseService.getCourseById(course.getId()).getSurveys());
        course.setQuizzes(courseService.getCourseById(course.getId()).getQuizzes());
        course.setSections(courseService.getCourseById(course.getId()).getSections());

        List<CourseFile> courseFile= courseService.getCourseById(id).getCourseFiles();
        course.setCourseFiles(courseFile);
        course.setSections(courseService.getCourseById(id).getSections());
        course.setQuizzes(courseService.getCourseById(id).getQuizzes());
        course.setSurveys(courseService.getCourseById(id).getSurveys());

        List<CourseAccount> courseAccountList = courseService.getCourseById(id).getCourseAccountList();
        courseAccountList.clear();


        //  교육 필수대상자 수정 처리

        if(!mails[0].equals("0")) {

            // 교육대상자 알림 메일 보내기
            for (String userId : mails) {

                Account account = userService.getAccountByUserId(userId);
                if(account != null) {
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
                }
            }
        }

        if(!mails2[0].equals("0")) {

            // 교육대상자 알림 메일 보내기
            for (String userId : mails2) {
                Account account = userService.getAccountByUserId(userId);
                if(account != null) {
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
                }
            }
        }

        course.setCourseAccountList(courseAccountList);

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

        return "redirect:/admin/course/edit/" + id;
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Course course = courseService.getCourseById(id);

        String courseMastId = course.getCourseMaster().getId();

        courseService.delete(course);

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
