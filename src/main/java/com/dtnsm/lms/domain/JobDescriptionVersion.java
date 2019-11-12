package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="job_description_version")
public class JobDescriptionVersion extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    // 등록일
    @Column(length = 10, nullable = false)
    private String regDate;

    private double ver = 1.0;

    private String isActive = "0";

    @ManyToOne
    @JoinColumn(name = "jd_id")
    private JobDescription jd;

    @OneToMany(mappedBy = "jdVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinderJd>  binderJds = new ArrayList<>();

    @OneToMany(mappedBy = "jdVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobDescriptionVersionFile> jdFiles = new ArrayList<>();
}
