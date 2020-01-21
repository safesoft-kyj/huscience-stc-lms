package com.dtnsm.lms.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageUtil {
    public static List<String> conversionPdf2Img(InputStream is, String uniqueId, String uploadDir) {

        List<String> savedImgList = new ArrayList<>(); //저장된 이미지 경로를 저장하는 List 객체
        try {
            PDDocument pdfDoc = PDDocument.load(is); //Document 생성
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);

            String resultImgPath = uploadDir; //이미지가 저장될 경로
            Files.createDirectories(Paths.get(resultImgPath)); //PDF 2 Img에서는 경로가 없는 경우 이미지 파일이 생성이 안되기 때문에 디렉토리를 만들어준다.

            String fileName = "";

            //순회하며 이미지로 변환 처리
            for (int i=0; i<pdfDoc.getPages().getCount(); i++) {
//                String imgFileName = resultImgPath + "/" + i + ".png";
                fileName = i + ".jpg".toString();
                String imgFileName = Paths.get(resultImgPath, fileName).toString();

                //DPI 설정
                BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB);

                // 이미지로 만든다.
                ImageIOUtil.writeImage(bim, imgFileName , 300);

                //저장 완료된 이미지를 list에 추가한다.
                savedImgList.add(fileName);
            }
            pdfDoc.close(); //모두 사용한 PDF 문서는 닫는다.
        }
        catch (Exception e) {
            e.getMessage();
        }
        return savedImgList;
    }
}
