package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseCertificateNumber;
import com.dtnsm.lms.domain.Privilege;
import com.dtnsm.lms.repository.CourseCertificateInfoRepository;
import com.dtnsm.lms.repository.CourseCertificateLogRepository;
import com.dtnsm.lms.repository.CourseCertificateNumberRepository;
import com.google.common.base.Optional;
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
    CourseAccountService courseAccountService;


    // 새로운 수료증 번호 받기
    public CourseCertificateNumber newCertificateNumber(String cerText, String cerYear, CourseAccount courseAccount) {

        CourseCertificateNumber courseCertificateNumber = courseCertificateNumberRepository.findAllByCerTextAndCerYear(cerText, cerYear);

        if (courseCertificateNumber == null) {
            courseCertificateNumber = courseCertificateNumberRepository.save(new CourseCertificateNumber(cerText, cerYear));
        }

        courseCertificateNumber.setCerSeq(courseCertificateNumber.getCerSeq() + 1);

        courseCertificateNumber.setFullNumber(String.format("%s-%s-%04d", courseCertificateNumber.getCerText()
                , courseCertificateNumber.getCerYear()
                , courseCertificateNumber.getCerSeq()));

        return courseCertificateNumberRepository.save(courseCertificateNumber);
    }

    // 현재 번호 받기
    public CourseCertificateNumber getCurrentCertificateNumber(String cerText, String cerYear) {
        return courseCertificateNumberRepository.findAllByCerTextAndCerYear(cerText, cerYear);
    }

    // Certification 기록
    public void CreateCertificate(CourseAccount courseAccount) {



    }
}
