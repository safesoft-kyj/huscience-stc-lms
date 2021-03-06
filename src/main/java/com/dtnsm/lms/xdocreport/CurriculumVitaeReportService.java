package com.dtnsm.lms.xdocreport;

import com.dtnsm.lms.util.DocumentConverter;
import com.dtnsm.lms.xdocreport.dto.CV;
import com.groupdocs.assembly.DataSourceInfo;
import com.groupdocs.assembly.DocumentAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurriculumVitaeReportService {
    private final DocumentConverter documentConverter;

//    @Value("${groupdocs.license}")
//    private String license;

//    @PostConstruct
//    public void init() {
//        try {
//            com.groupdocs.conversion.License lic = new com.groupdocs.conversion.License();
//            lic.setLicense(new FileInputStream(new File(license)));
//            log.info("@License Loaded.");
//        } catch (Exception error) {
//            log.error("Error : {}", error.toString());
//        }
//    }

//    public boolean assembleDocument(CV cv, String targetPath) {
//        try {
//            FileOutputStream os = new FileOutputStream(targetPath);
//            return assembleDocument(cv, os);
//        } catch (Exception e) {
//            log.error("error : {}", e);
//            return false;
//        }
//    }

    public boolean assembleDocument(CV cv, OutputStream os) {
        try {
            InputStream is = CurriculumVitaeReportService.class.getResourceAsStream("CV2.docx");
            DataSourceInfo dataSourceInfo = new DataSourceInfo(cv, "cv");
            return documentConverter.assembleDocument(is, os, dataSourceInfo);
        } catch (Exception error) {
            log.error("error : {}", error);
            error.getStackTrace();
            return false;
        } finally {
            if(os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (Exception e) {}
            }
        }
    }
}
