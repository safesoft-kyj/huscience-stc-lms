package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name="el_course_quiz_action_answer")
@Audited(withModifiedFlag = true)
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

//    // 시험을 여러번 친경우 최종 상태를 가진다 0:이전시험, 1:마지막 시험
//    @ColumnDefault("'1'")
//    private String isActive = "1";

    private long questionId;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "quiz_action_id")
    private CourseQuizAction quizAction;


//    @OneToOne(fetch = FetchType.LAZY)
//    private CourseQuizQuestion question;

    public CourseQuizActionAnswer(){}
}
