package com.dtnsm.lms.xdocreport;

import com.dtnsm.lms.util.DocumentLicense;
import com.dtnsm.lms.xdocreport.dto.CV;
import com.groupdocs.assembly.DataSourceInfo;
import com.groupdocs.assembly.DocumentAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

@Component
@Slf4j
public class CurriculumVitaeReportService {
    private InputStream is = CurriculumVitaeReportService.class.getResourceAsStream("CV2.docx");
    @Value("${groupdocs.license}")
    private String license;

    @PostConstruct
    public void init() {
        try {
            com.groupdocs.conversion.License lic = new com.groupdocs.conversion.License();
            lic.setLicense(new FileInputStream(new File(license)));
            log.info("@License Loaded.");
        } catch (Exception error) {
            log.error("Error : {}", error.toString());
        }
    }

    public boolean assembleDocument(CV cv, String targetPath) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(targetPath);
            DataSourceInfo dataSourceInfo = new DataSourceInfo(cv, "cv");
            DocumentAssembler assembler = new DocumentAssembler();
            return assembler.assembleDocument(is, os, dataSourceInfo);
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
