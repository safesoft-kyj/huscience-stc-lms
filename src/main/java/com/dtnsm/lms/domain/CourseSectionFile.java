package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="el_course_section_file")
public class CourseSectionFile extends AuditorCreateEntity<String> {

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

    @Column(name="image_folder")
    @ColumnDefault("''")
    private String imageFolder;

    @Column(name="image_size")
    @ColumnDefault("0")
    private int imageSize;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "section_id")
    private CourseSection courseSection;

    public CourseSectionFile() {
    }

    public CourseSectionFile(String fileName, String saveName, long size, String mimeType) {
        this.fileName = fileName;
        this.saveName = saveName;
        this.size = size;
        this.mimeType = mimeType;
    }

    public CourseSectionFile(String fileName, String saveName, long size, String mimeType, CourseSection courseSection) {
        this.fileName = fileName;
        this.saveName = saveName;
        this.size = size;
        this.mimeType = mimeType;
        this.courseSection = courseSection;
    }

    public CourseSectionFile(String fileName, String saveName, long size, String mimeType, String imageFolder, int imageSize, CourseSection courseSection) {
        this.fileName = fileName;
        this.saveName = saveName;
        this.size = size;
        this.mimeType = mimeType;
        this.imageFolder = imageFolder.isEmpty() ? "" : imageFolder;
        this.imageSize = imageSize;
        this.courseSection = courseSection;
    }

    @Override
    public String toString() {
        return "UploadFile [id=" + id + ", fileName=" + fileName + ", saveName=" + saveName + ", size=" + size + ", mimeType=" + mimeType
                + ", insertBy=" + getCreatedBy() + "]"
                + ", insertDate=" + getCreatedDate() + "]";
    }
}
