package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
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
@Table(name="el_course_quiz_action_answer")
public class CourseQuizActionAnswer extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 정답
    @Column(length = 1)
    private String answer;

    // 사용자정답
    @Column(length = 1)
    private String userAnswer;

    // 정답유무 (1:정답, 0:오답)
    private int answerCount = 0;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "quiz_action_id")
    private CourseQuizAction quizAction;


    @OneToOne(fetch = FetchType.LAZY)
    private CourseQuizQuestion question;

    public CourseQuizActionAnswer(){}
}
