package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="job_description", uniqueConstraints = @UniqueConstraint(columnNames = {"shortName"}))
public class JobDescription extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private Long id;

    // Job Name
    private String title;

    // Job Name
    @Column(length = 10, nullable = false)
    private String shortName;

    @OneToMany(mappedBy = "jobDescription")
    private List<JobDescriptionVersion> jobDescriptionVersions = new ArrayList<>();
}
