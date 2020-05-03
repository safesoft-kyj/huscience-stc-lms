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

    // Employee Training Log 데이터 생성
    public EmployeeTrainingLogSource getEmployeeTrainingLog(String userId, long timestamp) {

        // 라이센스
//        CommonUtilities.applyLicense();

        // Employee Training Log Data Source 생성
        EmployeeTrainingLogSource trainingLogSource = new EmployeeTrainingLogSource("EmployeeTrainingLogSource");

        // 사용자별 로그를 가지고 온다.
        List<CourseTrainingLog> courseTrainingLogs = binderLogService.getAllByUserOrderByCompleteDateAsc(userId);

        // 나의 서명을 가지고 온다.
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

    // 사용자별 업로드 자료 삭제
    public void uploadTriningLogDelete(String userId) {

        List<CourseTrainingLog> courseTrainingLogs = courseTrainingLogRepository.findAllByAccount_UserIdAndIsUpload(userId, "1");
        if (courseTrainingLogs.size() > 0) {
            for (CourseTrainingLog courseTrainingLog : courseTrainingLogs) {
                courseTrainingLogRepository.delete(courseTrainingLog);
            }
        }
    }

    public void createTrainingLog(CourseAccount courseAccount) {

        //  강의별로 로그를 생성시킨다.
        for (CourseSection courseSection : courseAccount.getCourse().getSections()) {

            CourseTrainingLog courseTrainingLog = new CourseTrainingLog();
            courseTrainingLog.setAccount(courseAccount.getAccount());
            courseTrainingLog.setCompleteDate(DateUtil.getTodayDate());
            courseTrainingLog.setIsUpload("0");

            // Self training  이면
            if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {
                for (CourseSectionAction action : courseSection.getCourseSectionActions()) {

                    // 학습시간이 지정시간보다 큰경우는 학습시간으로 아니면 지정시간으로 저장한다.
                    if ( action.getTotalUseSecond() > courseSection.getSecond() ) {
                        courseTrainingLog.setTrainingTime(action.getTotalUseSecond());
                    } else {
                        courseTrainingLog.setTrainingTime(courseSection.getSecond());
                    }

                    courseTrainingLog.setCourseSectionAction(action);
                }

                courseTrainingLog.setType(TrainingType.SELF);
                courseTrainingLog.setOrganization(TrainingType.SELF.getLabel());

                StringBuilder sb = new StringBuilder();
                if (!courseSection.getName().trim().isEmpty()) sb.append(courseSection.getName().trim());
                if (!courseSection.getDescription().isEmpty()) sb.append("\r\n" + courseSection.getDescription().trim());

                courseTrainingLog.setTrainingCourse(sb.toString());

            } else {
                courseTrainingLog.setTrainingTime(courseSection.getSecond());

                courseTrainingLog.setType(TrainingType.OTHER);
                courseTrainingLog.setOrganization(courseAccount.getCourse().getTeam());

                // class training 인 경우는 과정명 + 강의명 + 강의설명으로 로그를 남긴다.
                if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0102")) {

                    StringBuilder sb = new StringBuilder();
                    if (!courseSection.getCourse().getTitle().trim().isEmpty()) sb.append(courseSection.getCourse().getTitle().trim());
                    if (!courseSection.getName().trim().isEmpty()) sb.append("\r\n" + courseSection.getName().trim());
                    if (!courseSection.getDescription().isEmpty()) sb.append("\r\n" + courseSection.getDescription().trim());

                    courseTrainingLog.setTrainingCourse(sb.toString());
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (!courseSection.getName().trim().isEmpty()) sb.append(courseSection.getName().trim());
                    if (!courseSection.getDescription().isEmpty()) sb.append("\r\n" + courseSection.getDescription().trim());
                    courseTrainingLog.setTrainingCourse(sb.toString());
                }
            }

            binderLogService.saveLog(courseTrainingLog);
        }
    }

    // 초기 교육자료를 업로드 한다.
    @Transactional
    public boolean uploadTrainingLog(String userId, MultipartFile multipartFile, boolean isUploadDataDelete) {

        boolean isSuccess = true;

        // TODO: 2020-01-02 시스테발 발생로그 부분은 테스트가 완료되는데로 1건이라도 있으면 업로드 되지 않게 주석을 제거해야 한다.
        // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
        //int rowCount = courseTrainingLogRepository.findAllByAccount_UserIdAndIsUpload(userId, "0").size();

        // 시스템 발생로그가 있으면 업로드 하지 못한다.
        //if (rowCount > 0) return false;

        // 업로드전 모든 기존 업로드 자료를 삭제한다.
        if(isUploadDataDelete) {

            // 업로드 로그를 삭제한다.
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

                    if (!empNo.toUpperCase().equals(user.getComNum().toUpperCase())) {
                        log.warn("로그인한 사용자의 트레이닝 로그 파일 Emp No가 다름.");

//                     attributes.addFlashAttribute("message", "Training Log 파일의 사번과 로그인한 사용자의 사번이 다릅니다.");
                        return false;
                    }
                    List<CourseTrainingLog> trainingLogs = new ArrayList<>();
                    for (int i = 1; i < logTable.getNumberOfRows(); i++) {
                        XWPFTableRow row = logTable.getRow(i);

                        String completionDate = row.getCell(0).getText().trim();
                        // 공백 문자 제거
                        completionDate  = completionDate.replaceAll(" ", "");
                        // IDEOGRAPHIC SPACE 라 불리는 유니코드 \u3000
                        // HTML 표현으로는 &#12288;
                        // 폰트 지원이 없으면 눈에 보이지 않는(display 되지 않는) 코드로만 존재하는 공백 등등
                        completionDate = completionDate.replaceAll("\\p{Z}", "");

                        // 깨지는 문자를 변경한다.
                        String trainingCourse = row.getCell(1).getText().trim();
                        trainingCourse = trainingCourse.replaceAll("–", "-");
//                        trainingCourse = trainingCourse.replaceAll(" ", " ");
                        trainingCourse = trainingCourse.replaceAll("\\p{Z}", " ");
                        trainingCourse = trainingCourse.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");
                        trainingCourse  = trainingCourse.replaceAll(System.getProperty("line.separator"), "\n");

                        String trainingHr = row.getCell(2).getText();
                        String organization = row.getCell(3).getText();
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
            return false;
        }

        return true;
    }

}
