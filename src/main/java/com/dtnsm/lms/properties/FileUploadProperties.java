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

    // 전자결재 파일 업로드 경로
    private String documentUploadDir;

    // 설문 파일 업로드 경로
    private String surveyUploadDir;

    // Digital Binder JD 업로드 경로
    private String binderJdUploadDir;

    // Xdoc 관련 폴더
    private String xdocUploadDir;

    private String cvUploadDir;

    public String getXdocUploadDir() {
        return xdocUploadDir;
    }

    public String getCvUploadDir() {
        return cvUploadDir;
    }

    public void setCvUploadDir(String cvUploadDir) {
        this.cvUploadDir = cvUploadDir;
    }

    public void setXdocUploadDir(String xdocUploadDir) {
        this.xdocUploadDir = xdocUploadDir;
    }

    public String getBinderJdUploadDir() {
        return binderJdUploadDir;
    }

    public void setBinderJdUploadDir(String binderJdUploadDir) {
        this.binderJdUploadDir = binderJdUploadDir;
    }

    public String getSurveyUploadDir() {
        return surveyUploadDir;
    }

    public void setSurveyUploadDir(String surveyUploadDir) {
        this.surveyUploadDir = surveyUploadDir;
    }

    public String getDocumentUploadDir() {
        return documentUploadDir;
    }

    public void setDocumentUploadDir(String documentUploadDir) {
        this.documentUploadDir = documentUploadDir;
    }

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
