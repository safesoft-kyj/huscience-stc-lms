package com.dtnsm.lms.xdocreport;

import com.dtnsm.lms.xdocreport.dto.CV;
import com.dtnsm.lms.xdocreport.dto.EducationDTO;
import com.dtnsm.lms.xdocreport.dto.JobDescriptionSign;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class CurriculumVitaeReportService {
    private InputStream in = JobDescriptionReportService.class.getResourceAsStream("CV.docx");
    private IXDocReport report;
    @Value("${file.xdoc-upload-dir}")
    private String tempDir;

    @PostConstruct
    public void init() throws Exception {
        report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
        FieldsMetadata metadata = report.createFieldsMetadata();
        metadata.load("cv", CV.class, false);
        metadata.addFieldAsImage("sign");//직원 서명
    }

    public void generateReport(CV cv, HttpServletResponse response) {
        try(OutputStream os = response.getOutputStream()) {
            IContext context = report.createContext();
            context.put("cv", cv);
            context.put("sign", cv.getSign());
//            context.put("history", cv.getCareerHistories());
            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
            report.convert(context, options, os);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if(in != null) {
                in.close();
                in = null;
            }
        } catch (IOException e) {
        }
    }
}
