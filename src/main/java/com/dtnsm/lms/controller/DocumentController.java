package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
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
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    MailService mailService;

    @Autowired
    DocumentTemplateService templateService;

    @Autowired
    private DocumentAccountService documentAccountService;

    @Autowired
    DocumentService documentService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private CourseManagerService courseManagerService;

    @Autowired
    private DocumentFileService documentFileService;

    private PageInfo pageInfo = new PageInfo();

    private BorderMaster borderMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public DocumentController() {
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

        String userId = SessionUtil.getUserId();

        Page<Document> documents;

        if(searchType.equals("all") && searchText.equals("")) {
            documents = documentService.getPageListByUserId(userId, pageable);
        } else if (searchType.equals("all") && !searchText.equals("")) {
            documents = documentService.getPageListByAccount_UserIdAndTitleLikeOrContentLike(userId, searchText, searchText, pageable);
        } else if (searchType.equals("subject")) {
            documents = documentService.getPageListByUserIdAndTitleLike(userId, searchText, pageable);
        } else {
            documents = documentService.getPageListByUserIdAndContentLike(userId, searchText, pageable);
        }

        model.addAttribute(pageInfo);
        model.addAttribute("borders", documents);

        return "content/document/list";
    }


    // 기안양식
    @GetMapping("/templateView")
    public String noticeView(Model model) {

        pageInfo.setPageTitle("기안작성");

        model.addAttribute(pageInfo);
        model.addAttribute("templateList", templateService.getList());

        return "content/document/templateView";
    }

    @GetMapping("/view/{id}")
    public String noticeView(@PathVariable("id") long id, Model model) {

        Document oldBorder = documentService.getById(id);
        oldBorder.setViewCnt(oldBorder.getViewCnt() + 1);

        Document border= documentService.save(oldBorder);

        pageInfo.setPageTitle("전자결재 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);

        return "content/document/view";
    }

    @GetMapping("/add/{templateId}")
    public String noticeAdd(@PathVariable("templateId") int templateId, Model model) {

        DocumentTemplate template = templateService.getById(templateId);

        Document border = new Document();
        border.setTemplate(template);
        border.setTitle("[" + template.getTitle() + "]");
        border.setContent(template.getContent());


        pageInfo.setPageTitle(template.getTitle() + " 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);
        model.addAttribute("mailList", userService.getAccountList());

        return "content/document/add";
    }

    @PostMapping("/add-post")
    @Transactional
    public String noticeAddPost(@Valid Document document
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            return "document/add/" + document.getTemplate().getId();
        }

        boolean isMail = mails[0].equals("0") ? false : true;

        if (isMail) {
            document.setMailSender(Arrays.toString(mails));
        }

        DocumentTemplate template = templateService.getById(document.getTemplate().getId());

        document.setAccount(userService.getAccountByUserId(SessionUtil.getUserId()));
        document.setTemplate(template);
        Document document1 = documentService.save(document);

//        documentFileService.storeFile(files, document1);

        if (files.length > 0) {
            Arrays.asList(files)
                    .stream()
                    .map(file -> documentFileService.storeFile(file, document1))
                    .collect(Collectors.toList());
        }

        if(isMail) {

            // 교육대상자 등록
            for(String userId : mails) {

                Account account = userService.findByUserId(userId);
                DocumentAccount documentAccount = new DocumentAccount();
                documentAccount.setDocument(document1);
                documentAccount.setAccount(account);
                documentAccount.setRequestDate(DateUtil.getTodayString());
                documentAccount.setRequestType(CourseRequestType.SPECIFY);
                documentAccount.setApprUserId1(userService.getAccountByUserId(account.getParentUserId()));
                documentAccount.setApprUserId2(userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId()));
                documentAccount.setIsTeamMangerApproval(document1.getTemplate().getIsTeamMangerApproval());
                documentAccount.setIsCourseMangerApproval(document1.getTemplate().getIsCourseMangerApproval());

                documentAccountService.save(documentAccount);
            }

            // 메일 보내기
//            for(String userId : mails) {
//
//                String email = userMapperService.getUserById(userId).getEmail();
//
//                // 메일보내기(서비스 전까지는 메일 보내지 않음
//                Mail mail = new Mail();
//                //mail.setEmail(email);
//                mail.setEmail("ks.hwang@safesoft.co.kr");
//                mail.setMessage(course1.getContent());
//                mail.setObject(course1.getTitle());
//
//                mailService.send(mail);
//            }
        }

        return "redirect:/document/list";
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        Document document = documentService.getById(id);

        pageInfo.setPageTitle(document.getTitle() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("border", document);
        model.addAttribute("id", document.getId());
        model.addAttribute("mailList", userService.getAccountList());

        return "content/document/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid Document document
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            document.setId(id);
            return "/content/document/list";
        }


        List<DocumentFile> documentFiles = documentService.getById(id).getDocumentFiles();
        document.setDocumentFiles(documentFiles);

        List<DocumentAccount> documentAccountList = documentService.getById(id).getDocumentAccountList();
        documentAccountList.clear();


        //  교육 필수대상자 수정 처리

        if(!mails[0].equals("0")) {

            // 교육대상자 알림 메일 보내기
            for (String userId : mails) {

                Account account = userService.getAccountByUserId(userId);
                if(account != null) {
                    DocumentAccount courseAccount = new DocumentAccount();

                    courseAccount.setDocument(document);
                    courseAccount.setAccount(account);
                    courseAccount.setRequestDate(DateUtil.getTodayString());
                    courseAccount.setRequestType(CourseRequestType.SPECIFY);        // 교육신청유형(관리자지정, 사용자 신청)
                    courseAccount.setStatus(ApprovalStatusType.REQUEST_DONE);    // 신청완료(팀장승인진행중)
                    courseAccount.setApprUserId1(userService.getAccountByUserId(account.getParentUserId()));
                    courseAccount.setApprUserId2(userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId()));
                    courseAccount.setIsTeamMangerApproval(document.getTemplate().getIsTeamMangerApproval());
                    courseAccount.setIsCourseMangerApproval(document.getTemplate().getIsCourseMangerApproval());
                    documentAccountList.add(courseAccount);
                }
            }
        }

        document.setDocumentAccountList(documentAccountList);


        Document document1 = documentService.save(document);

        Arrays.asList(files)
                .stream()
                .map(file -> documentFileService.storeFile(file, document1))
                .collect(Collectors.toList());


        return "redirect:/document/list";
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Document document = documentService.getById(id);

        documentService.delete(document);

        return "redirect:/document/list";
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


    // 결재현황
    @GetMapping("/approval/{id}")
    public String approval(@PathVariable("id") long id, Model model) {

        Document oldDocument = documentService.getById(id);

        oldDocument.setViewCnt(oldDocument.getViewCnt() + 1);

        Document document= documentService.save(oldDocument);

        DocumentAccount documentAccount = documentAccountService.getByDocumentIdAndUserId(id, SessionUtil.getUserId());

        pageInfo.setPageTitle(document.getTemplate().getTitle() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("document", document);
        model.addAttribute("documentAccount", documentAccount);

        return "content/document/approval";
    }

    // 교육신청 1차 결재 승인
    @GetMapping("/approvalAppr1/{id}")
    public String approvalAppr1(@PathVariable("id") long id, Model model) {

        Document oldDocument = documentService.getById(id);

        oldDocument.setViewCnt(oldDocument.getViewCnt() + 1);

        Document document= documentService.save(oldDocument);

        DocumentAccount documentAccount = documentAccountService.getByDocumentIdAndUserId(id, SessionUtil.getUserId());

        pageInfo.setPageTitle(document.getTemplate().getTitle() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", document);
        model.addAttribute("courseAccount", documentAccount);

        return "content/document/approvalAppr1";
    }

    // 교육신청 2차 결재 승인
    @GetMapping("/approvalAppr2/{id}")
    public String approvalAppr2(@PathVariable("id") long id, Model model) {

        Document oldDocument = documentService.getById(id);

        oldDocument.setViewCnt(oldDocument.getViewCnt() + 1);

        Document document= documentService.save(oldDocument);

        DocumentAccount documentAccount = documentAccountService.getByDocumentIdAndUserId(id, SessionUtil.getUserId());

        pageInfo.setPageTitle(document.getTemplate().getTitle() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("course", document);
        model.addAttribute("courseAccount", documentAccount);

        return "content/document/approvalAppr2";
    }

}