package com.dtnsm.lms.controller;

import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.GlobalUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
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
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/document")
public class DocumentController {

//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    MailService mailService;

    @Autowired
    ApprovalDocumentProcessService approvalDocumentProcessService;

    @Autowired
    DocumentTemplateService templateService;

    @Autowired
    private DocumentAccountOrderService documentAccountOrderService;


    @Autowired
    private DocumentCourseAccountService documentCourseAccountService;

    @Autowired
    DocumentService documentService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private CourseManagerService courseManagerService;

    @Autowired
    private DocumentFileService documentFileService;

    @Autowired
    SignatureRepository signatureRepository;

    @Autowired CourseService courseService;

    @Autowired
    CourseAccountService courseAccountService;


    private PageInfo pageInfo = new PageInfo();

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public DocumentController() {
        pageInfo.setParentId("m-document");
        pageInfo.setParentTitle("????????????");
    }

    // region # ????????????
    @GetMapping("/list")
    public String listPage(@RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @PageableDefault Pageable pageable
            , Model model) {

        pageInfo.setPageTitle("???????????? ??????");

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


    // ????????????
    @GetMapping("/templateView")
    public String noticeView(Model model) {

        pageInfo.setPageTitle("????????????");

        model.addAttribute(pageInfo);
        model.addAttribute("templateList", templateService.getList());

        return "content/document/templateView";
    }

    @GetMapping("/view/{id}")
    public String noticeView(@PathVariable("id") long id, Model model) {

        Document oldBorder = documentService.getById(id);
        oldBorder.setViewCnt(oldBorder.getViewCnt() + 1);

        Document border= documentService.save(oldBorder);

        pageInfo.setPageTitle("???????????? ??????");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);

        return "content/document/view";
    }

