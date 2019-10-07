package com.dtnsm.lms.course.controller;

import com.dtnsm.lms.base.ElCodeService;
import com.dtnsm.lms.course.domain.Course;
import com.dtnsm.lms.course.domain.CourseSurvey;
import com.dtnsm.lms.course.service.CourseService;
import com.dtnsm.lms.course.service.CourseSurveyService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/course/survey")
public class CourseSurveyAdminController {
    @Autowired
    ElCodeService codeService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSurveyService surveyService;

    private PageInfo pageInfo = new PageInfo();

    // 강의 구분
    private String majorCode = "BC03";
    private String minorCode = "BC0303";

    private Course course;

    public CourseSurveyAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("공지사항");
    }

    @GetMapping("/add/{courseId}")
    public String noticeAdd(@PathVariable("courseId") Long courseId, Model model) {

        course = courseService.getCourseById(courseId);
        CourseSurvey survey = new CourseSurvey();
        survey.setCourse(course);
        survey.setType(codeService.getMinorById(minorCode));

        pageInfo.setPageTitle(course.getTitle() + " 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("survey", survey);
        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("id", courseId);

        return "admin/course/survey/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid CourseSurvey survey
            , @RequestParam("id") Long id
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/section/add/" + id;
        }

        Course course = courseService.getCourseById(id);
        survey.setCourse(course);

        // Section 저장
        CourseSurvey survey1 = surveyService.saveSurvey(survey);

        return "redirect:/admin/course/list/" + survey1.getCourse().getCourseMaster().getId();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        CourseSurvey courseSurvey = surveyService.getCourseSurveyById(id);

        pageInfo.setPageTitle(courseSurvey.getName() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("courseSurvey", courseSurvey);
        model.addAttribute("codeList", codeService.getMinorList(majorCode));
        model.addAttribute("id", courseSurvey.getId());

        return "admin/course/survey/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid CourseSurvey courseSurvey
            , BindingResult result) {
        if(result.hasErrors()) {
            course.setId(id);
            return "/admin/course/list/" + course.getCourseMaster().getId();
        }

        CourseSurvey courseSurveyOld = surveyService.getCourseSurveyById(id);

        Course course = courseSurveyOld.getCourse();
        courseSurvey.setCourse(course);

        CourseSurvey courseSurvey1 = surveyService.saveSurvey(courseSurvey);

        return "redirect:/admin/course/view/" + courseSurvey1.getCourse().getId();
    }
}
