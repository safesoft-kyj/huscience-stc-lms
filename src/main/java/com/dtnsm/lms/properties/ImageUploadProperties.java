package com.dtnsm.lms.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "image-file")
public class ImageUploadProperties {

    // 게시판 업로드 경로
    private String matrix;
    // 게시판 에디터 이미지 업로드 경로
    private String course;

    private String pdf;

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getMatrix() {
        return matrix;
    }

    public String getCourse() {
        return course;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
