package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/document")
public class DocumentAdminController {

    @Autowired
    MailService mailService;

    @Autowired
    DocumentTemplateService templateService;

    @Autowired
    DocumentService documentService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private DocumentFileService documentFileService;

    private PageInfo pageInfo = new PageInfo();

    private BorderMaster borderMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public DocumentAdminController() {
        pageInfo.setParentId("m-document");
        pageInfo.setParentTitle("전자결재");
    }

    // region # 공지사항
    @GetMapping("/list")
    public String listPage(@RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @PageableDefault Pageable pageable
            , Model model) {

        pageInfo.setPageTitle("전자결재 조회");

        Page<Document> documents;

        if(searchType.equals("all") && searchText.equals("")) {
            documents = documentService.getPageList(pageable);
        } else if (searchType.equals("all") && !searchText.equals("")) {
            documents = documentService.getPageListByTitleLikeOrContentLike(searchText, searchText, pageable);
        } else if (searchType.equals("subject")) {
            documents = documentService.getPageListByTitleLike(searchText, pageable);
        } else {
            documents = documentService.getPageListByContentLike(searchText, pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documents);

        return "admin/document/list";
    }


    @GetMapping("/view/{id}")
    public String noticeView(@PathVariable("id") long id, Model model) {

        Document oldBorder = documentService.getById(id);
        oldBorder.setViewCnt(oldBorder.getViewCnt() + 1);

        Document border= documentService.save(oldBorder);

        pageInfo.setPageTitle("전자결재 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);

        return "admin/document/view";
    }

    @GetMapping("/add/{templateId}")
    public String noticeAdd(@PathVariable("templateId") int templateId, Model model) {

        DocumentTemplate template = templateService.getById(templateId);

        Document border = new Document();
        border.setTemplate(template);

        pageInfo.setPageTitle(template.getTitle() + " 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);
        model.addAttribute("mailList", userService.getAccountList());

        return "admin/document/add";
    }

    @PostMapping("/add-post")
    @Transactional
    public String noticeAddPost(@Valid Document document
            , @RequestParam("files") MultipartFile[] files
           , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/document/add";
        }

        DocumentTemplate template = templateService.getById(document.getTemplate().getId());

        document.setAccount(userService.getAccountByUserId(SessionUtil.getUserId()));
        document.setTemplate(template);
        Document document1 = documentService.save(document);

        Arrays.asList(files)
                .stream()
                .map(file -> documentFileService.storeFile(file, document1))
                .collect(Collectors.toList());

        return "redirect:/admin/document/list";
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        Document document = documentService.getById(id);

        pageInfo.setPageTitle(document.getTitle() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("border", document);
        model.addAttribute("id", document.getId());
        model.addAttribute("mailList", userService.getAccountList());

        return "admin/document/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid Document document
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            document.setId(id);
            return "admin/document/list";
        }

        List<DocumentFile> documentFiles = documentService.getById(id).getDocumentFiles();
        document.setDocumentFiles(documentFiles);

        Document document1 = documentService.save(document);

        Arrays.asList(files)
                .stream()
                .map(file -> documentFileService.storeFile(file, document1))
                .collect(Collectors.toList());

        return "redirect:/admin/document/list";
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Document document = documentService.getById(id);

        documentService.delete(document);

        return "redirect:/admin/document/list";
    }


    @GetMapping("/delete-file/{id}/{file_id}")
    public String noticeDeleteFile(@PathVariable int id, @PathVariable int file_id, HttpServletRequest request){

        // db및 파일 삭제
        documentFileService.deleteFile(file_id);

        return "redirect:/document/edit/" + id;

    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable int id, HttpServletRequest request){

        DocumentFile documentFile = documentFileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = documentFileService.loadFileAsResource(documentFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(documentFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, documentFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }

    // endregion

}
