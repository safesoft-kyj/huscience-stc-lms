package com.dtnsm.lms.service;

import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.TrainingType;
import com.dtnsm.lms.mybatis.dto.UserVO;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.CourseTrainingLogRepository;
import com.dtnsm.lms.util.DateUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BinderLogService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BinderLogService.class);

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

    public CourseTrainingLog saveLog(CourseTrainingLog courseTrainingLog) {
        return courseTrainingLogRepository.save(courseTrainingLog);
    }

    public List<CourseTrainingLog> getAllByUser(String userId){
        return courseTrainingLogRepository.findAllByAccount_UserId(userId);
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
            courseTrainingLog.setTrainingTime(courseSection.getSecond());

            for (CourseSectionAction action : courseSection.getCourseSectionActions()) {
                courseTrainingLog.setCourseSectionAction(action);
            }

            // Self training  이면
            if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {
                courseTrainingLog.setType(TrainingType.SELF);
                courseTrainingLog.setOrganization(TrainingType.SELF.getLabel());

                StringBuilder sb = new StringBuilder();
                if (!courseSection.getName().trim().isEmpty()) sb.append(courseSection.getName().trim());
                if (!courseSection.getDescription().isEmpty()) sb.append("\r\n" + courseSection.getDescription().trim());

                courseTrainingLog.setTrainingCourse(sb.toString());

            } else {
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
    public boolean uploadTrainingLog(String userId, MultipartFile multipartFile, boolean isUploadDataDelete) throws IOException {

        boolean isSuccess = true;

        // 발생구분별 로그(isUpload : 0=>시스템발생로고, 1=>업로드 로그)
        int rowCount = courseTrainingLogRepository.findAllByAccount_UserIdAndIsUpload(userId, "0").size();

        // 시스템 발생로그가 있으면 업로드 하지 못한다.
        if (rowCount > 0) return false;

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
            logger.trace("Table Size : " + tables.size());
            if (tables.size() == 3) {
                XWPFTable headerTable = tables.get(0);
                boolean isHeader = headerTable.getNumberOfRows() == 2 && headerTable.getRow(0).getTableCells().size() == 4;
                XWPFTable logTable = tables.get(1);

                boolean isLogTable = logTable.getNumberOfRows() > 1 && logTable.getRow(0).getTableCells().size() == 4;
                if (isHeader && isLogTable) {
                    String empNo = headerTable.getRow(1).getCell(3).getText();
                    logger.debug("@Employee No : {}", empNo);

                    if (!empNo.toUpperCase().equals(user.getComNum().toUpperCase())) {
                        logger.warn("로그인한 사용자의 트레이닝 로그 파일 Emp No가 다름.");

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
        } catch (IOException e) {
            return false;
        }

        return true;
    }

}
