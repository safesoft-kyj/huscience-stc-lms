package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.repository.CourseQuizActionRepository;
import com.dtnsm.lms.repository.CourseQuizActionAnswerRepository;
import com.dtnsm.lms.repository.CourseQuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseQuizActionService {

    @Autowired
    CourseQuizActionRepository quizActionRepository;

    @Autowired
    CourseQuizQuestionRepository questionRepository;

    @Autowired
    CourseQuizActionAnswerRepository questionAnswerRepository;

     /*
        Quiz
     */

    public CourseQuizAction saveQuizAction(CourseQuizAction quizAction){
        return quizActionRepository.save(quizAction);
    }

    public void deleteActionQuiz(CourseQuizAction quizAction) {

        quizActionRepository.delete(quizAction);
    }

    public void deleteQuiz(Long id) {
        quizActionRepository.delete(getCourseQuizActionById(id));
    }

    public CourseQuizAction getCourseQuizActionById(Long id) {
        return quizActionRepository.findById(id).get();
    }

    public CourseQuizAction getByCourseAccountIdAndIsActive(Long id, String isActive) {
        return quizActionRepository.findByCourseAccount_idAndIsActive(id, isActive);
    }

    public List<CourseQuizAction> getAllByUserId(String userId) {
        return quizActionRepository.findAllByAccount_UserId(userId);
    }

    public CourseQuizAction getTop1ByUserId(String userId) {
        return quizActionRepository.findTop1ByAccount_UserIdOrderByCreatedDateDesc(userId);
    }


   /*
        Question Answer
     */

    public CourseQuizActionAnswer saveQuizQuestionAnswer(CourseQuizActionAnswer questionAnswer){
        return questionAnswerRepository.save(questionAnswer);
    }

    public void deleteQuizQuestionAnswer(CourseQuizActionAnswer questionAnswer) {
        questionAnswerRepository.delete(questionAnswer);
    }

    public void deleteQuizQuestionAnswer(Long id) {
        questionAnswerRepository.delete(getCourseQuizQuestionAnswerById(id));
    }

    public CourseQuizActionAnswer getCourseQuizQuestionAnswerById(Long id) {
        return questionAnswerRepository.findById(id).get();
    }

}
