package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="el_document_course_account")
@Audited(withModifiedFlag = true)
public class DocumentCourseAccount extends AuditorEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;

    // 교육과정 등록 완료 여부 (0:미적용, 1:적용)
    @ColumnDefault("'0'")
    private String isCommit = "0";
}
