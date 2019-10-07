package com.dtnsm.lms.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileUploadProperties {
    // 게시판 업로드 경로
    private String borderUploadDir;
    // 게시판 에디터 이미지 업로드 경로
    private String borderPhotoUploadDir;
    // 교육과정 파일 업로드 경로
    private String courseUploadDir;

    // 강의 파일 업로드 경로
    private String courseSectionUploadDir;
    // 교육과정 에디터 이미지 업로드 경로
    private String coursePhotoUploadDir;

    public String getCourseSectionUploadDir() {
        return courseSectionUploadDir;
    }

    public void setCourseSectionUploadDir(String courseSectionUploadDir) {
        this.courseSectionUploadDir = courseSectionUploadDir;
    }



    public String getCourseUploadDir() {
        return courseUploadDir;
    }

    public String getCoursePhotoUploadDir() {
        return coursePhotoUploadDir;
    }

    public void setCourseUploadDir(String courseUploadDir) {
        this.courseUploadDir = courseUploadDir;
    }

    public void setCoursePhotoUploadDir(String coursePhotoUploadDir) {
        this.coursePhotoUploadDir = coursePhotoUploadDir;
    }

    public String getBorderPhotoUploadDir() {
        return borderPhotoUploadDir;
    }

    public void setBorderPhotoUploadDir(String borderPhotoUploadDir) {
        this.borderPhotoUploadDir = borderPhotoUploadDir;
    }

    public String getBorderUploadDir() {
        return borderUploadDir;
    }

    public void setBorderUploadDir(String borderUploadDir) {
        this.borderUploadDir = borderUploadDir;
    }


}
