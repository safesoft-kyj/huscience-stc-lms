package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.CourseSection;
import com.dtnsm.lms.domain.CourseSectionFile;
import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.domain.ScheduleFile;
import com.dtnsm.lms.exception.FileDownloadException;
import com.dtnsm.lms.exception.FileUploadException;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CourseSectionFileRepository;
import com.dtnsm.lms.repository.ScheduleFileRepository;
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
public class ScheduleFileService {

    @Autowired
    ScheduleFileRepository fileRepository;

    private final Path fileLocation;

    @Autowired
    public ScheduleFileService(FileUploadProperties prop) {
        this.fileLocation = Paths.get(prop.getScheduleUploadDir())
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


    public ScheduleFile storeFile(MultipartFile file, Schedule schedule) {
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

            ScheduleFile scheduleFile = new ScheduleFile(originName, saveName, fileSize, contentType, schedule);

            return fileRepository.save(scheduleFile);
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

    public Iterable<ScheduleFile> getFileList(){
        Iterable<ScheduleFile> iterable = fileRepository.findAll();

        if(null == iterable) {
            throw new FileDownloadException("업로드 된 파일이 존재하지 않습니다.");
        }

        return  iterable;
    }

    public ScheduleFile getUploadFile(long id) {
        ScheduleFile scheduleFile = fileRepository.findById(id).get();

        if(null == scheduleFile) {
            throw new FileDownloadException("해당 아이디["+id+"]로 업로드 된 파일이 존재하지 않습니다.");
        }
        return scheduleFile;
    }

    @Transactional
    public void deleteFile(long id) {

        ScheduleFile scheduleFile = getUploadFile(id);

        deleteFile(scheduleFile);

        fileRepository.delete(scheduleFile);
    }

    public void deleteFile(ScheduleFile scheduleFile) {

        Resource resource = loadFileAsResource(scheduleFile.getSaveName());

        try {
            resource.getFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
