package com.dtnsm.lms.util;

import com.dtnsm.lms.domain.Project;
import com.dtnsm.lms.exception.FileUploadException;
import com.dtnsm.lms.properties.FileUploadProperties;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public  class XdocUtil {
    //
    public static void createXdocToPdf(FileUploadProperties prop, String fileName) {

        try {

            String inFilePath = prop.getXdocUploadDir() + fileName;
            String outFilePath = prop.getXdocUploadDir() + fileName + "_out.pdf";


            File inputFile = new File(inFilePath);

            InputStream in = new FileInputStream(inFilePath);

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            // 2) Create context Java model
            IContext context = report.createContext();
            Project project = new Project("XDocReport");
            context.put("project", project);

            // 3) Generate report by merging Java model with the Docx
            OutputStream out = new FileOutputStream(new File(outFilePath));
//            report.process(context, out);
//            report.convert();
//            report.convert();
            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
            report.convert(context, options, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }


    public static void createXdocToDocx(FileUploadProperties prop, String fileName) {

        try {

            String inFilePath = prop.getXdocUploadDir() + fileName;
            String outFilePath = prop.getXdocUploadDir() + fileName + "_out.docx";


            File inputFile = new File(inFilePath);

            InputStream in = new FileInputStream(inFilePath);

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            // 2) Create context Java model
            IContext context = report.createContext();
            Project project = new Project("XDocReport");
            context.put("project", project);

            // 3) Generate report by merging Java model with the Docx
            OutputStream out = new FileOutputStream(new File(outFilePath));
            report.process(context, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }

}
