package com.dtnsm.lms.properties;

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

    // 시험문제 파일 업로드 경로
    private String courseQuizUploadDir;

    // 연간일정 파일 업로드 경로
    private String scheduleUploadDir;

    public String getCourseQuizUploadDir() {
        return courseQuizUploadDir;
    }

    public void setCourseQuizUploadDir(String courseQuizUploadDir) {
        this.courseQuizUploadDir = courseQuizUploadDir;
    }


    public String getScheduleUploadDir() {
        return scheduleUploadDir;
    }

    public void setScheduleUploadDir(String scheduleUploadDir) {
        this.scheduleUploadDir = scheduleUploadDir;
    }

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
