package com.dtnsm.lms.service;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseCertificateInfo;
import com.dtnsm.lms.domain.CourseCertificateLog;
import com.dtnsm.lms.domain.CourseCertificateNumber;
import com.dtnsm.lms.repository.CourseCertificateInfoRepository;
import com.dtnsm.lms.repository.CourseCertificateLogRepository;
import com.dtnsm.lms.repository.CourseCertificateNumberRepository;
import com.dtnsm.lms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseCertificateService {

    @Autowired
    CourseCertificateNumberRepository courseCertificateNumberRepository;

    @Autowired
    CourseCertificateInfoRepository courseCertificateInfoRepository;

    @Autowired
    CourseCertificateLogRepository courseCertificateLogRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private SignatureService signatureService;

    @Autowired
    CourseAccountService courseAccountService;


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
        String sign2 = optionalSignature != null ? optionalSignature2.getBase64signature() : "";

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
        courseAccount.setCourseCertificateLog(courseCertificateLog1);
        courseAccountService.save(courseAccount);

        return courseCertificateLog1;
    }


    //
    public CourseCertificateLog getCourseCertificateLog(Long docId) {

        return courseCertificateLogRepository.findByCourseAccount_Id(docId);
    }
}
