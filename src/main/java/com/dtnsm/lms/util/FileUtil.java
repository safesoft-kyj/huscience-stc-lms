package com.dtnsm.lms.util;

import com.dtnsm.lms.exception.FileDownloadException;
import com.dtnsm.lms.properties.FileUploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public  class FileUtil {

    @Autowired
    public static FileUploadProperties prop;

    // 한글파일명 깨짐 현상 해소
    public static String getNewFileName(HttpServletRequest request, String fileName) {
        String header = request.getHeader("User-Agent");

        String docName = "";

        try {
            if (header.contains("MSIE") || header.contains("Trident")) {
                docName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
            } else if (header.contains("Firefox")) {
                docName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            } else if (header.contains("Opera")) {
                docName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            } else if (header.contains("Chrome")) {
                docName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return docName;
    }

    public static Resource loadFileAsResource(String uploadDir, String fileName) {

        Path fileLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Path filePath = fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) {
                return resource;
            }else {
                throw new FileDownloadException(fileName + " 파일을 찾을 수 없습니다.");
            }
        }catch(MalformedURLException e) {
            throw new FileDownloadException(fileName + " 파일을 찾을 수 없습니다.", e);
        }
    }

    public static String CreateFileName(String originalName){

        //uuid 생성
        UUID uuid = UUID.randomUUID();
        // 랜던생성 + 파일이름 저장
        String  saveName = uuid.toString() + "_" + originalName;

        return saveName;
    }

    public static String CreateFileName(String originalName, String contentType){

        //uuid 생성
        UUID uuid = UUID.randomUUID();
        // 랜던생성 + 파일이름 저장
        //String  saveName = uuid.toString() + "_" + originalName;
        String saveName = uuid.toString() + "." + contentType;

        return saveName;
    }

}
