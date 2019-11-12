package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseSurveyAction;
import com.dtnsm.lms.domain.CourseSurveyActionAnswer;
import com.dtnsm.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseSurveyActionService {

    @Autowired
    CourseSurveyActionRepository actionRepository;

    @Autowired
    CourseSurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    CourseSurveyQuestionAnswerRepository surveyQuestionAnswerRepository;

     /*
        Survey
     */

    public CourseSurveyAction saveSurveyAction(CourseSurveyAction surveyAction){
        return actionRepository.save(surveyAction);
    }

    public void deleteActionSurvey(CourseSurveyAction surveyAction) {

        actionRepository.delete(surveyAction);
    }

    public void deleteSurvey(Long id) {
        actionRepository.delete(getCourseSurveyActionById(id));
    }

    public CourseSurveyAction getCourseSurveyActionById(Long id) {
        return actionRepository.findById(id).get();
    }

    public List<CourseSurveyAction> getAllByUserId(String userId) {
        return actionRepository .findAllByAccount_UserId(userId);
    }

    public CourseSurveyAction getTop1ByUserId(String userId) {
        return actionRepository.findTop1ByAccount_UserIdOrderByCreatedDateDesc(userId);
    }


   /*
        Question Answer
     */

    public CourseSurveyActionAnswer saveSurveyQuestionAnswer(CourseSurveyActionAnswer surveyAnswer){
        return surveyQuestionAnswerRepository.save(surveyAnswer);
    }

    public void deleteSurveyQuestionAnswer(CourseSurveyActionAnswer surveyAnswer) {
        surveyQuestionAnswerRepository.delete(surveyAnswer);
    }

    public void deleteSurveyQuestionAnswer(Long id) {
        surveyQuestionAnswerRepository.delete(getCourseSurveyQuestionAnswerById(id));
    }

    public CourseSurveyActionAnswer getCourseSurveyQuestionAnswerById(Long id) {
        return surveyQuestionAnswerRepository.findById(id).get();
    }

}
