package com.dtnsm.lms.course.controller;

import com.dtnsm.lms.base.ElCodeService;
import com.dtnsm.lms.course.domain.CourseMaster;
import com.dtnsm.lms.course.service.CourseMasterService;
import com.dtnsm.lms.course.service.CourseService;
import com.dtnsm.lms.util.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/course-master")
public class CourseMasterController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseMasterController.class);

    @Autowired
    CourseMasterService courseMasterService;

    @Autowired
    CourseService courseService;

    @Autowired
    ElCodeService codeService;

    private PageInfo pageInfo = new PageInfo();

    public CourseMasterController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정유형");
    }

    @GetMapping("/list")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("교육과정유형조회");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseMasterService.getPageList(pageable));

        return "admin/course-master/list";
    }


    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") String id, Model model) {

        CourseMaster oldCourse = courseMasterService.getById(id);

        pageInfo.setPageId("m-course-edit");
        pageInfo.setPageTitle("교육과정유형수정");
        model.addAttribute(pageInfo);
        model.addAttribute("course", courseMasterService.save(oldCourse));

        return "admin/course-master/view";
    }

    @GetMapping("/add")
    public String add(Model model) {

        CourseMaster courseMaster = new CourseMaster();
        courseMaster.setId(courseMasterService.getCourseMasterNumber());

        pageInfo.setPageId("m-course-add");
        pageInfo.setPageTitle("교육과정유형등록");
        model.addAttribute(pageInfo);
        model.addAttribute("course", courseMaster);
        model.addAttribute("minorList", courseMasterService.getTypeList());

        return "admin/course-master/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid CourseMaster courseMaster, BindingResult result) {
        if(result.hasErrors()) {
            return "notice-add";
        }

        courseMasterService.save(courseMaster);

        return "redirect:/admin/course-master/list";
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") String id, Model model) {

        pageInfo.setPageId("m-course-edit");
        pageInfo.setPageTitle("교육과정유형수정");
        model.addAttribute(pageInfo);
        model.addAttribute("course", courseMasterService.getById(id));
        model.addAttribute("minorList", courseMasterService.getTypeList());

        return "admin/course-master/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") String id, @Valid CourseMaster elCourse, BindingResult result) {
        if(result.hasErrors()) {
            elCourse.setId(id);
            return "notice-edit";
        }
        courseMasterService.save(elCourse);

        return "redirect:/admin/course-master/list";
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") String id) {

        CourseMaster course = courseMasterService.getById(id);

        courseMasterService.delete(course);

        return "redirect:/admin/course-master/list";
    }


}