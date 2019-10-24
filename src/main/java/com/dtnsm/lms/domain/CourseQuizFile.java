package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="el_course_quiz_file")
public class CourseQuizFile extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="file_name")
    private String fileName;

    @Column(name="save_name")
    private String saveName;

    @Column(name="size")
    private long size;

    @Column(name="mime_type")
    private String mimeType;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private CourseQuiz quiz;

    public CourseQuizFile() {
    }

    public CourseQuizFile(String fileName, String saveName, long size, String mimeType) {
        this.fileName = fileName;
        this.saveName = saveName;
        this.size = size;
        this.mimeType = mimeType;
    }

    public CourseQuizFile(String fileName, String saveName, long size, String mimeType, CourseQuiz courseQuiz) {
        this.fileName = fileName;
        this.saveName = saveName;
        this.size = size;
        this.mimeType = mimeType;
        this.quiz = courseQuiz;
    }

    @Override
    public String toString() {
        return "UploadFile [id=" + id + ", fileName=" + fileName + ", saveName=" + saveName + ", size=" + size + ", mimeType=" + mimeType
                + ", insertBy=" + getCreatedBy() + "]"
                + ", insertDate=" + getCreatedDate() + "]";
    }
}
