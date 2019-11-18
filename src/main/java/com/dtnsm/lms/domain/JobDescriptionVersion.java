package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="job_description_version")
public class JobDescriptionVersion extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    @DateTimeFormat(pattern = "dd-MMM-yyyy")
    @Column(name = "release_date")
    private Date release_date;

    @Column(name = "version_no")
    private String version_no;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "jd_id", referencedColumnName = "id")
    private JobDescription jobDescription;

//    @OneToMany(mappedBy = "jdVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToOne
    private JobDescriptionVersionFile jobDescriptionVersionFile;

    @Transient
    private MultipartFile file;
}
