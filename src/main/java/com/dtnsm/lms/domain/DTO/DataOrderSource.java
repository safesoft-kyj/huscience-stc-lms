package com.dtnsm.lms.domain.DTO;

import com.dtnsm.lms.domain.DocDataSource;

import java.util.List;

public class DataOrderSource {

    private List<DocDataSource> courseOrders;

    public void SetCourseOrders(List<DocDataSource> courseOrders) {
        this.courseOrders = courseOrders;
    }

    public List<DocDataSource> getCourseOrders() {
        return courseOrders;
    }
}
