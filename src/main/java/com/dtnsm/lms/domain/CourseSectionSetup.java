package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="el_course_section_setup")
@Audited(withModifiedFlag = true)
public class CourseSectionSetup extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    // 강의명
    @Column(name="name")
    private String name;

    // 종횡 을 구분한 pixcel(기준 3000)
    @ColumnDefault("0")
    private int targetWidth;

    // targetWdith 기준으로 작을때 표현할 width 백분율
    @ColumnDefault("0")
    private int smallWidthRate;

    // targetWdith 기준으로 클때 표현할 width 백분율
    @ColumnDefault("0")
    private int bigWidthRate;
}
