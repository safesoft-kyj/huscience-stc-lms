package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.CertificateFile;
import com.dtnsm.lms.exception.FileDownloadException;
import com.dtnsm.lms.exception.FileUploadException;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CertificateFileRepository;
import com.dtnsm.lms.util.SessionUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.List;
import java.util.UUID;

@Service
public class CertificateFileService {

    @Autowired
    CertificateFileRepository fileRepository;

    @Autowired
    UserServiceImpl userService;

    private final Path fileLocation;

    @Autowired
    public CertificateFileService(FileUploadProperties prop) {
        this.fileLocation = Paths.get(prop.getCertificateUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        }catch(Exception e) {
            throw new FileUploadException("파일을 업로드할 디렉토리를 생성하지 못했습니다.", e);
        }
    }


    public List<CertificateFile> getAllByUserId(String userId) {
        return fileRepository.findAllByAccount_UserId(userId);
    }

    public Page<CertificateFile> getAllByUserId(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return fileRepository.findAllByAccount_UserId(userId, pageable);
    }

    public CertificateFile storeFile(MultipartFile file) {
        String originName = FilenameUtils.getBaseName(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        originName = originName + "." + extension;
        if (originName.equals("") || originName.equals(".")) return null;

        // 파일명을 생성한다.
        String saveName = StringUtils.cleanPath(CreateFileName(extension));

        try {
            // 파일명에 부적합 문자가 있는지 확인한다.
            if(originName.contains(".."))
                throw new FileUploadException("파일명에 부적합 문자가 포함되어 있습니다. " + originName);

            // Path targetLocation = this.fileLocation.resolve(originName);
            Path targetLocation = this.fileLocation.resolve(saveName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 사용자 등록
            Account account =  userService.getAccountByUserId(SessionUtil.getUserId());

            CertificateFile uploadFile = new CertificateFile(originName, saveName, file.getSize(), file.getContentType(), account);

            fileRepository.save(uploadFile);

            return uploadFile;
        }catch(Exception e) {
            throw new FileUploadException("["+originName+"] 파일 업로드에 실패하였습니다. 다시 시도하십시오.",e);
        }
    }


    // 수료증 생성
    public CertificateFile storeFile(MultipartFile file, String fileName) {
        String originName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        originName = originName + "." + extension;
        if (originName.equals("") || originName.equals(".")) return null;

        // 파일명을 생성한다.
        String saveName = StringUtils.cleanPath(CreateFileName(extension));

        try {
            // 파일명에 부적합 문자가 있는지 확인한다.
            if(originName.contains(".."))
                throw new FileUploadException("파일명에 부적합 문자가 포함되어 있습니다. " + originName);

            // Path targetLocation = this.fileLocation.resolve(originName);
            Path targetLocation = this.fileLocation.resolve(saveName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 사용자 등록
            Account account =  userService.getAccountByUserId(SessionUtil.getUserId());

            CertificateFile uploadFile = new CertificateFile(originName, saveName, file.getSize(), file.getContentType(), account);

            fileRepository.save(uploadFile);

            return uploadFile;
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

    public List<CertificateFile> getFileList(){
        List<CertificateFile> iterable = fileRepository.findAll();

        if(null == iterable) {
            throw new FileDownloadException("업로드 된 파일이 존재하지 않습니다.");
        }

        return  iterable;
    }

    public CertificateFile getUploadFile(int id) {
        CertificateFile uploadFile = fileRepository.findById(id).get();

        if(null == uploadFile) {
            throw new FileDownloadException("해당 아이디["+id+"]로 업로드 된 파일이 존재하지 않습니다.");
        }
        return uploadFile;
    }

    @Transactional
    public void deleteFile(int id) {

        CertificateFile uploadFile = getUploadFile(id);

        deleteFile(uploadFile);

        fileRepository.delete(uploadFile);
    }

    private void deleteFile(CertificateFile uploadFile) {

        Resource resource = loadFileAsResource(uploadFile.getSaveName());

        try {
            resource.getFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String CreateFileName(String extension){
        //uuid 생성
        UUID uuid = UUID.randomUUID();
        // 랜던생성 + 파일이름 저장
        String  saveName = SessionUtil.getUserId() + "_" + uuid.toString() + "." + extension;

        return saveName;
    }
}
