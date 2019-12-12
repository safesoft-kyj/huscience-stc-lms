package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.repository.CourseSurveyQuestionRepository;
import com.dtnsm.lms.repository.CourseSurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseSurveyService {

    @Autowired
    CourseSurveyRepository surveyRepository;

    @Autowired
    CourseSurveyQuestionRepository questionRepository;

    @Autowired
    SurveyService surveyService;



    /*
    Survey
    */

    public List<CourseSurvey> getAllByCourseId(long courseId) {
        return surveyRepository.findAllByCourse_Id(courseId);
    }

    public CourseSurvey saveSurvey(CourseSurvey quiz){
        return surveyRepository.save(quiz);
    }

    public void deleteQuiz(CourseSurvey quiz) {

        surveyRepository.delete(quiz);
    }

    public void deleteSurvey(Long id) {
        surveyRepository.delete(getCourseSurveyById(id));
    }

    public CourseSurvey getCourseSurveyById(Long id) {
        return surveyRepository.findById(id).get();
    }

    /*
        Quiz Questi
     */

    public CourseSurveyQuestion saveSurveyQuestion(CourseSurveyQuestion quizItem){
        return questionRepository.save(quizItem);
    }

    public void deleteSurveyQuestion(CourseSurveyQuestion question) {
        questionRepository.delete(question);
    }

    public void deleteSurveyQuestion(Long id) {
        questionRepository.delete(getCourseSurveyQuestionById(id));
    }

    public CourseSurveyQuestion getCourseSurveyQuestionById(Long id) {
        return questionRepository.findById(id).get();
    }

    // 교육과정의 첫번재 설문을 등록한다.
    public void CreateAutoSurvey(Course course, Long surveyId) {

        if (course.getIsSurvey().equals("Y")) {

            Survey survey = surveyService.getSurveyById(surveyId);

            if (survey == null) return;

            CourseSurvey courseSurvey = new CourseSurvey();
            courseSurvey.setName("[설문] " + course.getTitle());
            courseSurvey.setCourse(course);
            courseSurvey.setSurvey(survey);
            CourseSurvey courseSurvey1 = saveSurvey(courseSurvey);

            // 문제를 복사한다.
            CopySurveyQuestion(courseSurvey1);
        }
    }

    // 기본설문을 과정설문에 복사한다.
    public void CopySurveyQuestion(CourseSurvey courseSurvey) {

        if(courseSurvey.getSurvey() != null) {

            // 기존 설문이 있는 경우 삭제한다.
            if (courseSurvey.getQuestions().size() > 0) {

                for(CourseSurveyQuestion courseSurveyQuestion : courseSurvey.getQuestions()) {
                    questionRepository.delete(courseSurveyQuestion);
                }
            }

            // 설문 template을 과정 설문에 복사
            for(SurveyQuestion surveyQuestion : courseSurvey.getSurvey().getSurveyQuestions()) {

                CourseSurveyQuestion courseSurveyQuestion = new CourseSurveyQuestion();
                courseSurveyQuestion.setCourseSurvey(courseSurvey);
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

                saveSurveyQuestion(courseSurveyQuestion);
            }
        }
    }

}
