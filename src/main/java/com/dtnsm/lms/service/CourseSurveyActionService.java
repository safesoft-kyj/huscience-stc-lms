package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseSurveyAction;
import com.dtnsm.lms.domain.CourseSurveyActionAnswer;
import com.dtnsm.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CourseSurveyActionService {

    @Autowired
    CourseSurveyActionRepository actionRepository;

    @Autowired
    CourseSurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    CourseSurveyActionAnswerRepository surveyActionAnswerRepository;

     /*
        Survey
     */

    @Transactional
    public CourseSurveyAction saveSurveyAction(CourseSurveyAction surveyAction){
        return actionRepository.save(surveyAction);
    }

    @Transactional
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

    @Transactional
    public CourseSurveyActionAnswer saveSurveyActionAnswer(CourseSurveyActionAnswer surveyAnswer){
        return surveyActionAnswerRepository.save(surveyAnswer);
    }

    @Transactional
    public void deleteSurveyActionAnswer(CourseSurveyActionAnswer surveyAnswer) {
        surveyActionAnswerRepository.delete(surveyAnswer);
    }

    @Transactional
    public void deleteSurveyActionAnswer(Long id) {
        surveyActionAnswerRepository.delete(getCourseSurveyActionAnswerById(id));
    }

    public CourseSurveyActionAnswer getCourseSurveyActionAnswerById(Long id) {
        return surveyActionAnswerRepository.findById(id).get();
    }

}
