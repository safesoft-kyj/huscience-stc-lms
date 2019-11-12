package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="el_document_course_account")
public class DocumentCourseAccount extends AuditorEntity<String> {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;
}
