package com.dtnsm.lms.service;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.DTO.CommonUtilities;
import com.dtnsm.lms.domain.datasource.CertificateSource;
import com.dtnsm.lms.domain.datasource.EmployeeTrainingLogSource;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CertificateFileRepository;
import com.dtnsm.lms.repository.CourseCertificateInfoRepository;
import com.dtnsm.lms.repository.CourseCertificateLogRepository;
import com.dtnsm.lms.repository.CourseCertificateNumberRepository;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.groupdocs.assembly.DataSourceInfo;
import com.groupdocs.assembly.DocumentAssembler;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CourseCertificateService {

    @Autowired
    CourseCertificateNumberRepository courseCertificateNumberRepository;

    @Autowired
    CourseCertificateInfoRepository courseCertificateInfoRepository;

    @Autowired
    CourseCertificateLogRepository courseCertificateLogRepository;

    @Autowired
    CertificateFileService certificateFileService;

    @Autowired
    CertificateFileRepository certificateFileRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private SignatureService signatureService;

    @Autowired
    CourseAccountService courseAccountService;

    public FileUploadProperties prop;

    public CourseCertificateService(FileUploadProperties prop) {
        this.prop = prop;
    }

    // 새로운 수료증 번호 받기
    public CourseCertificateNumber newCertificateNumber(String cerText, String cerYear, CourseAccount courseAccount) {

        CourseCertificateNumber courseCertificateNumber = courseCertificateNumberRepository.findAllByCerTextAndCerYear(cerText, cerYear);

        if (courseCertificateNumber == null) {
            courseCertificateNumber = courseCertificateNumberRepository.save(new CourseCertificateNumber(cerText, cerYear));
        }

        courseCertificateNumber.setCerSeq(courseCertificateNumber.getCerSeq() + 1);

        courseCertificateNumber.setFullNumber(String.format("%s-%s-%03d", courseCertificateNumber.getCerText()
                , courseCertificateNumber.getCerYear()
                , courseCertificateNumber.getCerSeq()));

        CourseCertificateNumber courseCertificateNumber1 = courseCertificateNumberRepository.save(courseCertificateNumber);

        // 수료증 정보를 생성한다.
        CreateCertificate(courseCertificateNumber1, courseAccount);

        return courseCertificateNumber1;
    }

    // 현재 번호 받기
    public CourseCertificateNumber getCurrentCertificateNumber(String cerText, String cerYear) {
        return courseCertificateNumberRepository.findAllByCerTextAndCerYear(cerText, cerYear);
    }

    // Certification 기록
    public CourseCertificateLog CreateCertificate(CourseCertificateNumber courseCertificateNumber, CourseAccount courseAccount) {

        // 수료증 기본 정보를 가지고 온다.
        CourseCertificateInfo courseCertificateInfo = courseCertificateInfoRepository.findByIsActive(1);

        // 수료증 기본 정보가 없으면 종료한다.
        if (courseCertificateInfo == null ) return null;

        // 강사의 서명을 가지고 온다.
        Signature optionalSignature = signatureService.getSignature(courseCertificateInfo.getCerManager1().getUserId());
        String sign1 = optionalSignature != null ? optionalSignature.getBase64signature() : "";

        // 대표자의 서명을 가지고 온다.
        Signature optionalSignature2 = signatureService.getSignature(courseCertificateInfo.getCerManager2().getUserId());
        String sign2 = optionalSignature2 != null ? optionalSignature2.getBase64signature() : "";

        CourseCertificateLog courseCertificateLog = new CourseCertificateLog();
        courseCertificateLog.setCerNo(courseCertificateNumber.getFullNumber());
        courseCertificateLog.setSopName(courseCertificateInfo.getSopName());
        courseCertificateLog.setSopEffectiveDate(courseCertificateInfo.getSopEffectiveDate());
        courseCertificateLog.setCerManager1(courseCertificateInfo.getCerManager1());
        courseCertificateLog.setCerManager2(courseCertificateInfo.getCerManager2());
        courseCertificateLog.setCerManagerSign1(sign1);
        courseCertificateLog.setCerManagerSign2(sign2);
        courseCertificateLog.setCerDate(DateUtil.getTodayDate());
        courseCertificateLog.setCourseAccount(courseAccount);
        courseCertificateLog.setAccount(courseAccount.getAccount());
        courseCertificateLog.setCerManagerText1(courseCertificateInfo.getCerManagerText1());
        courseCertificateLog.setCerManagerText2(courseCertificateInfo.getCerManagerText2());

        CourseCertificateLog courseCertificateLog1 = courseCertificateLogRepository.save(courseCertificateLog);

        // 과정 사용자 정보에 수료증 정보를 업데이트 한다.
        courseAccount.setCertificateBindDate(DateUtil.getTodayDate());
        courseAccount.setCourseCertificateLog(courseCertificateLog1);
        CourseAccount courseAccount1 = courseAccountService.save(courseAccount);

        // 수료증 파일을 생성한다.
        createCertificationFile(courseAccount1);

        return courseCertificateLog1;
    }

    public CourseCertificateLog getCourseCertificateLog(Long docId) {
        return courseCertificateLogRepository.findByCourseAccount_Id(docId);
    }


    public boolean createCertificationFile(CourseAccount courseAccount) {

        // GroupDoc 라이센스
//        CommonUtilities.applyLicense();

//        String userId = SessionUtil.getUserId();
        String userId = courseAccount.getAccount().getUserId();
        long timestamp = System.currentTimeMillis();


        // Employee Training Log Template 파일 및 출력 파일 지정
        String srcCertificationTemplateFileName = "etl_certi.docx";

        String outputCertificationFileName = String.format("%s_%s_certi.pdf", timestamp, userId);



        // 파일 기본 경로
        String sourceRootFoloer = prop.getXdocUploadDir() + "Data//Storage//";
        String outputRootFoloer = prop.getCertificateUploadDir();

        // 수료증 Full Path
        String srcCertificationFilePath = sourceRootFoloer + srcCertificationTemplateFileName;
//        String srcCertificationFilePath = CurriculumVitaeReportService.class.getResource(srcCertificationTemplateFileName).getPath();
//        InputStream is = CurriculumVitaeReportService.class.getResourceAsStream(srcCertificationTemplateFileName);

        String outputCertificationFilePath = outputRootFoloer + outputCertificationFileName;

//        System.out.println(srcCertificationFilePath);

        try {
            // Employee Training Log Data
            CertificateSource certificateSource = this.getCertificateSource(userId, timestamp, courseAccount.getId());

            // Employee Training Log Data를 source에 추가한다.
            DataSourceInfo dataSourceInfo = new DataSourceInfo(certificateSource, null);

            DocumentAssembler assembler = new DocumentAssembler();
//            assembler.getKnownTypes().add(EmployeeTrainingLogSource.class);
//            assembler.getKnownTypes().add(CourseTrainingLog.class);
//            assembler.getKnownTypes().add(Account.class);

//            assembler.setOptions(DocumentAssemblyOptions.UPDATE_FIELDS_AND_FORMULAS);
//            assembler.assembleDocument(in, out, new DataSourceInfo( accountList, "accountList"));

            // 1. Employee Training Log File 생성
            assembler.assembleDocument(srcCertificationFilePath, outputCertificationFilePath , dataSourceInfo);
//            File file = new File(outputCertificationFilePath);
//            FileOutputStream os = new FileOutputStream(file);
//
//            assembler.assembleDocument(is, os, dataSourceInfo);
//            out.flush();
//            out.close();

            // 수료증 파일 생성후 File 정보를 기록한다.
            File file = new File(outputCertificationFilePath);
            if(file.exists()) {
                if (courseAccount != null) {
                    Path source = Paths.get(outputCertificationFilePath);
                    String mimeType = Files.probeContentType(source);
                    String fileName = courseAccount.getCourse().getTitle() + " 수료증.pdf";

                    CertificateFile uploadFile = new CertificateFile(fileName, outputCertificationFileName, file.length(), mimeType, courseAccount.getAccount(), courseAccount);
                    certificateFileRepository.save(uploadFile);
                }
            }

        } catch (Exception exp) {
            System.out.println("Exception: " + exp.getMessage());
        }

        return true;
    }


    // 수료증 PDF 파일 생성에 필요한 기본 정보 가져오기
    public CertificateSource getCertificateSource(String userId, long timestamp, long docId) {

        // 라이센스
//        CommonUtilities.applyLicense();

        // 사용자별 과정 정보
        CourseAccount courseAccount = courseAccountService.getById(docId);

        // 사용자별 수료증 로그
        CourseCertificateLog certificateLog = courseCertificateLogRepository.findByCourseAccount_Id(docId);

        // 관리자1 정보
        Account man1 = certificateLog.getCerManager1();
        // 관리자2 정보
        Account man2 = certificateLog.getCerManager2();

        // Employee Training Log Data Source 생성
        CertificateSource trainingLogSource = new CertificateSource("CertificateSource");

        // 관리자1 사인정보
        byte[] imageBytes1 = null;
        // 관리자2 사인정보
        byte[] imageBytes2 = null;

        if (!certificateLog.getCerManagerSign1().isEmpty()) {
            String data = certificateLog.getCerManagerSign1().split(",")[1];
            imageBytes1 = DatatypeConverter.parseBase64Binary(data);
        }

        if (!certificateLog.getCerManagerSign2().isEmpty()) {
            String data = certificateLog.getCerManagerSign2().split(",")[1];
            imageBytes2 = DatatypeConverter.parseBase64Binary(data);
        }

        // TODO :수료증 교육기간 설정(사용자별 교육시작일 ~ 교육종료일로 설정)

        String prior = "";

        // 2020-09-08 Self Training 인 경우는 교육완료일(Complete Date)를 수료증에 기록한다.
        if(courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {

            prior = String.format("On %s"
                    , DateUtil.getDateToString(courseAccount.getCertificateBindDate(), "dd MMM yyyy"));

        } else{     // 2020-09-08 Self Training 이 아닌 경우는 개인별 교육기간을 수료증에 기록한다.
            // 과정 시작일과 종료일이 같은 경우
            if (courseAccount.getFromDate().equals(courseAccount.getToDate())) {
                prior = String.format("On %s", courseAccount.getFromDate());
            } else {
                prior = String.format("From %s to %s"
                        , DateUtil.getDateToString(DateUtil.getStringToDate(courseAccount.getFromDate()), "dd MMM yyyy")
                        , DateUtil.getDateToString(DateUtil.getStringToDate(courseAccount.getToDate()), "dd MMM yyyy"));
            }
        }

        // TODO : Job Title 로 변경
        trainingLogSource.setNo(certificateLog.getCerNo());
        trainingLogSource.setName(courseAccount.getAccount().getEngName());
        trainingLogSource.setCourseTitle(courseAccount.getCourse().getTitle());
        trainingLogSource.setDepart1(certificateLog.getCerManagerText1().isEmpty() ? man1.getOrgDepart() : certificateLog.getCerManagerText1() );
        trainingLogSource.setDepart2(certificateLog.getCerManagerText2().isEmpty() ? man2.getOrgDepart() : certificateLog.getCerManagerText2() );
        trainingLogSource.setManName1(man1.getEngName().isEmpty() ? man1.getName() : man1.getEngName());
        trainingLogSource.setManName2(man2.getEngName().isEmpty() ? man2.getName() : man2.getEngName());
        trainingLogSource.setSign1(imageBytes1);
        trainingLogSource.setSign2(imageBytes2);
        trainingLogSource.setPrior(prior);
        trainingLogSource.setSopName(certificateLog.getSopName());
        trainingLogSource.setSopEffDate(DateUtil.getDateToString(DateUtil.getStringToDate(certificateLog.getSopEffectiveDate()), "dd-MMM-yyyy"));

        return trainingLogSource;
    }

    public boolean getCourseCertificateActive() {
        if (courseCertificateInfoRepository.findByIsActive(1) == null) {
            return false;
        }
        return true;
    }
}


