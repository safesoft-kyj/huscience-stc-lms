package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseQuiz;
import com.dtnsm.lms.domain.CourseQuizActionAnswer;
import com.dtnsm.lms.domain.CourseQuizQuestion;
import com.dtnsm.lms.repository.CourseQuizQuestionAnswerRepository;
import com.dtnsm.lms.repository.CourseQuizQuestionRepository;
import com.dtnsm.lms.repository.CourseQuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseQuizService {

    @Autowired
    CourseQuizRepository quizRepository;

    @Autowired
    CourseQuizQuestionRepository questionRepository;

    @Autowired
    CourseQuizQuestionAnswerRepository questionAnswerRepository;

     /*
        Quiz
     */

    public CourseQuiz saveQuiz(CourseQuiz quiz){
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(CourseQuiz quiz) {

        quizRepository.delete(quiz);
    }

    public void deleteQuiz(Long id) {
        quizRepository.delete(getCourseQuizById(id));
    }

    public CourseQuiz getCourseQuizById(Long id) {
        return quizRepository.findById(id).get();
    }

    /*
        Question
     */

    public CourseQuizQuestion saveQuizQuestion(CourseQuizQuestion question){
        return questionRepository.save(question);
    }

    public void deleteQuizQuestion(CourseQuizQuestion question) {
        questionRepository.delete(question);
    }

    public void deleteQuizQuestion(Long id) {
        questionRepository.delete(getCourseQuizQuestionById(id));
    }

    public CourseQuizQuestion getCourseQuizQuestionById(Long id) {
        return questionRepository.findById(id).get();
    }
}
