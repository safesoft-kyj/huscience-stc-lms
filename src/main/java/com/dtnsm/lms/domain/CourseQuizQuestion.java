package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Row;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name="el_course_quiz_question")
public class CourseQuizQuestion extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 객관식 질문 항목
    @Column(name="question")
    private String question;

    // 예제 1
    @Column(length = 200)
    private String ex1;

    // 예제 2
    @Column(length = 200)
    private String ex2;

    // 예제 3
    @Column(length = 200)
    private String ex3;

    // 예제 4
    @Column(length = 200)
    private String ex4;

    // 예제 5
    @Column(length = 200)
    private String ex5;

    // 예제 5
    @Column(length = 1)
    private String answer;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private CourseQuiz quiz;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "question")
    @JoinColumn(name = "question_id")
    private CourseQuizActionAnswer questionAnswer;

    public CourseQuizQuestion(){}

    public CourseQuizQuestion(String question, String ex1, String ex2, String ex3, String ex4, String ex5, String answer) {
        this.question = question;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = ex5;
        this.answer = answer;
    }

    // 엑셀 파일업로드(시험문제)
    public static CourseQuizQuestion fromQuiz(Row row) {
        return new CourseQuizQuestion(
                row.getCell(0).getStringCellValue()
                , row.getCell(1).getStringCellValue()   // ex1
                , row.getCell(2).getStringCellValue()   // ex2
                , row.getCell(3).getStringCellValue()   // ex3
                , row.getCell(4).getStringCellValue()   // ex4
                , row.getCell(5).getStringCellValue()   // ex5
                , row.getCell(6).getStringCellValue()   // 정답
        );
    }
}
