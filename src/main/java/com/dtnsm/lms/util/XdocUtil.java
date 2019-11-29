package com.dtnsm.lms.util;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.domain.Project;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.querydsl.sql.dml.Mapper;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.ClassPathImageProvider;
import fr.opensagres.xdocreport.document.images.FileImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;


public  class XdocUtil {

    public static Project getPorject() {
        Project project = new Project("Gildong Hong");
        project.setCourseTitle("KGCP/ICH-GCP Practice");
        project.setCertiNo("DTNSM-GCP-2019001");
        project.setCourseDate("On 04 MAR 2019");
        project.setFirstUserName("Lim, Hyunjin / QMO of Dt&SanoMedics");
        project.setSecondUserName("Kim, Kwangho / Registered Director of Dt&SanoMedics");
        project.setSopName("SOP-TM0002_RD08 v1.1");
        project.setEffectiveDate("04-MAR-2019");

        return project;
    }

    //
    public static void createXdocToHtml(FileUploadProperties prop, String fileName, SignatureRepository signatureRepository) {

        try {

            String inFilePath = prop.getXdocUploadDir() + fileName;
            String outFilePath = prop.getXdocUploadDir() + fileName + "_out.html";


            File inputFile = new File(inFilePath);

            InputStream in = new FileInputStream(inFilePath);

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            // 2) Create context Java model
            IContext context = report.createContext();

            Project project = getPorject();

            context.put("project", project);

            // 3) Generate report by merging Java model with the Docx
            OutputStream out = new FileOutputStream(new File(outFilePath));

//            report.process(context, out);
//            report.convert();
//            report.convert();
            Options options = Options.getTo(ConverterTypeTo.XHTML).via(ConverterTypeVia.XWPF);
//            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);

            report.convert(context, options, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }

    //
    public static void createXdocToPdf(FileUploadProperties prop, String fileName, SignatureRepository signatureRepository) {

        try {

            String inFilePath = prop.getXdocUploadDir() + fileName;
            String outFilePath = prop.getXdocUploadDir() + fileName + "_out.pdf";


            File inputFile = new File(inFilePath);

            InputStream in = new FileInputStream(inFilePath);

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            // 2) Create context Java model
            IContext context = report.createContext();

            Project project = getPorject();

            context.put("project", project);

            // 3) Generate report by merging Java model with the Docx
            OutputStream out = new FileOutputStream(new File(outFilePath));

//            report.process(context, out);
//            report.convert();
//            report.convert();
            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
//            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);

            report.convert(context, options, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }


    public static void createXdocToDocx(FileUploadProperties prop, String fileName, SignatureRepository signatureRepository) {

        try {
            String inFilePath = prop.getXdocUploadDir() + fileName;
            String outFilePath = prop.getXdocUploadDir() + fileName + "_out.docx";


            File inputFile = new File(inFilePath);

            InputStream in = new FileInputStream(inFilePath);

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            // 2) Create fields metadata to manage image
            FieldsMetadata metadata = new FieldsMetadata();
            metadata.addFieldAsImage("sign1");
            metadata.addFieldAsImage("sign2");
            report.setFieldsMetadata(metadata);

            // 2) Create context Java model
            IContext context = report.createContext();

            Project project = getPorject();

            context.put("project", project);

            Signature signature = GlobalUtil.getSignature(signatureRepository, SessionUtil.getUserId());

            String data = signature.getBase64signature().split(",")[1];

            byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);

//            BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(imageBytes));
//
//            ImageIO.write(bufImg, "jpg", new File(target));


            IImageProvider sign1 = new ByteArrayImageProvider(imageBytes);
            IImageProvider sign2 = new ByteArrayImageProvider(imageBytes);

//            IImageProvider sign1 = new ClassPathImageProvider(XdocUtil.class, signature.getBase64signature());

//            IImageProvider sign1 = new FileImageProvider(bufImg);

//            IImageProvider sign2 = new ClassPathImageProvider(XdocUtil.class, signature.getBase64signature());




            context.put("sign1", sign1);
            context.put("sign2", sign2);

            // 3) Generate report by merging Java model with the Docx
            OutputStream out = new FileOutputStream(new File(outFilePath));
            report.process(context, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }



    public static void testXdocToPdf(FileUploadProperties prop, String fileName, SignatureRepository signatureRepository) {

        try {

            String inFilePath = prop.getXdocUploadDir() + fileName;
            String outFilePath = prop.getXdocUploadDir() + fileName + "_out2.pdf";


            InputStream in = new FileInputStream(new File(inFilePath));
            XWPFDocument document = new XWPFDocument(in);
            PdfOptions options = PdfOptions.create();
            OutputStream out = new FileOutputStream(new File(outFilePath));
            PdfConverter.getInstance().convert(document, out, options);

            document.close();
            out.close();

//            File inputFile = new File(inFilePath);
//
//            InputStream in = new FileInputStream(inFilePath);
//
//            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
//
//            // 2) Create context Java model
//            IContext context = report.createContext();
//            Project project = new Project("Kang Seok Hwang");
//            project.setCourseTitle("Test Course");
//            project.setCertiNo("aaaaaa-01");
//            project.setCourseDate("04-MAR-2019");
//            project.setFirstUserName("lee");
//            project.setSecondUserName("hong");
//            project.setSopName("SOP-TM0002_RD08 v1.1");
//            project.setEffectiveDate("04-MAR-2019");
//
//            context.put("project", project);
//
//            // 3) Generate report by merging Java model with the Docx
//            OutputStream out = new FileOutputStream(new File(outFilePath));
//
//
//            PdfOptions pdfOptions = PdfOptions.create().fontEncoding("windows-1250");
//
//
////            report.process(context, out);
////            report.convert();
////            report.convert();
//            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
////            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
//
//            report.convert(context, options, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
