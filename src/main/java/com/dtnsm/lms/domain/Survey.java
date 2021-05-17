package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_survey")
@Audited(withModifiedFlag = true)
public class Survey extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

//    // 문제 유형(BC0201:객관식, BC0202:주관식) => Major Code : BC02
//    @Column(name="type", length = 10)
//    private String type;

    // 문제
    @Column(name="name")
    private String name;

    // 1: 기본 설문, 0:선택 설문
    @ColumnDefault("0")
    private int isActive = 0;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyFile> surveyFiles = new ArrayList<>();

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyQuestion> surveyQuestions = new ArrayList<>();
}
