package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.BorderFile;
import com.dtnsm.lms.domain.Survey;
import com.dtnsm.lms.domain.SurveyFile;
import com.dtnsm.lms.domain.SurveyQuestion;
import com.dtnsm.lms.repository.SurveyQuestionRepository;
import com.dtnsm.lms.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyService {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    SurveyFileService surveyFileService;

     /*
        Survey
     */

     public List<Survey> getList() {
         return surveyRepository.findAll(sortByIdAsc());
     }

     private Sort sortByIdAsc() {
         return new Sort(Sort.Direction.ASC, "id");
     }

    public Survey saveSurvey(Survey survey){
        return surveyRepository.save(survey);
    }

    public void deleteSurvey(Survey survey) {

        surveyRepository.delete(survey);

        // 첨부파일 삭제
        for(SurveyFile surveyFile : survey.getSurveyFiles()) {
            surveyFileService.deleteFile(surveyFile);
        }
    }

    public void deleteSurvey(Long id) {
        deleteSurvey(surveyRepository.getOne(id));
    }

    public Survey getSurveyById(Long id) {
        return surveyRepository.findById(id).get();
    }

    public Survey getByIsActive(int id) {
        return surveyRepository.findByIsActive(id);
    }

    /*
        Question
     */

    public SurveyQuestion saveSurveyQuestion(SurveyQuestion question){
        return surveyQuestionRepository.save(question);
    }

    public void deleteSurveyQuestion(SurveyQuestion question) {
        surveyQuestionRepository.delete(question);
    }

    public void deleteSurveyQuestion(Long id) {
        surveyQuestionRepository.delete(getSurveyQuestionById(id));
    }

    public SurveyQuestion getSurveyQuestionById(Long id) {
        return surveyQuestionRepository.findById(id).get();
    }


}
