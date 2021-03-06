package com.dtnsm.lms.service;

import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.TrainingType;
import com.dtnsm.lms.domain.datasource.CertificateSource;
import com.dtnsm.lms.domain.datasource.EmployeeTrainingLogSource;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.CourseTrainingLogRepository;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.LogUtil;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class BinderLogService {

//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BinderLogService.class);

    @Autowired
    CourseTrainingLogRepository courseTrainingLogRepository;

    @Autowired
    BinderLogService binderLogService;

    @Autowired
    SignatureRepository signatureRepository;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private UserJobDescriptionRepository userJobDescriptionRepository;

    @Autowired
    CourseSectionActionService courseSectionActionService;

    // Employee Training Log ????????? ??????
    public EmployeeTrainingLogSource getEmployeeTrainingLog(String userId, long timestamp) {

        // ????????????
//        CommonUtilities.applyLicense();

        // Employee Training Log Data Source ??????
        EmployeeTrainingLogSource trainingLogSource = new EmployeeTrainingLogSource("EmployeeTrainingLogSource");

        // ???????????? ????????? ????????? ??????.
        List<CourseTrainingLog> courseTrainingLogs = binderLogService.getAllByUserOrderByCompleteDateAsc(userId);

        // ?????? ????????? ????????? ??????.
        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
        byte[] imageBytes = null;

        if (optionalSignature.isPresent()) {
            Signature signature = optionalSignature.get();
            String data = signature.getBase64signature().split(",")[1];
            imageBytes = DatatypeConverter.parseBase64Binary(data);
        }

        Account account = userService.getAccountByUserId(userId);
        String depart = "";
        if (account.getOrgDepart() != null && !account.getOrgDepart().isEmpty()) {
            depart = account.getOrgDepart();
        }

        if (account.getOrgTeam() != null && !account.getOrgTeam().isEmpty()) {
            if (depart.equals("")) {
                depart = account.getOrgTeam();
            } else {
                depart += " / " + account.getOrgTeam();
            }
        }

        Iterable<UserJobDescription> userJobDescriptions = getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED));
        if(ObjectUtils.isEmpty(userJobDescriptions)) {
            trainingLogSource.setJobTitle("");
        } else {
            trainingLogSource.setJobTitle(StreamSupport.stream(userJobDescriptions.spliterator(), false)
                    .map(u -> u.getJobDescriptionVersion().getJobDescription().getTitle()).collect(Collectors.joining(",")));
        }
        trainingLogSource.setDepart(depart);
        trainingLogSource.setSign(imageBytes);
        trainingLogSource.setInDate(account.getIndate() != null ? DateUtil.getDateToString(DateUtil.getStringToDate(account.getIndate()), "dd-MMM-yyyy") : "");
        trainingLogSource.setToDate(DateUtil.getDateToString(DateUtil.getTodayDate(), "dd-MMM-yyyy"));
        trainingLogSource.setAccount(account);
        trainingLogSource.setCourseTrainingLogs(courseTrainingLogs);

        return trainingLogSource;
    }


    private Iterable<UserJobDescription> getJobDescriptionList(String userId, List<JobDescriptionStatus> statusList) {
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        if(statusList.size() == 1) {
            builder.and(qUserJobDescription.status.eq(statusList.get(0)));
        } else {
            builder.and(qUserJobDescription.status.in(statusList));
        }
        builder.and(qUserJobDescription.username.eq(userId));
//        builder.and(qUserJobDescription.reviewed.eq(false));

        return userJobDescriptionRepository.findAll(builder, qUserJobDescription.id.desc());
    }


    public CourseTrainingLog saveLog(CourseTrainingLog courseTrainingLog) {
        return courseTrainingLogRepository.save(courseTrainingLog);
    }

    public List<CourseTrainingLog> getAllByUserOrderByCompleteDateAsc(String userId){
        return courseTrainingLogRepository.findAllByAccount_UserIdOrderByCompleteDateAsc(userId);
    }

    public List<CourseTrainingLog> getAllByUserOrderByCompleteDateDesc(String userId){
        return courseTrainingLogRepository.findAllByAccount_UserIdOrderByCompleteDateDesc(userId);
    }

    // ???????????? ????????? ?????? ??????
    public void uploadTriningLogDelete(String userId) {

        List<CourseTrainingLog> courseTrainingLogs = courseTrainingLogRepository.findAllByAccount_UserIdAndIsUpload(userId, "1");
        if (courseTrainingLogs.size() > 0) {
            for (CourseTrainingLog courseTrainingLog : courseTrainingLogs) {
                courseTrainingLogRepository.delete(courseTrainingLog);
            }
        }
    }

    public void createTrainingLog(CourseAccount courseAccount) {

        log.info("1.Training Log ?????? ??????(BinderLogService) : User {}, Course Name {}, TypeId {}, ???????????? {} ~ {}", courseAccount.getAccount().getName()
                , courseAccount.getCourse().getTitle()
                , courseAccount.getCourse().getCourseMaster().getId()
                , courseAccount.getFromDate()
                , courseAccount.getToDate());

        //  ???????????? ????????? ???????????????.
        for (CourseSection courseSection : courseAccount.getCourse().getSections()) {

            CourseTrainingLog courseTrainingLog = new CourseTrainingLog();
            courseTrainingLog.setCourseAccount(courseAccount);
            courseTrainingLog.setAccount(courseAccount.getAccount());
            courseTrainingLog.setIsUpload("0");

            // Self training  ??????
            if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {

                // CompleteDate : self training -> ???????????????
                log.info("2. {} CompleteDate : {}"
                        , courseAccount.getCourse().getCourseMaster().getCourseName()
                        , DateUtil.getTodayDate());
                courseTrainingLog.setCompleteDate(DateUtil.getTodayDate());

                for (CourseSectionAction action : courseAccount.getCourseSectionActions()) {

                    if(action.getCourseSection().getId() == courseSection.getId()) {

                        // TODO ??????????????? ?????????????????? ???????????? ?????????????????? ????????? ?????????????????? ????????????.
                        // ??????????????? ?????????????????? ???????????? ?????????????????? ????????? ?????????????????? ????????????.
                        if (action.getTotalUseSecond() > courseSection.getSecond()) {
                            courseTrainingLog.setTrainingTime(action.getTotalUseSecond());
                        } else {
                            courseTrainingLog.setTrainingTime(courseSection.getSecond());
                        }

                        // 210709 CYJ traininglog - sectionAction ?????? ????????????
                        //courseTrainingLog.setCourseSectionAction(action);
                    }
                }

                courseTrainingLog.setType(TrainingType.SELF);
                courseTrainingLog.setOrganization(TrainingType.SELF.getLabel());

                StringBuilder sb = new StringBuilder();
                if (!courseSection.getName().trim().isEmpty()) sb.append(courseSection.getName().trim());
                if (!courseSection.getDescription().isEmpty()) sb.append("\r\n" + courseSection.getDescription().trim());

                courseTrainingLog.setTrainingCourse(sb.toString());

            } else {

                // 2020-05-08 CompleteDate : class, ???????????????, ???????????? -> ?????? ???????????? ??????
                log.info("2. {} CompleteDate : {}"
                        , courseAccount.getCourse().getCourseMaster().getCourseName()
                        , courseAccount.getToDate());
                courseTrainingLog.setCompleteDate(DateUtil.getStringToDate(courseSection.getStudyDate()));

                courseTrainingLog.setTrainingTime(courseSection.getSecond());

                courseTrainingLog.setType(TrainingType.OTHER);
                courseTrainingLog.setOrganization(courseAccount.getCourse().getTeam());

                // class training ??? ????????? ????????? + ????????? + ?????????????????? ????????? ?????????.
                if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0102")) {

                    StringBuilder sb = new StringBuilder();

                    // class training????????? ????????? 1????????? ???????????? ???????????? ??????????????? ?????????????????? ????????????.
                    if (courseSection.getCourse().getSections().size() == 1
                            && courseSection.getCourse().getTitle().trim().equals(courseSection.getName().trim())) {
                        if (!courseSection.getName().trim().isEmpty()) sb.append(courseSection.getName().trim());
                        if (!courseSection.getDescription().isEmpty()) sb.append("\r\n" + courseSection.getDescription().trim());
                    } else {
                        if (!courseSection.getCourse().getTitle().trim().isEmpty()) sb.append(courseSection.getCourse().getTitle().trim());
                        if (!courseSection.getName().trim().isEmpty()) sb.append("\r\n" + courseSection.getName().trim());
                        if (!courseSection.getDescription().isEmpty()) sb.append("\r\n" + courseSection.getDescription().trim());
                    }
                    courseTrainingLog.setTrainingCourse(sb.toString());
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (!courseSection.getName().trim().isEmpty()) sb.append(courseSection.getName().trim());
                    if (!courseSection.getDescription().isEmpty()) sb.append("\r\n" + courseSection.getDescription().trim());
                    courseTrainingLog.setTrainingCourse(sb.toString());
                }
            }

            // Employee trining log ??????
            binderLogService.saveLog(courseTrainingLog);

            // ?????? ????????? ??????????????? ?????? ??????
            for(CourseSectionAction courseSectionAction : courseAccount.getCourseSectionActions()) {
                CourseSectionAction courseSectionAction1 = courseSectionActionService.getById(courseSectionAction.getId());
                courseSectionAction1.setLogApplyDate(DateUtil.getTodayDate());
                courseSectionAction1.setIsLogApply("1");
                courseSectionActionService.save(courseSectionAction1);
            }

            log.info("Training Log ?????? ?????? : User {}", courseAccount.getAccount().getName());
        }
    }

    // ?????? ??????????????? ????????? ??????.
    @Transactional
    public String uploadTrainingLog(String userId, MultipartFile multipartFile, boolean isUploadDataDelete) {

        // TODO : 2020-05-10 ?????? ???????????? ????????? ??????
        boolean isSuccess = true;

        // TODO: 2020-01-02 ???????????? ???????????? ????????? ???????????? ?????????????????? 1???????????? ????????? ????????? ?????? ?????? ????????? ???????????? ??????.
        // ??????????????? ??????(isUpload : 0=>?????????????????????, 1=>????????? ??????)
        //int rowCount = courseTrainingLogRepository.findAllByAccount_UserIdAndIsUpload(userId, "0").size();

        // ????????? ??????????????? ????????? ????????? ?????? ?????????.
        //if (rowCount > 0) return false;

        // ???????????? ?????? ?????? ????????? ????????? ????????????.
        if(isUploadDataDelete) {

            // ????????? ????????? ????????????.
            uploadTriningLogDelete(userId);
        }

        Account user = userService.getAccountByUserId(userId);

        try {
            XWPFDocument doc = new XWPFDocument(multipartFile.getInputStream());
            boolean hasError = false;
            List<XWPFTable> tables = doc.getTables();
//            logger.trace("Table Size : " + tables.size());
            if (tables.size() == 3) {
                XWPFTable headerTable = tables.get(0);
                boolean isHeader = headerTable.getNumberOfRows() == 2 && headerTable.getRow(0).getTableCells().size() == 4;
                XWPFTable logTable = tables.get(1);

                boolean isLogTable = logTable.getNumberOfRows() > 1 && logTable.getRow(0).getTableCells().size() == 4;
                if (isHeader && isLogTable) {
                    String empNo = headerTable.getRow(1).getCell(3).getText();
                    log.debug("@Employee No : {}", empNo);

                   /* if (!empNo.toUpperCase().equals(user.getComNum().toUpperCase())) {
                        return "Employee Training Log ?????? ??? Emp No??? ????????????.";
                    }*/
                    List<CourseTrainingLog> trainingLogs = new ArrayList<>();
                    for (int i = 1; i < logTable.getNumberOfRows(); i++) {
                        XWPFTableRow row = logTable.getRow(i);

                        String completionDate = row.getCell(0).getText().trim();
                        // ?????? ?????? ??????
                        completionDate  = completionDate.replaceAll(" ", "");
                        // IDEOGRAPHIC SPACE ??? ????????? ???????????? \u3000
                        // HTML ??????????????? &#12288;
                        // ?????? ????????? ????????? ?????? ????????? ??????(display ?????? ??????) ???????????? ???????????? ?????? ??????
                        completionDate = completionDate.replaceAll("\\p{Z}", "");

                        // ????????? ????????? ????????????.
                        String trainingCourse = row.getCell(1).getText().trim();
                        trainingCourse = trainingCourse.replaceAll("???", "-");
//                        trainingCourse = trainingCourse.replaceAll(" ", " ");
                        trainingCourse = trainingCourse.replaceAll("\\p{Z}", " ");
                        trainingCourse = trainingCourse.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");
                        trainingCourse  = trainingCourse.replaceAll(System.getProperty("line.separator"), "\n");

                        String trainingHr = row.getCell(2).getText();
                        if(trainingHr.isEmpty())
                            trainingHr = "0";
                        String organization = row.getCell(3).getText();

                        if(completionDate.isEmpty() && trainingCourse.isEmpty() && trainingHr.equals("0") && organization.isEmpty())
                            break;

                        double time = Double.parseDouble(trainingHr) * 3600;
                        boolean isSelfTraining = TrainingType.SELF.getLabel().toUpperCase().equals(organization.toUpperCase());

                        log.info("Training log Upload : {}, {}, {}, {}, {}", i, trainingCourse, completionDate, organization, user.getName());

                        CourseTrainingLog trainingLog = CourseTrainingLog.builder()
                                .trainingCourse(trainingCourse)
                                .completeDate(DateUtil.getStringToDate(completionDate, "dd-MMM-yyyy"))
                                .trainingTime((int) time)
                                .type(isSelfTraining ? TrainingType.SELF : TrainingType.OTHER)
                                .isUpload("1")
                                .organization(organization)
                                .account(user)
                                .build();

                        courseTrainingLogRepository.save(trainingLog);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Training log Upload Error : {}, {}, {}", user.getUserId(), user.getName(), e.getMessage());
            LogUtil.write("BinderLogService/uploadTrainingLog", "error", e.getMessage());
            e.printStackTrace();
            return "Training Log??? ????????? ??? ??? ????????????. ???????????? ????????? ????????? ?????????.";
        }

        return "";
    }
}