    @GetMapping("/add/{templateId}")
    public String documentAdd(@PathVariable("templateId") int templateId, Model model) {

        DocumentTemplate template = templateService.getById(templateId);

        Document document = new Document();
        document.setTemplate(template);
        document.setTitle("[" + template.getTitle() + "]");
        document.setContent(template.getContent());

        pageInfo.setPageTitle(template.getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("document", document);
        model.addAttribute("mailList", userService.getAccountList());

        return "content/document/add";
    }

    @GetMapping("/addAccount/{templateId}/{docId}")
    public String noticeAdd(@PathVariable("templateId") int templateId
            , @PathVariable("docId") long docId
            , Model model) {

        DocumentTemplate template = templateService.getById(templateId);

        Document document = new Document();
        document.setTemplate(template);
        document.setTitle("[" + template.getTitle() + "]");
        document.setContent(template.getContent());
        document.setCourseAccount(courseAccountService.getById(docId));

        pageInfo.setPageTitle(template.getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("document", document);
        model.addAttribute("mailList", userService.getAccountList());

        return "content/document/addAccount";
    }

    @GetMapping("/popupCourse/{typeId}")
    public String popupCourse(@PathVariable("typeId") String typeId, Model model, Pageable pageable) {

        Page<CourseAccount> coursePorcessList = courseAccountService.getAllByCourse_CourseMaster_IdAndAccount_UserIdAndFStatusAndIsCommit("BC0104", SessionUtil.getUserId(), "1", "0", pageable);
        Page<CourseAccount> courseComplteList = courseAccountService.getAllByCourse_CourseMaster_IdAndAccount_UserIdAndFStatusAndIsCommit("BC0104", SessionUtil.getUserId(), "1", "1", pageable);

//        Document border = new Document();
//        border.setTemplate(template);
//        border.setTitle("[" + template.getTitle() + "]");
//        border.setContent(template.getContent());
//
//        pageInfo.setPageTitle(template.getTitle());

        model.addAttribute(pageInfo);
        model.addAttribute("processList", coursePorcessList);
        model.addAttribute("completeList", courseComplteList);

        return "content/document/popupCourse";
    }

    // ???????????? ????????? ?????? ??????
    @PostMapping("/addAccount-post")
    @Transactional
    public String addAccountPost(@Valid Document document
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            //return "document/add/" + document.getTemplate().getId();
            return "redirect:/document/add/" + document.getTemplate().getId() + '/' + document.getCourseAccount().getId();
        }

        DocumentTemplate template = templateService.getById(document.getTemplate().getId());

        Optional<CourseAccount> optCourseAccount = courseAccountService.getId(document.getCourseAccount().getId());

        if(optCourseAccount.isPresent()) {

            CourseAccount courseAccount = optCourseAccount.get();
            // ??????????????? ????????? ????????? ??????????????? ????????????.
            courseAccount.setReportStatus("0");

            // ????????? ???????????? ????????????.
            document.setCourseAccount(courseAccountService.save(courseAccount));
        }

        document.setAccount(userService.getAccountByUserId(SessionUtil.getUserId()));
        document.setTemplate(template);
        Document document1 = documentService.save(document);

        if (files.length > 0) {
            Arrays.asList(files)
                    .stream()
                    .map(file -> documentFileService.storeFile(file, document1))
                    .collect(Collectors.toList());
        }

        // ?????? ?????? ??????
        Account account = userService.findByUserId(SessionUtil.getUserId());
        approvalDocumentProcessService.documentRequestProcess(account, document1);

        return "redirect:/approval/mainRequest2/report";
    }

    @PostMapping("/add-post")
    @Transactional
    public String noticeAddPost(@Valid Document document
            , @RequestParam(value = "mailList", required = false, defaultValue = "0") String[] mails
            , @RequestParam("files") MultipartFile[] files
            , BindingResult result) {
        if(result.hasErrors()) {
            //return "document/add/" + document.getTemplate().getId();
            return "redirect:/document/add/" + document.getTemplate().getId();
        }

        // TODO : ????????????????????? Template ID??? ????????? ?????? ??????
        if (document.getTemplate().getId() == 1) {
            if (document.getCourseAccount().getId() == 0) return "redirect:/document/add/" + document.getTemplate().getId();
        }

        boolean isMail = mails[0].equals("0") ? false : true;

        if (isMail) {
            document.setMailSender(Arrays.toString(mails));
        }

        DocumentTemplate template = templateService.getById(document.getTemplate().getId());

        //  ???????????? ????????????(??????????????????????????? ?????????)
        if (document.getCourseAccount() != null) {
            if (document.getCourseAccount().getId() >= 0) {

                Optional<CourseAccount> courseAccount = courseAccountService.getId(document.getCourseAccount().getId());

                if(courseAccount.isPresent()) {
                    document.setCourseAccount(courseAccount.get());
                }
            }
        }
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

        // ?????? ?????? ??????
        Account account = userService.findByUserId(SessionUtil.getUserId());
        approvalDocumentProcessService.documentRequestProcess(account, document1);

        if(isMail) {

            // ??????????????? ??????
            for(String userId : mails) {
                Account courseAccount = userService.findByUserId(userId);
                DocumentCourseAccount documentCourseAccount = new DocumentCourseAccount();
                documentCourseAccount.setDocument(document1);
                documentCourseAccount.setAccount(courseAccount);
                documentCourseAccountService.save(documentCourseAccount);
            }
        }

        return "redirect:/approval/document/listMyProcess";
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        Document document = documentService.getById(id);

        pageInfo.setPageTitle(document.getTitle() + " ??????");

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
            , BindingResult result
            , HttpServletRequest request) {
        if(result.hasErrors()) {
            document.setId(id);
            return "/content/document/list";
        }

        Document oldDocument = documentService.getById(id);


//        List<DocumentAccount> documentAccountList = documentService.getById(id).getDocumentAccountList();
//        documentAccountList.clear();
//
//        Account account = userService.getAccountByUserId(SessionUtil.getUserId());
//        approvalDocumentProcessService.documentRequestProcess(account, document);
//
//        document.setDocumentAccountList(documentAccountList);


        if (oldDocument != null) {

            document.setCourseAccount(oldDocument.getCourseAccount());
            document.setDocumentFiles(oldDocument.getDocumentFiles());
            document.setDocumentCourseAccountList(oldDocument.getDocumentCourseAccountList());

            // ?????????????????? ????????? ?????? ?????? ????????? ????????????.
            if (oldDocument.getFnCurrSeq() <= 1) {

                Document document1 = documentService.save(document);

                Arrays.asList(files)
                        .stream()
                        .map(file -> documentFileService.storeFile(file, document1))
                        .collect(Collectors.toList());

            }
        }

        // ?????? URL??? ????????????.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id, HttpServletRequest request) {

        Document document = documentService.getById(id);

        // ?????????????????? ????????? ?????? ?????? ????????? ????????????.
        if (document != null) {

            if (document.getFnCurrSeq() <= 1) {

                // ????????? ?????? ????????? ???????????? ???????????? ??????????????? ????????????.
                CourseAccount courseAccount = document.getCourseAccount();
                courseAccount.setReportStatus("9");
                courseAccountService.save(courseAccount);

                documentService.delete(document);
            }
        }

        // ?????? URL??? ????????????.
        String refUrl = request.getHeader("referer");
        return "redirect:" +  refUrl;
    }


    @GetMapping("/delete-file/{id}/{file_id}")
    public String noticeDeleteFile(@PathVariable int id, @PathVariable int file_id, HttpServletRequest request){

        // db??? ?????? ??????
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

        // ??????????????? ?????? ?????? ??????
        String newFileName = FileUtil.getNewFileName(request, documentFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }

    // endregion


    // ????????????
    @GetMapping("/approval/{id}")
    public String approval(@PathVariable("id") long docId, Model model) {

        Document document = documentService.getById(docId);

        pageInfo.setPageTitle(document.getTemplate().getTitle() + " ??????");

        model.addAttribute(pageInfo);
        model.addAttribute("document", document);
        model.addAttribute("signature", GlobalUtil.getSignature(signatureRepository, SessionUtil.getUserId()));

        return "content/document/approval";
    }

    // ???????????? 1??? ?????? ??????
    @GetMapping("/approvalAppr1/{id}")
    public String approvalAppr1(@PathVariable("id") long orderId, Model model) {

        DocumentAccountOrder documentAccountOrder = documentAccountOrderService.getById(orderId);

        Document document = documentAccountOrder.getDocument();

        pageInfo.setPageTitle(document.getTemplate().getTitle() + " ??????");

        model.addAttribute(pageInfo);
        model.addAttribute("document", document);
        model.addAttribute("documentAccountOrder", documentAccountOrder);
        model.addAttribute("userId", SessionUtil.getUserId());

        return "content/document/approvalAppr1";
    }

    // ???????????? 2??? ?????? ??????
    @GetMapping("/approvalAppr2/{id}")
    public String approvalAppr2(@PathVariable("id") long docId, Model model) {

        Document document = documentService.getById(docId);

        pageInfo.setPageTitle(document.getTemplate().getTitle() + " ??????");

        model.addAttribute(pageInfo);
        model.addAttribute("document",  document);
        model.addAttribute("documentAccountOrder", document.getDocumentAccountOrders());
        model.addAttribute("userId", SessionUtil.getUserId());
        return "content/document/approvalAppr2";
    }

    // ????????????(1??? ??????/?????????) ??????
//    @GetMapping("/rejectAppr1/{id}")
//    public String rejectAppr1(@PathVariable("id") long id, Model model) {
//
//        pageInfo.setPageId("m-mypage-approval");
//        pageInfo.setPageTitle("??????????????????");
//
//        // ??????ID??? ?????????ID??? ????????????????????? ????????? ??????.
//        DocumentAccount documentAccount = documentAccountService.getByDocumentIdAndUserId(documentId, userId);
//
//        // 1??? ?????? ??????
//        approvalDocumentProcessService.documentReject1Proces(documentAccount);
//
//        model.addAttribute(pageInfo);
//
//        return "redirect:/content/document/listAppr1Process";
//    }

    // ????????????(2??? ???????????????) ??????
//    @GetMapping("/rejectAppr2/{documentId}/{userId}")
//    public String rejectAppr2(@PathVariable("documentId") long documentId
//            , @PathVariable("userId") String userId
//            , Model model) {
//
//        pageInfo.setPageId("m-mypage-approval");
//        pageInfo.setPageTitle("??????????????????");
//
//        // ??????ID??? ?????????ID??? ????????????????????? ????????? ??????.
//        DocumentAccount documentAccount = documentAccountService.getByDocumentIdAndUserId(documentId, userId);
//
//        // 2??? ?????? ??????
//        approvalDocumentProcessService.documentReject2Proces(documentAccount);
//
//        model.addAttribute(pageInfo);
//
//        return "redirect:/content/document/listAppr2Process";
//    }


}