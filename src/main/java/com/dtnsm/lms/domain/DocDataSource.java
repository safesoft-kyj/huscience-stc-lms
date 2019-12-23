package com.dtnsm.lms.domain;


import org.springframework.stereotype.Component;

import java.util.List;

@Component("DocDataSource")
public class DocDataSource {

//    @Autowired
//    BinderLogService binderLogService;
//
//    @Autowired
//    UserServiceImpl userService;

    private List<CourseTrainingLog> courseTrainingLogs;
    private Account account;

//    public DocDataSource() {
////        this.account = userService.getAccountByUserId(userId);
////        this.courseTrainingLogs = binderLogService.getAllByUser(userId);
//    }
    public void setAccount(Account account) {
        this.account = account;
    }

    public void setCourseTrainingLogs(List<CourseTrainingLog> courseTrainingLogs){
        this.courseTrainingLogs = courseTrainingLogs;
    }

    public Account getAccount() {
        return account;
    }

    public List<CourseTrainingLog> getCourseTrainingLog() {
        return this.courseTrainingLogs;
    }
}

