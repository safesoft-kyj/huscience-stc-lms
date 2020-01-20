package com.dtnsm.lms.util;

import com.groupdocs.assembly.DataSourceInfo;
import com.groupdocs.assembly.DocumentAssembler;
import com.groupdocs.assembly.LoadSaveOptions;
import com.groupdocs.conversion.config.ConversionConfig;
import com.groupdocs.conversion.handler.ConversionHandler;
import com.groupdocs.conversion.handler.ConvertedDocument;
import com.groupdocs.conversion.options.load.LoadOptions;
import com.groupdocs.conversion.options.load.WordsLoadOptions;
import com.groupdocs.conversion.options.save.MarkupSaveOptions;
import com.groupdocs.conversion.options.save.PdfSaveOptions;
import com.groupdocs.conversion.options.save.SaveOptions;
import com.groupdocs.conversion.options.save.WatermarkOptions;
import com.groupdocs.conversion.utils.wrapper.stream.GroupDocsOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

@Component
@Slf4j
public class DocumentConverter {
    private ConversionConfig config = new ConversionConfig();
    private ConversionHandler conversionHandler;

    @Value("${groupdocs.license}")
    private String licensePath;

    private WordsLoadOptions wordsLoadOptions;

    @PostConstruct
    public void init() {
        try {
//            FileInputStream licenseFileInputStream = new FileInputStream(new File(license));
            log.info("@License Loaded. {}", licensePath);
            com.groupdocs.conversion.License conversionLicense = new com.groupdocs.conversion.License();
            conversionLicense.setLicense(licensePath);
            log.info("@Apply Conversion License.");
            com.groupdocs.assembly.License assemblyLicense = new com.groupdocs.assembly.License();
            assemblyLicense.setLicense(licensePath);
            log.info("@Apply Assembly License.");

            wordsLoadOptions = new WordsLoadOptions();
            wordsLoadOptions.setHideComments(true);
            wordsLoadOptions.setHideWordTrackedChanges(true);
        } catch (Exception error) {
            log.error("Error : {}", error.toString());
        }

        conversionHandler = new ConversionHandler(config);
    }


    public void toHTML(InputStream is, OutputStream os) {
        try {
            SaveOptions saveOption = new MarkupSaveOptions();
            ConvertedDocument convertedDocumentPath = conversionHandler.convert(is, saveOption);
            System.out.print("Converted file path is: " + convertedDocumentPath.getFileType());
            GroupDocsOutputStream outputStream = new GroupDocsOutputStream(os);
            convertedDocumentPath.save(outputStream);
        } catch (Exception error) {
            log.error("error : {}", error);
        } finally {
            if(os != null) {
                try {
                    os.flush();
                    os.close();
                } catch(Exception e) {}
            }
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e){}
            }
        }
    }
    public String word2html(String source) {
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            SaveOptions saveOption = new MarkupSaveOptions();
            ConvertedDocument convertedDocumentPath = conversionHandler.convert(source, wordsLoadOptions, saveOption);
            System.out.print("Converted file path is: " + convertedDocumentPath.getFileType());
            GroupDocsOutputStream outputStream = new GroupDocsOutputStream(os);
            convertedDocumentPath.save(outputStream);

            return os.toString("utf-8");

        } catch (Exception error) {
            log.error("error : {}", error);
            return "";
        }
    }



    public void word2pdf(InputStream is, OutputStream os) {
        try {
            SaveOptions saveOption = new PdfSaveOptions();
//            WatermarkOptions watermarkOptions = new WatermarkOptions();
//            watermarkOptions.setText("Dt&SanoMedics");
//            saveOption.setWatermarkOptions(watermarkOptions);
            ConvertedDocument convertedDocumentPath = conversionHandler.convert(is, wordsLoadOptions, saveOption);
            System.out.print("Converted file path is: " + convertedDocumentPath.getFileType());
            GroupDocsOutputStream outputStream = new GroupDocsOutputStream(os);
            convertedDocumentPath.save(outputStream);
        } catch (Exception error) {
            log.error("error : {}", error);
        } finally {
            if(os != null) {
                try {
                    os.flush();
                    os.close();
                } catch(Exception e) {}
            }
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e){}
            }
        }
    }

    public boolean assembleDocument(InputStream is, OutputStream os, DataSourceInfo ... dataSourceInfo) {
        try {
            DocumentAssembler assembler = new DocumentAssembler();
            return assembler.assembleDocument(is, os, dataSourceInfo);
        } catch (Exception error) {
            log.error("Error : {}", error);
            return false;
        }
    }
}
