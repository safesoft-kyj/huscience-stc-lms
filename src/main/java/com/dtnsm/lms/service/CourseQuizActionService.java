package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseQuiz;
import com.dtnsm.lms.domain.CourseQuizAction;
import com.dtnsm.lms.domain.CourseQuizActionAnswer;
import com.dtnsm.lms.domain.CourseQuizQuestion;
import com.dtnsm.lms.repository.CourseQuizActionRepository;
import com.dtnsm.lms.repository.CourseQuizQuestionAnswerRepository;
import com.dtnsm.lms.repository.CourseQuizQuestionRepository;
import com.dtnsm.lms.repository.CourseQuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseQuizActionService {

    @Autowired
    CourseQuizActionRepository quizActionRepository;

    @Autowired
    CourseQuizQuestionRepository questionRepository;

    @Autowired
    CourseQuizQuestionAnswerRepository questionAnswerRepository;

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
