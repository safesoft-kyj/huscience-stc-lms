package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Row;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name="el_course_survey_question")
public class CourseSurveyQuestion extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 객관식 질문 항목
    @Column(name="question", length = 1000)
    private String question;

    // 구분(S:주관식, M:객관식
    @Column(length = 1)
    private String surveyGubun;

    // 예제 1
    @Column(length = 500)
    private String ex1;

    // 예제 2
    @Column(length = 500)
    private String ex2;

    // 예제 3
    @Column(length = 500)
    private String ex3;

    // 예제 4
    @Column(length = 500)
    private String ex4;

    // 예제 5
    @Column(length = 500)
    private String ex5;

    // 설문1 배점
    @Column(length = 5)
    private int ex1_score;

    // 설문2 배점
    @Column(length = 5)
    private int ex2_score;

    // 설문3 배점
    @Column(length = 5)
    private int ex3_score;

    // 설문4 배점
    @Column(length = 5)
    private int ex4_score;

    // 설문5 배점
    @Column(length = 5)
    private int ex5_score;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_survey_id")
    private CourseSurvey courseSurvey;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "question")
    @JoinColumn(name = "question_id")
    private CourseSurveyActionAnswer questionAnswer;

    public CourseSurveyQuestion(){}

    public CourseSurveyQuestion(String question, String surveyGubun
            , String ex1, String ex2, String ex3, String ex4, String ex5
            , Double ex1_score, Double ex2_score, Double ex3_score, Double ex4_score, Double ex5_score) {
        this.question = question;
        this.surveyGubun = surveyGubun;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = ex5;
        this.ex1_score = ex1_score.intValue();
        this.ex2_score = ex2_score.intValue();
        this.ex3_score = ex3_score.intValue();
        this.ex4_score = ex4_score.intValue();
        this.ex5_score = ex5_score.intValue();
    }


    // 엑셀 파일업로드(시험문제)
    public static CourseSurveyQuestion fromQuiz(Row row) {
        return new CourseSurveyQuestion(
                row.getCell(0).getStringCellValue()     // 질문
                , row.getCell(1).getStringCellValue()   // 구분(S:주관식, M:객관식)
                , row.getCell(2).getStringCellValue()   // ex1
                , row.getCell(3).getStringCellValue()   // ex2
                , row.getCell(4).getStringCellValue()   // ex3
                , row.getCell(5).getStringCellValue()   // ex4
                , row.getCell(6).getStringCellValue()   // ex5
                , row.getCell(7).getNumericCellValue()   // ex1_score
                , row.getCell(8).getNumericCellValue()   // ex2_score
                , row.getCell(9).getNumericCellValue()   // ex3_score
                , row.getCell(10).getNumericCellValue()   // ex4_score
                , row.getCell(11).getNumericCellValue()   // ex5_score
        );
    }
}
