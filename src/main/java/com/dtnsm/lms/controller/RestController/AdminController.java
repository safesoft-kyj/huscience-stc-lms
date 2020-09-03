package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.service.CourseCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CourseCertificateService courseCertificateService;

    private final CourseAccountRepository courseAccountRepository;

    @GetMapping("/certificate/create")
    public String monthTotalCalendar(@RequestParam("docId") Long docId) {

        Optional<CourseAccount> optional = courseAccountRepository.findById(docId);

        if(optional.isPresent()) {
            CourseAccount courseAccount = optional.get();
            courseCertificateService.createCertificationFile(courseAccount);
        }

        return "Success";
    }
}