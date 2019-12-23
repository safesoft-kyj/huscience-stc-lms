package com.dtnsm.lms.controller;

import com.dtnsm.lms.component.ExcelReader;
import com.dtnsm.lms.domain.CertificateFile;
import com.dtnsm.lms.properties.ImageUploadProperties;
import com.dtnsm.lms.service.CertificateFileService;
import com.dtnsm.lms.service.MunjeBankService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mypage/certificate/file")
public class CertificateFileController {

    @Autowired
    ImageUploadProperties prop;

    @Autowired
    ExcelReader excelReader;

    @Autowired
    MunjeBankService munjeBankService;

    private static final Logger logger = LoggerFactory.getLogger(CertificateFileController.class);

    @Autowired
    private CertificateFileService fileService;

    private PageInfo pageInfo = new PageInfo();

    public CertificateFileController() {
        pageInfo.setParentId("m-file");
        pageInfo.setParentTitle("마이페이지");
    }

    @GetMapping("/")
    public String controllerMain() {
        return "수료증 업로드";
    }


    @GetMapping("/list")
    public String minorList(Model model) {

        pageInfo.setPageId("m-border-add");
        pageInfo.setPageTitle("수료증 업로드");
        model.addAttribute(pageInfo);

        List<CertificateFile> fileList = fileService.getAllByUserId(SessionUtil.getUserId());
        model.addAttribute("fileList", fileList);

        return "content/mypage/certificate/file/list";
    }


    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageId("m-border-add");
        pageInfo.setPageTitle("수료증 업로드");
        model.addAttribute(pageInfo);

        return "content/mypage/certificate/file/add";
    }

    // 싱글파일 업로드
    @PostMapping("/add-post")
    public String singleFileAddPost(@RequestParam("file") MultipartFile file) {

        CertificateFile uploadFile = fileService.storeFile(file);

//        try {
//            List<MunjeBank> munjeBanks = excelReader.readFileToList(file, MunjeBank::fromSurvey);
//
//            for(MunjeBank munjeBank : munjeBanks) {
//                munjeBankService.save(munjeBank);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return "redirect:/mypage/certificate/file/list";
    }

    // 멀티파일 업로드
    @PostMapping("/multi-add-post")
    public String multiFilAddPost(@RequestParam("file") MultipartFile[] files) {

        Arrays.asList(files)
                .stream()
                .map(file -> fileService.storeFile(file))
                .collect(Collectors.toList());

        return "redirect:/mypage/certificate/file/list";
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") int id) {

        //  테이블및 파일 삭제
        fileService.deleteFile(id);

        return "redirect:/mypage/certificate/file/list";
    }

    @GetMapping("/downloadFile/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable int id, HttpServletRequest request) throws IOException {

        CertificateFile uploadFile = fileService.getUploadFile(id);

        String fileName = uploadFile.getSaveName();
        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(fileName);

        // Try to determine file's content type
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
//        String contentType = mimeTypesMap.getContentType(fileName);
        String contentType = uploadFile.getMimeType();
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, uploadFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + newFileName + "\"")
                .body(resource);
    }
}
