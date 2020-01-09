package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
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
import org.springframework.web.bind.annotation.*;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/course")
public class CourseController {

//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    CourseAccountOrderService courseAccountOrderService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    private CourseFileService fileService;

    @Autowired
    private ApprovalCourseProcessService approvalCourseProcessService;

    private PageInfo pageInfo = new PageInfo();

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public CourseController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정");
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
            courses = courseService.getPageList(typeId, 0, pageable);
        } else if(searchType.equals("all") && !searchText.equals("")) {
            courses = courseService.getPageListByTitleLikeOrContentLike(typeId, searchText, searchText, 0, pageable);
        } else if (searchType.equals("subject")) {
            courses = courseService.getPageListByTitleLike(typeId, searchText, 0, pageable);
        } else {
            courses = courseService.getPageListByContentLike(typeId, searchText, 0, pageable);
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
        pageInfo.setPageTitle(course.getCourseMaster().getCourseName());

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());


        model.addAttribute(pageInfo);
        model.addAttribute("course", course);
        model.addAttribute("account", account);

        return "content/course/view";
    }

    // 결재현황
    @GetMapping("/approval/{id}")
    public String approval(@PathVariable("id") long id, Model model) {

        CourseAccount courseAccount = courseAccountService.getById(id);

        pageInfo.setPageTitle(courseAccount.getCourse().getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", courseAccount.getCourse());
        model.addAttribute("courseAccount", courseAccount);

        return "content/course/approval";
    }

    // 결재 승인
    @GetMapping("/approvalAppr1/{id}")
    public String approvalAppr1(@PathVariable("id") long orderId, Model model) {

        CourseAccountOrder courseAccountOrder = courseAccountOrderService.getById(orderId);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        pageInfo.setPageTitle(courseAccount.getCourse().getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", courseAccount.getCourse());
        model.addAttribute("courseAccount", courseAccount);
        model.addAttribute("courseAccountOrder", courseAccountOrder);
        model.addAttribute("userId", SessionUtil.getUserId());

        return "content/course/approvalAppr1";
    }


    // 결재 승인
    @GetMapping("/approvalAppr2/{id}")
    public String approvalAppr2(@PathVariable("id") long docId, Model model) {

        CourseAccount courseAccount = courseAccountService.getById(docId);

        pageInfo.setPageTitle(courseAccount.getCourse().getCourseMaster().getCourseName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", courseAccount.getCourse());
        model.addAttribute("courseAccount", courseAccount);
        model.addAttribute("courseAccountOrder", courseAccount.getCourseAccountOrders());
        model.addAttribute("userId", SessionUtil.getUserId());

        return "content/course/approvalAppr1";
    }


    // 내부직원의 교육신청 처리
    @GetMapping("/request/{id}")
    public String request(@PathVariable("id") long id, Model model) {

        Course course = courseService.getCourseById(id);
        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        // 교육 신청 처리(requestType 0:관리자 지정, 1:신청)
        approvalCourseProcessService.courseRequestProcess(account, course, "1");

        return "redirect:/course/request/commit/" + id;
    }

    // 교육필수대상자로 지정된 사람에 대한 신청처리
    @GetMapping("/request2/{id}")
    public String request2(@PathVariable("id") long docId, Model model) {

        CourseAccount courseAccount = courseAccountService.getById(docId);

        // 교육 신청 처리(requestType 0:관리자 지정, 1:신청)
        approvalCourseProcessService.courseRequestProcess(courseAccount.getAccount(), courseAccount.getCourse(), "1");

        return "redirect:/course/request/commit/" + courseAccount.getCourse().getId();
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
}