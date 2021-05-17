package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_document_template")
@Audited(withModifiedFlag = true)
public class DocumentTemplate extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "No title")
    @Column(length = 500, nullable = false)
    private String title;

    @NotEmpty(message = "No content")
    @Column(columnDefinition = "ntext")
    private String content;

    // 팀장/부서장 승인 여부
    private String isTeamMangerApproval = "N" ;

    // 과정 관리자 승인 여부
    private String isCourseMangerApproval = "N" ;


//    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Document> documents = new ArrayList<>();

    public DocumentTemplate(){}

    public DocumentTemplate(String title, String content) {
        this.title = title;
        this.content = content;
    }

//    public boolean addDocument(Document document) {
//        if(documents == null)
//            documents = new ArrayList<>();
//
//        return documents.add(document);
//    }
}
