package com.dtnsm.lms.xdocreport;

import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.xdocreport.dto.JobDescriptionSign;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
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
@RequiredArgsConstructor
public class JobDescriptionReportService {
    private final UserJobDescriptionRepository userJobDescriptionRepository;
    private InputStream in = JobDescriptionReportService.class.getResourceAsStream("JD_sign.docx");
    @Value("${file.xdoc-upload-dir}")
    private String tempDir;
    private XWPFDocument signDoc;

    @PostConstruct
    public void init() throws Exception {
        //디렉토리가 존재하지 않는 경우 생성
        Files.createDirectories(Paths.get(tempDir).toAbsolutePath().normalize());
        signDoc = new XWPFDocument(in);
    }

    public void generateReport(JobDescriptionSign jobDescriptionSign, InputStream jdInputStream, String pdfOutput, Integer userJobDescriptionId) {
        String docxFileName = tempDir + "\\tmp_jd_sign_" + System.currentTimeMillis() + ".docx";
        String outputDocx = tempDir + "\\JD_"+userJobDescriptionId+".docx";
        File tempFile = new File(docxFileName);

        try {
            XWPFTable empTbl = signDoc.getTables().get(0);
            XWPFTable mngTbl = signDoc.getTables().get(1);

            FileOutputStream tempFileOutput = new FileOutputStream(tempFile);
            XWPFDocument jdDoc = new XWPFDocument(jdInputStream);
            jdDoc.setTable(1, empTbl);
            jdDoc.setTable(2, mngTbl);

            jdDoc.write(tempFileOutput);
            tempFileOutput.flush();
            tempFileOutput.close();

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(new FileInputStream(tempFile), TemplateEngineKind.Velocity);
            FieldsMetadata metadata = report.createFieldsMetadata();
            metadata.load("jd", JobDescriptionSign.class);
            metadata.addFieldAsImage("sign1");//직원 서명
            metadata.addFieldAsImage("sign2");//팀/부서장 서명

            IContext context = report.createContext();
            context.put("jd", jobDescriptionSign);
            context.put("sign1", jobDescriptionSign.getEmpSign());
            context.put("sign2", jobDescriptionSign.getMngSign());

            report.process(context, new FileOutputStream(new File(outputDocx)));
            //Docx4J
            WordprocessingMLPackage wmlPackage = Docx4J.load(new FileInputStream(new File(outputDocx)));
            Docx4J.toPDF(wmlPackage, new FileOutputStream(pdfOutput));

            PdfDocument pdf = new PdfDocument();
            pdf.loadFromStream(new FileInputStream(new File(pdfOutput)));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            pdf.saveToStream(outputStream, FileFormat.HTML);

            String htmlContent = outputStream.toString("UTF-8");

            UserJobDescription userJobDescription = userJobDescriptionRepository.findById(userJobDescriptionId).get();
            userJobDescription.setHtmlContent(htmlContent);
            userJobDescription.setPageCount(pdf.getPages().getCount());
            userJobDescriptionRepository.save(userJobDescription);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if(tempFile.exists()) {
//                tempFile.delete();
//            }
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
