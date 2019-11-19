package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.*;
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

    private PageInfo pageInfo = new PageInfo();

    // 강의 구분
    private String majorCode = "BC03";
    private String minorCode = "BC0303";

    private Course course;

    public CourseSurveyAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("공지사항");
    }

    @GetMapping("/list/{courseId}")
    public String list(@PathVariable("courseId") Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId);
        // 설문조사 여부가 Y 인경우만 Add 버튼 활성화
        String isAdd = course.getIsSurvey();

        pageInfo.setPageId("m-course-list-page");
        pageInfo.setPageTitle("설문조회");
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

        pageInfo.setPageTitle(course.getTitle() + " 등록");

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


        if(courseSurvey1.getSurvey() != null) {

            // 설문 template을 과정 설문에 복사
            for(SurveyQuestion surveyQuestion : courseSurvey.getSurvey().getSurveyQuestions()) {

                CourseSurveyQuestion courseSurveyQuestion = new CourseSurveyQuestion();
                courseSurveyQuestion.setCourseSurvey(courseSurvey1);
                courseSurveyQuestion.setQuestion(surveyQuestion.getQuestion());
                courseSurveyQuestion.setEx1(surveyQuestion.getEx1());
                courseSurveyQuestion.setEx2(surveyQuestion.getEx2());
                courseSurveyQuestion.setEx3(surveyQuestion.getEx3());
                courseSurveyQuestion.setEx4(surveyQuestion.getEx4());
                courseSurveyQuestion.setEx5(surveyQuestion.getEx5());
                courseSurveyQuestion.setEx1_score(surveyQuestion.getEx1_score());
                courseSurveyQuestion.setEx2_score(surveyQuestion.getEx2_score());
                courseSurveyQuestion.setEx3_score(surveyQuestion.getEx3_score());
                courseSurveyQuestion.setEx4_score(surveyQuestion.getEx4_score());
                courseSurveyQuestion.setEx5_score(surveyQuestion.getEx5_score());
                courseSurveyQuestion.setSurveyGubun(surveyQuestion.getSurveyGubun());

                courseSurveyService.saveSurveyQuestion(courseSurveyQuestion);
            }

        }

        return "redirect:/admin/course/list/" + courseSurvey1.getCourse().getCourseMaster().getId();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        CourseSurvey courseSurvey = courseSurveyService.getCourseSurveyById(id);

        pageInfo.setPageTitle(courseSurvey.getName() + " 수정");

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

}
