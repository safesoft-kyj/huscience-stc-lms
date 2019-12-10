package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.mybatis.dto.ReportForm1;
import com.dtnsm.lms.mybatis.dto.ReportForm3;
import com.dtnsm.lms.mybatis.service.ReportMapperService;
import com.dtnsm.lms.service.CodeService;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.service.CourseSurveyService;
import com.dtnsm.lms.service.SurveyService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/course/survey")
public class CourseSurveyAdminController {
    @Autowired
    CodeService codeService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSurveyService courseSurveyService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    ReportMapperService reportMapperService;

    private PageInfo pageInfo = new PageInfo();

    // 강의 구분
    private String majorCode = "BC03";
    private String minorCode = "BC0303";

    private Course course;

    public CourseSurveyAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정");
    }

    @GetMapping("/list/{courseId}")
    public String list(@PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);
        // 설문조사 여부가 Y 인경우만 Add 버튼 활성화
        String isAdd = course.getIsSurvey();

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("설문");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseSurveyService.getAllByCourseId(courseId));
        model.addAttribute("isAdd", isAdd);
        model.addAttribute("typeId", course.getCourseMaster().getId());

        return "admin/course/survey/list";
    }

    @GetMapping("/add/{courseId}")
    public String noticeAdd(@PathVariable("courseId") Long courseId, Model model) {

        course = courseService.getCourseById(courseId);
        CourseSurvey courseSurvey = new CourseSurvey();
        courseSurvey.setCourse(course);

        pageInfo.setPageTitle(course.getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("courseSurvey", courseSurvey);
        model.addAttribute("surveyList", surveyService.getList());
        model.addAttribute("id", courseId);

        return "admin/course/survey/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid CourseSurvey courseSurvey
            , @RequestParam("id") Long id
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/course/section/add/" + id;
        }

        Course course = courseService.getCourseById(id);
        courseSurvey.setCourse(course);

        // Section 저장
        CourseSurvey courseSurvey1 = courseSurveyService.saveSurvey(courseSurvey);

        // 선택된 설문을 복사한다.
        courseSurveyService.CopySurveyQuestion(courseSurvey1);

        return "redirect:/admin/course/list/" + courseSurvey1.getCourse().getCourseMaster().getId();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(id);

        pageInfo.setPageTitle(courseSurvey.getName());

        model.addAttribute(pageInfo);
        model.addAttribute("courseSurvey", courseSurvey);
        model.addAttribute("surveyList", surveyService.getList());
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

        CourseSurvey courseSurveyOld = courseSurveyService.getCourseSurveyById(id);

        Course course = courseSurveyOld.getCourse();
        courseSurvey.setCourse(course);

        CourseSurvey courseSurvey1 = courseSurveyService.saveSurvey(courseSurvey);

        // 선택된 설문을 복사한다.
        courseSurveyService.CopySurveyQuestion(courseSurvey1);


        return "redirect:/admin/course/survey/list/" + courseSurvey1.getCourse().getId();
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(id);

        Long courseId = courseSurvey.getCourse().getId();

        // 과정 신청된 내역이 있으면 삭제 하지 않는다.
        if (courseSurvey.getCourse().getCourseAccountList().size() <= 0) {
            courseSurveyService.deleteQuiz(courseSurvey);
        }

        return "redirect:/admin/course/survey/list/" + courseId;
    }

    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(id);

        pageInfo.setPageId("self");
        pageInfo.setPageTitle(courseSurvey.getName());

        model.addAttribute(pageInfo);
        model.addAttribute("borders", courseSurvey.getQuestions());
        model.addAttribute("courseId", courseSurvey.getCourse().getId());

        return "admin/course/survey/view";
    }

    // Report
    @GetMapping("/report/{id}")
    public String reportPage(@PathVariable("id") long id, Model model) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(id);

        List<ReportForm1> borders = reportMapperService.getSurveyReport(courseSurvey.getCourse().getId());

        List<ReportForm3> shoutQuestionsAnswer = reportMapperService.getSurveyReport2(courseSurvey.getCourse().getId());


        List<CourseSurveyQuestion> shoutQuestions = new ArrayList<>();
        for(CourseSurveyQuestion courseSurveyQuestion : courseSurvey.getQuestions()) {
            if (courseSurveyQuestion.getSurveyGubun().equals("S")) {
                shoutQuestions.add(courseSurveyQuestion);
            }
        }

        pageInfo.setPageId("self");
        pageInfo.setPageTitle(courseSurvey.getName());

        model.addAttribute(pageInfo);
        model.addAttribute("borders", borders);
        model.addAttribute("shoutQuestions", shoutQuestions);
        model.addAttribute("shoutQuestionsAnswer", shoutQuestionsAnswer);
        model.addAttribute("courseId", courseSurvey.getCourse().getId());

        return "admin/course/survey/report";
    }

}
