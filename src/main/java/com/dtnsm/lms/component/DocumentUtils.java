package com.dtnsm.lms.component;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

public class DocumentUtils {
    public static void main(String[] args) throws Exception {
//        FileWriter writer = new FileWriter(new File("D:\\group-docs-test-3.html"));
//        writer.write(DocumentUtils.convertHTML("D:\\test.docx"));
//        writer.flush();
//        writer.close();
        DocumentUtils.convert("D:\\matrix.xlsx", new File("D:\\matrix.pdf"), "pdf");
        DocumentUtils.convert("D:\\test.docx", new File("D:\\matrix.html"), "html");
    }
    public static void convert(String source, File destination, String output) throws Exception {
        URL url = new URL("http://localhost:8887/conversion?source=" + URLEncoder.encode(source, "utf-8")+"&output=" + output);
        FileUtils.copyURLToFile(url, destination);
    }

    public static void convert2(String fileName) throws Exception {
    }

    public static String convertHTML(String file) {
        try {
//            com.groupdocs.viewer.licensing.License lic = new com.groupdocs.viewer.licensing.License();
//            System.out.println(lic);
//            lic.setLicense(new FileInputStream(new File("C:\\dev\\GroupDocs.Total.Java.lic")));
//            System.out.println("@setLicense");
//            ViewerConfig config = new ViewerConfig();
////            ViewerImageHandler handler = new ViewerImageHandler(config);
//            config.setPageNamePrefix("dtnsm-viewer");
//            ViewerHtmlHandler handler = new ViewerHtmlHandler(config);
//            List<PageHtml> pages = handler.getPages(file);
//            StringBuilder html = new StringBuilder();
//            for (PageHtml page : pages) {
//            System.out.println("Page number: " + page.getPageNumber());
////            System.out.println("Resources count: " + page.getHtmlResources().size());
////            System.out.println("Html content: " + page.getHtmlContent());
//
//                // Html resources descriptions
////                for (HtmlResource resource : page.getHtmlResources()) {
////                    System.out.println(resource.getResourceName() + resource.getResourceType());
////
////                    // Get html page resource stream
////                    InputStream resourceStream = htmlHandler.getResource(guid, resource);
////                    System.out.println("Stream size: " + resourceStream.available());
////                    System.out.println("Content : " + resourceStream.toString());
////                }
////                byte[] b = page.getStream().readAllBytes();
////                String encoded = Base64.getEncoder().encodeToString(b);
////                html.append("<img width=\"786\" src=\"data:image/png;base64,").append(Base64.getEncoder().encodeToString(b)).append("\">");
//                html.append(page.getHtmlContent());
//            }

//            return html.toString();
            return "";
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();

            return e.getMessage();
        }
    }

    public static String convertHTML(InputStream is) {
        try {
//            ViewerConfig config = new ViewerConfig();
//            ViewerHtmlHandler htmlHandler = new ViewerHtmlHandler(config);
//
//            List<PageHtml> pages = htmlHandler.getPages(is);
//            StringBuilder html = new StringBuilder();
//            for (PageHtml page : pages) {
//
//                html.append(page.getHtmlContent());
//            }
//
//            return html.toString();
            return "";
        } catch (Exception e) {
            System.out.println("Exception#is: " + e.getMessage());
            e.printStackTrace();

            return e.getMessage();
        }
    }
}
