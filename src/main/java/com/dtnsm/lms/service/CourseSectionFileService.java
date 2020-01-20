package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseSection;
import com.dtnsm.lms.domain.CourseSectionFile;
import com.dtnsm.lms.exception.FileDownloadException;
import com.dtnsm.lms.exception.FileUploadException;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CourseSectionFileRepository;
import com.dtnsm.lms.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseSectionFileService {

    @Autowired
    CourseSectionFileRepository fileRepository;

    private final Path fileLocation;

    @Autowired
    public CourseSectionFileService(FileUploadProperties prop) {
        this.fileLocation = Paths.get(prop.getCourseSectionUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        }catch(Exception e) {
            throw new FileUploadException("파일을 업로드할 디렉토리를 생성하지 못했습니다.", e);
        }
    }


      /*
        Section File Service
     */


    public CourseSectionFile storeFile(MultipartFile file, CourseSection courseSection) {
        String originName = FilenameUtils.getBaseName(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        originName = originName + "." + extension;
        if (originName.equals("") || originName.equals(".")) return null;

        // 파일명을 생성한다.
        String saveName = StringUtils.cleanPath(FileUtil.CreateFileName(originName));

        try {
            // 파일명에 부적합 문자가 있는지 확인한다.
            if(originName.contains(".."))
                throw new FileUploadException("파일명에 부적합 문자가 포함되어 있습니다. " + originName);


            Path imageCreateLocation = this.fileLocation.resolve(String.valueOf(courseSection.getId()));
            Path targetLocation = this.fileLocation.resolve(saveName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Image 저장경로 설정
//            Path imageSavePath = this.fileLocation.resolve(saveName);
            // 이미지로 저장
            String imageFolder = targetLocation.toString().replaceAll(".pdf", "");
            int imageSize = conversionPdf2Img(file.getInputStream(), saveName, imageFolder);

            CourseSectionFile courseSectionFile = new CourseSectionFile(originName, saveName, fileSize, contentType, saveName.replaceAll(".pdf", ""), imageSize, courseSection);


            return fileRepository.save(courseSectionFile);
        }catch(Exception e) {
            throw new FileUploadException("["+originName+"] 파일 업로드에 실패하였습니다. 다시 시도하십시오.",e);
        }
    }

    public int conversionPdf2Img(InputStream is, String uniqueId, String uploadDir) {

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
                fileName = (i+1) + ".jpg".toString();
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
        return savedImgList.size();
    }


    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileLocation.resolve(fileName).normalize();
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


    public Iterable<CourseSectionFile> getFileList(){
        Iterable<CourseSectionFile> iterable = fileRepository.findAll();

        if(null == iterable) {
            throw new FileDownloadException("업로드 된 파일이 존재하지 않습니다.");
        }

        return  iterable;
    }

    public CourseSectionFile getUploadFile(long id) {
        CourseSectionFile courseSectionFile = fileRepository.findById(id).get();

        if(null == courseSectionFile) {
            throw new FileDownloadException("해당 아이디["+id+"]로 업로드 된 파일이 존재하지 않습니다.");
        }
        return courseSectionFile;
    }

    @Transactional
    public void deleteFile(long id) {

        CourseSectionFile courseSectionFile = getUploadFile(id);

        deleteFile(courseSectionFile);

        fileRepository.delete(courseSectionFile);
    }

    public void deleteFile(CourseSectionFile uploadFile) {

        Resource resource = loadFileAsResource(uploadFile.getSaveName());

        try {
            resource.getFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
