//package com.dtnsm.lms.course.domain;
//
//import com.dtnsm.lms.audit.AuditorCreateEntity;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotEmpty;
//
//@Entity
//@Getter
//@Setter
//@Table(name="el_course_user")
//public class CourseUser extends AuditorCreateEntity<String> {
//
//    @Id
//    @GeneratedValue(strategy=GenerationType.IDENTITY)
//    @Column(name="id")
//    private int id;
//
//    // 강의구분(1:강의, 2:시험) => Major Code : BA002
//    @Column(name="section_type", length = 1)
//    private String sectionType;
//
//    // 강의명
//    @Column(name="section_name")
//    private String sectionName;
//
//    // 학습시작일
//    @Column(name="section_from_date", length = 8)
//    private String sectionFromDate;
//
//    // 학습종료일
//    @Column(name="section_to_date", length = 8)
//    private String sectionToDate;
//
//    // 학습종료일
//    @Column(name="section_Minute")
//    private int sectionMinute;
//
//    @NotEmpty(message = "No file name")
//    @Column(name="file_name", nullable = false)
//    private String fileName;
//
//    @NotEmpty(message = "No save name")
//    @Column(name="save_name", nullable = false)
//    private String saveName;
//
//    @Column(name="size")
//    private long size;
//
//    @Column(name="mime_type")
//    private String mimeType;
//
//    // Parent 필드 추가
//    @ManyToOne
//    @JoinColumn(name = "course_id")
//    private Course course;
//
//    public CourseUser() {
//    }
//
//    public CourseUser(String fileName, String saveName, long size, String mimeType) {
//        this.fileName = fileName;
//        this.saveName = saveName;
//        this.size = size;
//        this.mimeType = mimeType;
//    }
//
//    public CourseUser(String fileName, String saveName, long size, String mimeType, Course course) {
//        this.fileName = fileName;
//        this.saveName = saveName;
//        this.size = size;
//        this.mimeType = mimeType;
//        this.course = course;
//    }
//
//    @Override
//    public String toString() {
//        return "UploadFile [id=" + id + ", fileName=" + fileName + ", saveName=" + saveName + ", size=" + size + ", mimeType=" + mimeType
//                + ", insertBy=" + getCreatedBy() + "]"
//                + ", insertDate=" + getCreatedDate() + "]";
//    }
//}
