package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.CourseAccountService;
import com.dtnsm.lms.service.CourseSectionFileService;
import com.dtnsm.lms.service.CourseSectionService;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
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
    UserServiceImpl userService;

    @Autowired
    private CourseSectionFileService courseSectionFileService;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();

    public MyPageController() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("마이페이지");
    }

    @GetMapping("/main")
    public String main(Model model) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("메인");

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = userService.getAccountByUserId(userDetails.getUserId());
        List<CourseAccount> courseAccountList = courseAccountService.getCourseAccountByUserId(userDetails.getUserId());

        model.addAttribute(pageInfo);
        model.addAttribute("courseAccountList", courseAccountList);

        return "content/mypage/main";
    }

    @GetMapping("/quiz")
    public String quiz(Model model) {

        pageInfo.setPageId("m-mypage-quiz");
        pageInfo.setPageTitle("시험/시험결과/수료증발급");
        model.addAttribute(pageInfo);

        return "content/mypage/main";
    }

    @GetMapping("/approval")
    public String approval(Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");
        model.addAttribute(pageInfo);

        return "content/mypage/approval";
    }

    @GetMapping("/myinfo")
    public String myInfo(Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("사용자정보");
        model.addAttribute(pageInfo);

        return "content/mypage/myinfo";
    }

    @GetMapping("/classroom/view/{id}")
    public String myInfo(@PathVariable("id") Long courseId, Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("강의목차");

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = userService.getAccountByUserId(userDetails.getUserId());
//        List<CourseAccount> courseAccountList = courseAccountService.getCourseAccountByUserId(userDetails.getUserId());

        Course course = courseService.getCourseById(courseId);

        model.addAttribute(pageInfo);
        model.addAttribute("course", course);

        return "content/mypage/classroom/view";
    }

    @GetMapping("/classroom/pdfview/{id}")
    public String pdfView(@PathVariable("id") Long fileId, Model model) {

        CourseSectionFile courseSectionFile = courseSectionFileService.getUploadFile(fileId);

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle(courseSectionFile.getCourseSection().getName());

        model.addAttribute(pageInfo);
        model.addAttribute("courseSectionFile", courseSectionFile);

        return "content/mypage/classroom/pdfview";
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
