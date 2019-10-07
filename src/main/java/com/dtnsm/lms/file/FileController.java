package com.dtnsm.lms.file;

import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/base/file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    private PageInfo pageInfo = new PageInfo();

    public FileController() {
        pageInfo.setParentId("m-file");
        pageInfo.setParentTitle("파일업로드");
    }

    @GetMapping("/")
    public String controllerMain() {
        return "Hello~ File Upload Test.";
    }


    @GetMapping("/list")
    public String minorList(Model model) {

        pageInfo.setPageId("m-border-add");
        pageInfo.setPageTitle("파일업로드");
        model.addAttribute(pageInfo);

        Iterable<ElFile> fileList = fileService.getFileList();
        model.addAttribute("fileList", fileList);

        return "base/file/list";
    }


    @GetMapping("/add")
    public String add(Model model) {

        pageInfo.setPageId("m-border-add");
        pageInfo.setPageTitle("파일업로드");
        model.addAttribute(pageInfo);

        return "base/file/add";
    }

    // 싱글파일 업로드
    @PostMapping("/add-post")
    public String singleFileAddPost(@RequestParam("file") MultipartFile file) {

        ElFile uploadFile = fileService.storeFile(file);

        return "redirect:/base/file/list";
    }

    // 멀티파일 업로드
    @PostMapping("/multi-add-post")
    public String multiFilAddPost(@RequestParam("file") MultipartFile[] files) {

        Arrays.asList(files)
                .stream()
                .map(file -> fileService.storeFile(file))
                .collect(Collectors.toList());

        return "redirect:/base/notice/list";
    }

    @GetMapping("/downloadFile/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable int id, HttpServletRequest request) throws IOException {

        ElFile uploadFile = fileService.getUploadFile(id);

        String fileName = uploadFile.getSaveName();
        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(fileName);

        // Try to determine file's content type
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String contentType = mimeTypesMap.getContentType(fileName);
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, uploadFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }


}
