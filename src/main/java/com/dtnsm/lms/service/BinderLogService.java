package com.dtnsm.lms.service;

import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseTrainingLog;
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

    // 사용자별 업로드 자료 삭제
    public void uploadTriningLogDelete(String userId) {

        List<CourseTrainingLog> courseTrainingLogs = courseTrainingLogRepository.findAllByAccount_UserIdAndIsUpload(userId, "1");
        if (courseTrainingLogs.size() > 0) {
            for (CourseTrainingLog courseTrainingLog : courseTrainingLogs) {
                courseTrainingLogRepository.delete(courseTrainingLog);
            }
        }
    }

    // 초기 교육자료를 업로드 한다.
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
                    String completionDate = row.getCell(0).getText();
                    String trainingCourse = row.getCell(1).getText();
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

        return true;
    }

}
