package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseQuiz;
import com.dtnsm.lms.domain.CourseSurvey;
import com.dtnsm.lms.domain.CourseSurveyQuestion;
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

}
