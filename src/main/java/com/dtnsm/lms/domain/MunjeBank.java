package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.MunjeGubun;
import com.dtnsm.lms.domain.constant.MunjeType;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_munje_bank")
public class MunjeBank extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 문제 타입(quiz:시험, survey:설문)
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    MunjeType munjeType;

    // 문제 유형(single:주관식, multiple:객관식)
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    MunjeGubun munjeGubun;

    // 문제 문항
    @Column(name="quest", length = 500)
    private String quest;

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

    // 문제정답
    @Column(length = 1)
    @ColumnDefault(value = "0")
    private String isAns1 = "0";

    // 문제정답
    @Column(length = 1)
    @ColumnDefault(value = "0")
    private String isAns2 = "0";

    // 문제정답
    @Column(length = 1)
    @ColumnDefault(value = "0")
    private String isAns3 = "0";

    // 문제정답
    @Column(length = 1)
    @ColumnDefault(value = "0")
    private String isAns4 = "0";
    // 문제정답
    @Column(length = 1)
    @ColumnDefault(value = "0")
    private String isAns5 = "0";

    public MunjeBank() {}

    public MunjeBank(MunjeType munjeType, MunjeGubun munjeGubun, String quest
            , String ex1, String ex2, String ex3, String ex4, String ex5
            , String isAns1, String isAns2, String isAns3, String isAns4, String isAns5) {

        this.munjeType = munjeType;
        this.munjeGubun = munjeGubun;
        this.quest = quest;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
        this.ex5 = ex5;
        this.isAns1 = isAns1;
        this.isAns2 = isAns2;
        this.isAns3 = isAns3;
        this.isAns4 = isAns4;
        this.isAns5 = isAns5;
    }

    // 엑셀 파일업로드(시험문제)
    public static MunjeBank fromQuiz(Row row) {
        return new MunjeBank(MunjeType.QUIZ                // munjeType
                , MunjeGubun.MULTIPLE
                , row.getCell(0).getStringCellValue()   // quest
                , row.getCell(1).getStringCellValue()   // ex1
                , row.getCell(2).getStringCellValue()   // ex2
                , row.getCell(3).getStringCellValue()   // ex3
                , row.getCell(4).getStringCellValue()   // ex4
                , row.getCell(5).getStringCellValue()   // ex5
                , row.getCell(6).getStringCellValue()   // isAns1
                , row.getCell(7).getStringCellValue()   // isAns2
                , row.getCell(8).getStringCellValue()   // isAns3
                , row.getCell(9).getStringCellValue()   // isAns4
                , row.getCell(10).getStringCellValue()   // isAns5
        );
    }

    // 엑셀 파일업로드(설문)
    public static MunjeBank fromSurvey(Row row) {
        return new MunjeBank(MunjeType.SURVEY                // munjeType
                , MunjeGubun.MULTIPLE
                , row.getCell(0).getStringCellValue()   // quest
                , row.getCell(1).getStringCellValue()   // ex1
                , row.getCell(2).getStringCellValue()   // ex2
                , row.getCell(3).getStringCellValue()   // ex3
                , row.getCell(4).getStringCellValue()   // ex4
                , row.getCell(5).getStringCellValue()   // ex5
                , row.getCell(6).getStringCellValue()   // isAns1
                , row.getCell(7).getStringCellValue()   // isAns2
                , row.getCell(8).getStringCellValue()   // isAns3
                , row.getCell(9).getStringCellValue()   // isAns4
                , row.getCell(10).getStringCellValue()   // isAns5
        );
    }
}
