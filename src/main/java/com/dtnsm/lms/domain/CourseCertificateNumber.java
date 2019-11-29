package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Data
@Table(name="el_course_certificate_number", uniqueConstraints={@UniqueConstraint(columnNames={"cerText","cerYear"})})
public class CourseCertificateNumber extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 30 , nullable = false)
    private String cerText;

    // Completion Date
    @Column(length = 4, nullable = false)
    private String cerYear;

    // Completion Date
    @ColumnDefault("0")
    private Integer cerSeq = 0;

    @Column(length = 30)
    private String fullNumber;

    public CourseCertificateNumber(){}

    public CourseCertificateNumber(String cerText, String cerYear) {
        this.cerText = cerText;
        this.cerYear = cerYear;
    }

    public CourseCertificateNumber(String cerText, String cerYear, Integer cerSeq) {
        this.cerText = cerText;
        this.cerYear = cerYear;
        this.cerSeq = cerSeq;
    }


}
