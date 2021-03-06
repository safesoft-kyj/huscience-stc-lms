package com.dtnsm.lms.service;

import com.dtnsm.common.entity.JobDescriptionVersion;
import com.dtnsm.common.entity.JobDescriptionVersionFile;
import com.dtnsm.lms.exception.FileDownloadException;
import com.dtnsm.lms.exception.FileUploadException;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.JobDescriptionFileRepository;
import com.dtnsm.lms.util.DocumentConverter;
import com.dtnsm.lms.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class JobDescriptionFileService {

    @Autowired
    JobDescriptionFileRepository borderFileRepository;

    @Autowired
    private DocumentConverter documentConverter;


    private final Path fileLocation;

    @Autowired
    public JobDescriptionFileService(FileUploadProperties prop) {
        this.fileLocation = Paths.get(prop.getBinderJdUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        }catch(Exception e) {
            throw new FileUploadException("파일을 업로드할 디렉토리를 생성하지 못했습니다.", e);
        }
    }

    public JobDescriptionVersionFile storeFile(MultipartFile file, JobDescriptionVersion jobDescriptionVersion) {
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

            JobDescriptionVersionFile borderFile = new JobDescriptionVersionFile(originName, saveName, file.getSize(), file.getContentType(), jobDescriptionVersion);

            /* 사용자에게 Job Description 을 보여주기 위해서 PDF 파일로 변환한다.
            *  해당 PDF파일을 사용자에게는 HTML로 변환하여 출력
            */
            new Thread(() -> {
                try {
                    //TODO JD 변환 추가
                    WordprocessingMLPackage wordMLPackage = Docx4J.load(file.getInputStream());
//                    String outputPDF = fileLocation + "/" + file.getOriginalFilename().substring(0, saveName.lastIndexOf(".")) + ".pdf";
//                    OutputStream os = new FileOutputStream(outputPDF);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    Docx4J.save(wordMLPackage, os);
//                    os.flush();
//                    PdfDocument pdf = new PdfDocument();
//                    pdf.loadFromBytes(os.toByteArray());
//                    borderFile.setPageCount(pdf.getPages().getCount());
//
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    pdf.saveToStream(stream, FileFormat.HTML);

                    documentConverter.toHTML(new ByteArrayInputStream(os.toByteArray()), stream);

                    borderFile.setHtmlContent(stream.toString("utf-8"));

                } catch (Exception error) {System.err.println(error);}
            }).run();

            borderFileRepository.save(borderFile);

            return borderFile;
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

    public Iterable<JobDescriptionVersionFile> getFileList(){
        Iterable<JobDescriptionVersionFile> iterable = borderFileRepository.findAll();

        if(null == iterable) {
            throw new FileDownloadException("업로드 된 파일이 존재하지 않습니다.");
        }

        return  iterable;
    }

    public JobDescriptionVersionFile getUploadFile(int id) {
        JobDescriptionVersionFile borderFile = borderFileRepository.findById(id).get();

        if(null == borderFile) {
            throw new FileDownloadException("해당 아이디["+id+"]로 업로드 된 파일이 존재하지 않습니다.");
        }
        return borderFile;
    }

    @Transactional
    public void deleteFile(int id) {

        JobDescriptionVersionFile borderFile = getUploadFile(id);

        deleteFile(borderFile);

        borderFileRepository.delete(borderFile);
    }

    public void deleteFile(JobDescriptionVersionFile uploadFile) {

        Resource resource = loadFileAsResource(uploadFile.getSaveName());

        try {
            resource.getFile().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
