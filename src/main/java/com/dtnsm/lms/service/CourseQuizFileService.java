package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseQuiz;
import com.dtnsm.lms.domain.CourseQuizFile;
import com.dtnsm.lms.exception.FileDownloadException;
import com.dtnsm.lms.exception.FileUploadException;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CourseQuizFileRepository;
import com.dtnsm.lms.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class CourseQuizFileService {

    @Autowired
    CourseQuizFileRepository fileRepository;

    private final Path fileLocation;

    @Autowired
    public CourseQuizFileService(FileUploadProperties prop) {
        this.fileLocation = Paths.get(prop.getCourseQuizUploadDir())
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


    public CourseQuizFile storeFile(MultipartFile file, CourseQuiz courseQuiz) {
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

            Path targetLocation = this.fileLocation.resolve(saveName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            CourseQuizFile courseSectionFile = new CourseQuizFile(originName, saveName, file.getSize(), file.getContentType(), courseQuiz);

            return fileRepository.save(courseSectionFile);
        }catch(Exception e) {
            throw new FileUploadException("["+originName+"] 파일 업로드에 실패하였습니다. 다시 시도하십시오.",e);
        }
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

    public Iterable<CourseQuizFile> getFileList(){
        Iterable<CourseQuizFile> iterable = fileRepository.findAll();

        if(null == iterable) {
            throw new FileDownloadException("업로드 된 파일이 존재하지 않습니다.");
        }

        return  iterable;
    }

    public CourseQuizFile getUploadFile(long id) {
        CourseQuizFile courseQuizFile = fileRepository.findById(id).get();

        if(null == courseQuizFile) {
            throw new FileDownloadException("해당 아이디["+id+"]로 업로드 된 파일이 존재하지 않습니다.");
        }
        return courseQuizFile;
    }

    @Transactional
    public void deleteFile(long id) {

        CourseQuizFile courseQuizFile = getUploadFile(id);

        deleteFile(courseQuizFile);

        fileRepository.delete(courseQuizFile);
    }

    public void deleteFile(CourseQuizFile uploadFile) {

        Resource resource = loadFileAsResource(uploadFile.getSaveName());

        try {
            resource.getFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
