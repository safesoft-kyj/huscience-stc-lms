//package com.dtnsm.lms.controller;
//
//import com.dtnsm.lms.domain.CourseMaster;
//import com.dtnsm.lms.domain.ElMinor;
//import com.dtnsm.lms.repository.CourseCertificateLogRepository;
//import com.dtnsm.lms.repository.CourseCertificateNumberRepository;
//import com.dtnsm.lms.service.CodeService;
//import com.dtnsm.lms.service.CourseCertificateService;
//import com.dtnsm.lms.service.CourseMasterService;
//import com.dtnsm.lms.service.CourseService;
//import com.dtnsm.lms.util.PageInfo;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.validation.Valid;
//import java.util.List;
//
//@Controller
//@RequestMapping("/admin/certificate")
//public class CertificateController {
//
//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CertificateController.class);
//
//    @Autowired
//    CourseCertificateService courseCertificateService;
//
//    @Autowired
//    CourseCertificateLogRepository courseCertificateLogRepository;
//
//    @Autowired
//    CourseCertificateNumberRepository courseCertificateNumberRepository;
//
//
//    private PageInfo pageInfo = new PageInfo();
//
//    public CertificateController() {
//        pageInfo.setParentId("m-course");
//        pageInfo.setParentTitle("교육과정유형");
//    }
//
//    @GetMapping("/number/list")
//    public String listPage(Model model) {
//
//        pageInfo.setPageId("m-course-list-page");
//        pageInfo.setPageTitle("수료증정보등록");
//        model.addAttribute(pageInfo);
//        model.addAttribute("borders", courseCertificateNumberRepository.fin.getPageList(pageable));
//
//        // 교육신철일자 유형(1:상시, 2:기간)
//        List<ElMinor> codeList = codeService.getMinorList("BC01");
//        model.addAttribute("codeList", codeList);
//
//        return "admin/course-master/list";
//    }
//
//
////    @GetMapping("/view/{id}")
////    public String viewPage(@PathVariable("id") String id, Model model) {
////
////        CourseMaster oldCourse = courseMasterService.getById(id);
////
////        pageInfo.setPageId("m-course-edit");
////        pageInfo.setPageTitle("교육과정유형수정");
////        model.addAttribute(pageInfo);
////        model.addAttribute("course", courseMasterService.save(oldCourse));
////
////        return "admin/course-master/view";
////    }
////
////    @GetMapping("/add")
////    public String add(Model model) {
////
////        CourseMaster courseMaster = new CourseMaster();
////        courseMaster.setId(courseMasterService.getCourseMasterNumber());
////
////        pageInfo.setPageId("m-course-add");
////        pageInfo.setPageTitle("교육과정등록");
////        model.addAttribute(pageInfo);
////        model.addAttribute("course", courseMaster);
////        model.addAttribute("minorList", courseMasterService.getTypeList());
////        // 교육유형 (BC0101:온라인, BC0102:오프라인)
////        List<ElMinor> codeList = codeService.getMinorList("BC01");
////        model.addAttribute("codeList", codeList);
////
////        return "admin/course-master/add";
////    }
////
////    @PostMapping("/add-post")
////    public String noticeAddPost(@Valid CourseMaster courseMaster, BindingResult result) {
////        if(result.hasErrors()) {
////            return "notice-add";
////        }
////
////        courseMasterService.save(courseMaster);
////
////        return "redirect:/admin/course-master/list";
////    }
////
////    @GetMapping("/edit/{id}")
////    public String noticeEdit(@PathVariable("id") String id, Model model) {
////
////        pageInfo.setPageId("m-course-edit");
////        pageInfo.setPageTitle("교육과정수정");
////        model.addAttribute(pageInfo);
////        model.addAttribute("course", courseMasterService.getById(id));
////        model.addAttribute("minorList", courseMasterService.getTypeList());
////        // 교육신철일자 유형(1:상시, 2:기간)
////        List<ElMinor> codeList = codeService.getMinorList("BC04");
////        model.addAttribute("codeList", codeList);
////
////        return "admin/course-master/edit";
////    }
//
//    @PostMapping("/edit-post/{id}")
//    public String noticeEditPost(@PathVariable("id") String id, @Valid CourseMaster elCourse, BindingResult result) {
//        if(result.hasErrors()) {
//            elCourse.setId(id);
//            return "notice-edit";
//        }
//        courseMasterService.save(elCourse);
//
//        return "redirect:/admin/course-master/list";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String noticeDelete(@PathVariable("id") String id) {
//
//        CourseMaster course = courseMasterService.getById(id);
//
//        courseMasterService.delete(course);
//
//        return "redirect:/admin/course-master/list";
//    }
//
//
//}