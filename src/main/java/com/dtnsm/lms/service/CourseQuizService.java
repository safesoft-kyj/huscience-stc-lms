package com.dtnsm.lms.service;

import com.dtnsm.lms.component.ExcelReader;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.repository.CourseQuizActionAnswerRepository;
import com.dtnsm.lms.repository.CourseQuizQuestionRepository;
import com.dtnsm.lms.repository.CourseQuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CourseQuizService {

    @Autowired
    CourseQuizRepository quizRepository;

    @Autowired
    CourseQuizQuestionRepository questionRepository;

    @Autowired
    CourseQuizActionAnswerRepository questionAnswerRepository;

    @Autowired
    CourseQuizFileService courseQuizFileService;

    @Autowired
    ExcelReader excelReader;


     /*
        Quiz
     */

    public List<CourseQuiz> getAllByCourseId(long courseId) {
        return quizRepository.findAllByCourse_Id(courseId);
    }

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

    public List<CourseQuizQuestion> getAllByQuizId(Long quizId) {
        return questionRepository.findAllByQuiz_Id(quizId);
    }

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

    // ??????????????? ????????? ????????? ????????????.
    public CourseQuiz CreateAutoQuiz(Course course) {

        if (course.getIsQuiz().equals("Y")) {
            // ????????? ????????? ?????? ?????? ????????? ????????? ??????.
            float hour = course.getHour();

            CourseQuiz courseQuiz = new CourseQuiz();
            courseQuiz.setName("[??????] " + course.getTitle());
            courseQuiz.setPassCount(1);
            courseQuiz.setCourse(course);
            courseQuiz.setHour(hour);
            courseQuiz.setMinute(Math.round(courseQuiz.getHour() * 60));
            courseQuiz.setSecond(Math.round(courseQuiz.getMinute() * 60));

            return saveQuiz(courseQuiz);
        }

        return null;
    }

    // ??????????????? ????????? ????????? ????????????.
    public void CreateAutoQuiz(Course course, MultipartFile file, int passCount, float hour) {

        if (course.getIsQuiz().equals("Y")) {
            CourseQuiz courseQuiz = new CourseQuiz();
            courseQuiz.setName("[??????] " + course.getTitle());
            courseQuiz.setPassCount(passCount);
            courseQuiz.setCourse(course);
            courseQuiz.setHour(hour);
            courseQuiz.setMinute(Math.round(courseQuiz.getHour() * 60));
            courseQuiz.setSecond(Math.round(courseQuiz.getMinute() * 60));

            courseQuiz = saveQuiz(courseQuiz);


            // ??????????????? ????????? ?????? ???????????? insert ??????.
            if (file != null) {

                CourseQuizFile uploadFile = courseQuizFileService.storeFile(file, courseQuiz);

                try {
                    List<CourseQuizQuestion> courseQuizQuestions = excelReader.readFileToList(file, CourseQuizQuestion::fromQuiz);

                    for (CourseQuizQuestion quizQuestion : courseQuizQuestions) {
                        // ??????????????? ????????????.

                        if (!quizQuestion.getQuestion().isEmpty()) {
                            quizQuestion.setQuiz(courseQuiz);
                            saveQuizQuestion(quizQuestion);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
