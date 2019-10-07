package com.dtnsm.lms.course.service;

import com.dtnsm.lms.border.domain.BorderFile;
import com.dtnsm.lms.course.domain.Course;
import com.dtnsm.lms.course.domain.CourseFile;
import com.dtnsm.lms.course.domain.CourseSection;
import com.dtnsm.lms.course.repository.CourseFileRepository;
import com.dtnsm.lms.course.repository.CourseSectionRepository;
import com.dtnsm.lms.file.FileDownloadException;
import com.dtnsm.lms.file.FileUploadException;
import com.dtnsm.lms.file.FileUploadProperties;
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
public class CourseFileService {

    @Autowired
    CourseFileRepository fileRepository;

    private final Path fileLocation;

    @Autowired
    public CourseFileService(FileUploadProperties prop) {
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


    public CourseFile storeFile(MultipartFile file, Course course) {
        String originName = StringUtils.cleanPath(file.getOriginalFilename());

        if (originName.equals("")) return null;

        // 파일명을 생성한다.
        String saveName = StringUtils.cleanPath(CreateFileName(file.getOriginalFilename()));

        try {
            // 파일명에 부적합 문자가 있는지 확인한다.
            if(originName.contains(".."))
                throw new FileUploadException("파일명에 부적합 문자가 포함되어 있습니다. " + originName);

            Path targetLocation = this.fileLocation.resolve(saveName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            CourseFile courseFile = new CourseFile(originName, saveName, file.getSize(), file.getContentType(), course);

//            courseFile.setFileName(originName);
//            courseFile.setSaveName(saveName);
//            courseFile.setSize(file.getSize());
//            courseFile.setMimeType(file.getContentType());

            return fileRepository.save(courseFile);
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

    public Iterable<CourseFile> getFileList(){
        Iterable<CourseFile> iterable = fileRepository.findAll();

        if(null == iterable) {
            throw new FileDownloadException("업로드 된 파일이 존재하지 않습니다.");
        }

        return  iterable;
    }

    public CourseFile getUploadFile(long id) {
        CourseFile courseFile = fileRepository.findById(id).get();

        if(null == courseFile) {
            throw new FileDownloadException("해당 아이디["+id+"]로 업로드 된 파일이 존재하지 않습니다.");
        }
        return courseFile;
    }

    @Transactional
    public void deleteFile(long id) {

        CourseFile courseFile = getUploadFile(id);

        deleteFile(courseFile);

        fileRepository.delete(courseFile);
    }

    public void deleteFile(CourseFile uploadFile) {

        Resource resource = loadFileAsResource(uploadFile.getSaveName());

        try {
            resource.getFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String CreateFileName(String originalName){

        //uuid 생성
        UUID uuid = UUID.randomUUID();
        // 랜던생성 + 파일이름 저장
        String  saveName = uuid.toString() + "_" + originalName;

        return saveName;
    }


}
