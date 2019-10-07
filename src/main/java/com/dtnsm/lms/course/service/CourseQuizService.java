package com.dtnsm.lms.course.service;

import com.dtnsm.lms.course.domain.CourseQuiz;
import com.dtnsm.lms.course.domain.CourseQuizQuestion;
import com.dtnsm.lms.course.domain.CourseQuizQuestionItem;
import com.dtnsm.lms.course.repository.CourseQuizQuestionItemRepository;
import com.dtnsm.lms.course.repository.CourseQuizQuestionRepository;
import com.dtnsm.lms.course.repository.CourseQuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseQuizService {

    @Autowired
    CourseQuizRepository quizRepository;

    @Autowired
    CourseQuizQuestionRepository questionRepository;

    @Autowired
    CourseQuizQuestionItemRepository itemRepository;

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

    /*
        Question Item
     */

    public CourseQuizQuestionItem saveQuizQuestionItem(CourseQuizQuestionItem quizItem){
        return itemRepository.save(quizItem);
    }

    public void deleteQuizQuestionItem(CourseQuizQuestionItem quizItem) {
        itemRepository.delete(quizItem);
    }

    public void deleteQuizQuestionItem(Long id) {
        itemRepository.delete(getCourseQuizQuestionItemById(id));
    }

    public CourseQuizQuestionItem getCourseQuizQuestionItemById(Long id) {
        return itemRepository.findById(id).get();
    }


}
