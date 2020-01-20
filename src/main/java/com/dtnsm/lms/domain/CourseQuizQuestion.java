package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.util.ExcelUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;

import javax.persistence.*;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    @Column(name="question", length = 1000)
    private String question;

    // 예제 1
    @Column(length = 1000)
    private String ex1;

    // 예제 2
    @Column(length = 1000)
    private String ex2;

    // 예제 3
    @Column(length = 1000)
    private String ex3;

    // 예제 4
    @Column(length = 1000)
    private String ex4;

    // 예제 5
    @Column(length = 1000)
    private String ex5;

    // 예제 6
    @Column(length = 1000)
    private String ex6;

    // 예제 7
    @Column(length = 1000)
    private String ex7;

    // 예제 8
    @Column(length = 1000)
    private String ex8;

    // 예제 9
    @Column(length = 1000)
    private String ex9;

    // 답
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

    public CourseQuizQuestion(String question, String answer, String ex1, String ex2, String ex3, String ex4, String ex5, String ex6, String ex7, String ex8, String ex9) {
        this.question = question;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = ex5;
        this.ex6 = ex6;
        this.ex7 = ex7;
        this.ex8 = ex8;
        this.ex9 = ex9;
        this.answer = answer;
    }

    public CourseQuizQuestion(String question, String answer, String ex1, String ex2, String ex3, String ex4, String ex5, String ex6, String ex7, String ex8) {
        this.question = question;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = ex5;
        this.ex6 = ex6;
        this.ex7 = ex7;
        this.ex8 = ex8;
        this.ex9 = "";
        this.answer = answer;
    }

    public CourseQuizQuestion(String question, String answer, String ex1, String ex2, String ex3, String ex4, String ex5, String ex6, String ex7) {
        this.question = question;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = ex5;
        this.ex6 = ex6;
        this.ex7 = ex7;
        this.ex8 = "";
        this.ex9 = "";
        this.answer = answer;
    }

    public CourseQuizQuestion(String question, String answer, String ex1, String ex2, String ex3, String ex4, String ex5, String ex6) {
        this.question = question;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = ex5;
        this.ex6 = ex6;
        this.ex7 = "";
        this.ex8 = "";
        this.ex9 = "";
        this.answer = answer;
    }

    public CourseQuizQuestion(String question, String answer, String ex1, String ex2, String ex3, String ex4, String ex5) {
        this.question = question;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = ex5;
        this.ex6 = "";
        this.ex7 = "";
        this.ex8 = "";
        this.ex9 = "";
        this.answer = answer;
    }

    public CourseQuizQuestion(String question, String answer, String ex1, String ex2, String ex3, String ex4) {
        this.question = question;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = "";
        this.ex6 = "";
        this.ex7 = "";
        this.ex8 = "";
        this.ex9 = "";
        this.answer = answer;
    }


    // 엑셀 파일업로드(시험문제)
    public static CourseQuizQuestion fromQuiz(Row  row) {

        short lastNum = row.getLastCellNum();

        List<String> cellValues = new ArrayList<>();
        for(int i = 0; i < lastNum; i++) {
            cellValues.add(ExcelUtil.getStringByCellValue(row.getCell(i)));
        }

        System.out.println(cellValues.size());
        if (cellValues.size() <= 6) {
            return new CourseQuizQuestion(cellValues.get(0), cellValues.get(1), cellValues.get(2), cellValues.get(3), cellValues.get(4), cellValues.get(5));
        } else if (cellValues.size() == 7) {
            return new CourseQuizQuestion(cellValues.get(0), cellValues.get(1), cellValues.get(2), cellValues.get(3), cellValues.get(4), cellValues.get(5), cellValues.get(6));
        } else if (cellValues.size() == 8) {
            return new CourseQuizQuestion(cellValues.get(0), cellValues.get(1), cellValues.get(2), cellValues.get(3), cellValues.get(4), cellValues.get(5), cellValues.get(6)
                    , cellValues.get(7));
        } else if (cellValues.size() == 9) {
            return new CourseQuizQuestion(cellValues.get(0), cellValues.get(1), cellValues.get(2), cellValues.get(3), cellValues.get(4), cellValues.get(5), cellValues.get(6)
                    , cellValues.get(7), cellValues.get(8));
        } else if (cellValues.size() == 10) {
            return new CourseQuizQuestion(cellValues.get(0), cellValues.get(1), cellValues.get(2), cellValues.get(3), cellValues.get(4), cellValues.get(5), cellValues.get(6)
                    , cellValues.get(7), cellValues.get(8), cellValues.get(9));
        } else {
            return new CourseQuizQuestion(cellValues.get(0), cellValues.get(1), cellValues.get(2), cellValues.get(3), cellValues.get(4), cellValues.get(5), cellValues.get(6)
                    , cellValues.get(7), cellValues.get(8), cellValues.get(9), cellValues.get(9));
        }



//        System.out.println(lastNum);
//        return new CourseQuizQuestion(
//                lastNum < 0 ? "" : ExcelUtil.getStringByCellValue(row.getCell(0))  //문제
//                , lastNum < 1 ? "" : ExcelUtil.getStringByCellValue(row.getCell(1))    // 정답
//                , lastNum < 2 ? "" : ExcelUtil.getStringByCellValue(row.getCell(2))    // ex1
//                , lastNum < 3 ? "" : ExcelUtil.getStringByCellValue(row.getCell(3))    // ex2
//                , lastNum < 4 ? "" : ExcelUtil.getStringByCellValue(row.getCell(4))    // ex3
//                , lastNum < 5 ? "" : ExcelUtil.getStringByCellValue(row.getCell(5))    // ex4
//                , lastNum < 6 ? "" : ExcelUtil.getStringByCellValue(row.getCell(6))    // ex5
//                , lastNum < 7 ? "" : ExcelUtil.getStringByCellValue(row.getCell(7))    // ex6
//                , lastNum < 8 ? "" : ExcelUtil.getStringByCellValue(row.getCell(8))    // ex7
//                , lastNum < 9 ? "" : ExcelUtil.getStringByCellValue(row.getCell(9))    // ex8
//                , lastNum < 10 ? "" : ExcelUtil.getStringByCellValue(row.getCell(10))   // ex9
//        );


//        System.out.println(row);
//        return new CourseQuizQuestion(
//                 row.getCell(0).getStringCellValue()
//                , row.getCell(1).getCellType() == CellType.NUMERIC ? String.valueOf(Math.round(row.getCell(1).getNumericCellValue())) : row.getCell(1).getStringCellValue()   // 정답
//                , row.getCell(2).getStringCellValue()   // ex1)
//                , row.getCell(3).getStringCellValue()   // ex2
//                , row.getCell(4).getStringCellValue()   // ex3
//                , row.getCell(5) == null ? "" :  row.getCell(5).getStringCellValue()   // ex4
//                , row.getCell(6) == null ? "" :  row.getCell(6).getStringCellValue()   // ex5
//                , row.getCell(7) == null ? "" : row.getCell(7).getStringCellValue()   // ex6
//                , row.getCell(8) == null ? "" : row.getCell(8).getStringCellValue()   // ex7
//                , row.getCell(9) == null ? "" : row.getCell(9).getStringCellValue()   // ex8
//                , row.getCell(10) == null ? "" : row.getCell(10).getStringCellValue()   // ex9
//        );
    }
}
