package com.dtnsm.lms.controller.RestController;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.domain.CertificateFile;
import com.dtnsm.lms.service.CertificateFileService;
import com.dtnsm.lms.service.SignatureService;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/certificate")
public class CertificateRestController {

    @Autowired
    private CertificateFileService fileService;

    @PostMapping("/save")
    public boolean signSave(@RequestParam("pdf") MultipartFile file, @RequestParam("filename") String fileName){

        CertificateFile uploadFile = fileService.storeFile(file, fileName);

        if (uploadFile == null) return false;
        else return true;
    }
}
