package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="job_description")
public class JobDescription extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    // Job Name
    private String title;

    // Job Name
    @Column(length = 10, nullable = false)
    private String shortName;

    // 등록일
    @Column(length = 10, nullable = false)
    private String regDate;

    @OneToMany(mappedBy = "jd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobDescriptionVersion> jdVersions = new ArrayList<>();
}
