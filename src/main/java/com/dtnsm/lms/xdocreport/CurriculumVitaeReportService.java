//package com.dtnsm.lms.xdocreport;
//
//import com.dtnsm.lms.component.DocumentUtils;
//import com.dtnsm.lms.domain.CurriculumVitae;
//import com.dtnsm.lms.repository.CurriculumVitaeRepository;
//import com.dtnsm.lms.xdocreport.dto.CV;
//import fr.opensagres.xdocreport.core.XDocReportException;
//import fr.opensagres.xdocreport.document.IXDocReport;
//import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
//import fr.opensagres.xdocreport.template.IContext;
//import fr.opensagres.xdocreport.template.TemplateEngineKind;
//import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.docx4j.Docx4J;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.io.*;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class CurriculumVitaeReportService {
//    private final CurriculumVitaeRepository curriculumVitaeRepository;
//    private InputStream in = JobDescriptionReportService.class.getResourceAsStream("CV.docx");
//    private IXDocReport report;
//    @Value("${file.xdoc-upload-dir}")
//    private String tempDir;
//
//    @PostConstruct
//    public void init() throws Exception {
//        report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
//        FieldsMetadata metadata = report.createFieldsMetadata();
//        metadata.load("cv", CV.class, false);
//        metadata.addFieldAsImage("sign");//직원 서명
//    }
//
//    public void generateReport(CV cv, String docxFile, Integer cvId) {
//        try {
//
//            IContext context = report.createContext();
//            context.put("cv", cv);
//            context.put("sign", cv.getSign());
//            //데이터 바인딩 후 워드 문서 생성
//            OutputStream os = new FileOutputStream(new File(docxFile));
//            report.process(context, os);
//            os.flush();
//            os.close();
//
//            //Word -> PDF 변환
//            WordprocessingMLPackage wmlPackage = Docx4J.load(new FileInputStream(new File(docxFile)));
//            String outputPdf = docxFile.substring(0, docxFile.lastIndexOf(".")) + ".pdf";
//            Docx4J.toPDF(wmlPackage, new FileOutputStream(new File(outputPdf)));
//
//            //PDF -> HTML 변환
////            PdfDocument pdf = new PdfDocument();
////            pdf.loadFromStream(new FileInputStream(new File(outputPdf)));
////            ByteArrayOutputStream html = new ByteArrayOutputStream();
////            pdf.saveToStream(html, FileFormat.HTML);
//
//            CurriculumVitae curriculumVitae = curriculumVitaeRepository.findById(cvId).get();
//            curriculumVitae.setHtmlContent(DocumentUtils.convertHTML(new FileInputStream(new File(docxFile))));
////            curriculumVitae.setPageCount(pdf.getPages().getCount());
//
//            curriculumVitaeRepository.save(curriculumVitae);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XDocReportException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @PreDestroy
//    public void destroy() {
//        try {
//            if(in != null) {
//                in.close();
//                in = null;
//            }
//        } catch (IOException e) {
//        }
//    }
//}
