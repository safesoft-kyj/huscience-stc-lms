package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.repository.BorderRepository;
import com.dtnsm.lms.repository.BorderViewAccountRepository;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/admin/border")
public class BorderAdminController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BorderAdminController.class);

    @Autowired
    MailService mailService;

    @Autowired
    BorderMasterService borderMasterService;

    @Autowired
    BorderService borderService;

    @Autowired
    BorderRepository borderRepository;

    @Autowired
    private BorderFileService borderFileService;

    @Autowired
    private UserService userService;

    @Autowired
    private BorderViewAccountRepository borderViewAccountRepository;

    @Autowired
    CourseManagerService courseManagerService;

    @Autowired
    private FileService fileService;

    private PageInfo pageInfo = new PageInfo();

    private BorderMaster borderMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public BorderAdminController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("게시판");
    }


    // region # 공지사항
//
//    @GetMapping("/list/{typeId}")
//    public String noticeListMulti(@PathVariable("typeId") String typeId, @PageableDefault Pageable pageable, Model model) {
//
//        // 초기생성만 되고 타이틀이 없는 경우 삭제
//        //borderService.deleteBlankBorder();
//
//        String borderName = borderMasterService.getById(typeId).getBorderName();
//        pageInfo.setPageTitle(borderName + "조회");
//
//        Page<Border> borders = borderService.getPageList(typeId, pageable);
//        model.addAttribute(pageInfo);
//        model.addAttribute("borders", borders);
//        model.addAttribute("typeId", typeId);
//
//        return "admin/border/list";
//    }

    @GetMapping("/{typeId}")
    public String listPage(@PathVariable("typeId") String typeId
            , @RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @PageableDefault Pageable pageable
            , Model model) {

        // 초기생성만 되고 타이틀이 없는 경우 삭제
        //borderService.deleteBlankBorder();

        String borderName = borderMasterService.getById(typeId).getBorderName();
        pageInfo.setPageTitle(borderName);

        searchText = searchText.trim();

        Page<Border> borders;

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "isNotice", "createdDate"));

        BooleanBuilder builder = new BooleanBuilder();
        QBorder border = QBorder.border;
        builder.and(border.borderMaster.id.eq(typeId));

        if(searchType.equals("all")) {
//            borders = borderService.getPageList(typeId, pageable);
//            borders = borderService.getPageListByTitleLikeOrContentLike(typeId, searchText, searchText, pageable);
            builder.and(border.title.contains(searchText).or(border.content.contains(searchText)));
        } else if (searchType.equals("subject")) {
//            borders = borderService.getPageListByTitleLike(typeId, searchText, pageable);
            builder.and(border.title.contains(searchText));
        } else if (searchType.equals("content")) {
//            borders = borderService.getPageListByContentLike(typeId, searchText, pageable);
            builder.and(border.content.contains(searchText));
        }

        borders = borderRepository.findAll(builder, pageable);

        model.addAttribute(pageInfo);
        model.addAttribute("borders", borders);
        model.addAttribute("typeId", typeId);

        return "admin/border/list";
    }


    @GetMapping("/{typeId}/view/{id}")
    public String noticeView(@PathVariable("typeId") String typeId
            , @PathVariable("id") long id, Model model) {

        Border border = borderService.getBorderById(id);

        pageInfo.setPageTitle(border.getBorderMaster().getBorderName());

        model.addAttribute(pageInfo);
        model.addAttribute("typeId", typeId);
        model.addAttribute("border", border);

        return "admin/border/view";
    }

    @GetMapping("/{typeId}/add")
    public String noticeAdd(@PathVariable("typeId") String typeId, Model model) {

        this.borderMaster = borderMasterService.getById(typeId);
        Border border = new Border();
        border.setBorderMaster(this.borderMaster);

        //Border border = borderService.save(new Border("", "", this.borderMaster));

        pageInfo.setPageTitle(borderMaster.getBorderName());

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);
        model.addAttribute("typeId", typeId);

        return "admin/border/add";
    }

    @PostMapping("/add-post")
    @Transactional
    public String noticeAddPost(@Valid Border border
            , @RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @RequestParam(value = "page", defaultValue = "") String page
            , @RequestParam("files") MultipartFile[] files, BindingResult result
            , HttpServletRequest request) {
        if(result.hasErrors()) {
            return "admin/border/add";
        }

        border.setBorderMaster(borderMasterService.getById(border.getBorderMaster().getId()));
        if (!border.getIsNotice().equals("1")) {
            border.setFromDate("1900-01-01");
            border.setToDate("1900-01-01");
        } else {
            border.setFromDate(DateUtil.getTodayString());
        }

        border.setAccount(userService.findByUserId(SessionUtil.getUserId()));
        Border border1 = borderService.save(border);

        Arrays.asList(files)
                .stream()
                .map(file -> borderFileService.storeFile(file, border1))
                .collect(Collectors.toList());


        if(border1.getBorderMaster().getIsMail().equals("Y")) {
            // 메일보내기
            Mail mail = new Mail();
            mail.setEmail(courseManagerService.getCourseManager().getAccount().getEmail());
            mail.setMessage(border1.getContent());
            mail.setObject(String.format("[LMS/%s/%s] %s", border1.getBorderMaster().getBorderName(), border1.getAccount().getName(), border1.getTitle()));

            mailService.sendBorder(mail);
        }

        return String.format("redirect:/admin/border/%s?page=%s&searchType=%s&searchText=%s"
                , border1.getBorderMaster().getId()
                , page
                , searchType
                , searchText);

    }

    // 첨부파일 업로드
    @PostMapping("/file-post")
    public String filePost(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {

        Border border = borderService.getBorderById(id);

        borderFileService.storeFile(file, border);

        return "redirect:/admin/border/list/" + border.getBorderMaster().getId();
    }

    @GetMapping("/{typeId}/edit/{id}")
    public String noticeEdit(@PathVariable("typeId") String typeId, @PathVariable("id") long id, Model model) {

        Border border = borderService.getBorderById(id);

        pageInfo.setPageTitle(border.getBorderMaster().getBorderName());

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);
        model.addAttribute("typeId", typeId);
        model.addAttribute("id", border.getId());

        return "admin/border/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id, @Valid Border border
            , @RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @RequestParam(value = "page", defaultValue = "") String page
            , @RequestParam("files") MultipartFile[] files, BindingResult result
            , HttpServletRequest request) {
        if(result.hasErrors()) {
            border.setId(id);
            return "/admin/border/" + border.getBorderMaster().getId();
        }

        Border oldBorder = borderService.getBorderById(id);


        if (!border.getIsNotice().equals("1")) {
            border.setFromDate("1900-01-01");
            border.setToDate("1900-01-01");
        } else {
            border.setFromDate(DateUtil.getTodayString());
        }

        border.setAccount(userService.findByUserId(SessionUtil.getUserId()));
        border.setBorderFiles(oldBorder.getBorderFiles());
        border.setBorderViewAccounts(oldBorder.getBorderViewAccounts());
        border.setViewCnt(oldBorder.getViewCnt());

        Border border1 = borderService.save(border);

        Arrays.asList(files)
                .stream()
                .map(file -> borderFileService.storeFile(file, border1))
                .collect(Collectors.toList());

        return String.format("redirect:/admin/border/%s?page=%s&searchType=%s&searchText=%s"
                , border1.getBorderMaster().getId()
                , page
                , searchType
                , searchText);
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id
            , @RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @RequestParam(value = "page", defaultValue = "") String page) {

        List<BorderViewAccount> borderViewAccounts = borderService.getAllBorderAccountByBorderId(id);

        // 게시물 조회자를 먼저 삭제한다.
        for(BorderViewAccount borderViewAccount : borderViewAccounts) {
            borderViewAccountRepository.delete(borderViewAccount);
        }

        Border border = borderService.getBorderById(id);
        String borderMastId = border.getBorderMaster().getId();

        borderService.delete(border);

        return String.format("redirect:/admin/border/%s?page=%s&searchType=%s&searchText=%s"
                , borderMastId
                , page
                , searchType
                , searchText);
    }


    @GetMapping("/delete-file/{border_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable long border_id, @PathVariable int file_id
            , @RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @RequestParam(value = "page", defaultValue = "") String page
            , HttpServletRequest request){

        // db및 파일 삭제
        borderFileService.deleteFile(file_id);

        Border border = borderService.getBorderById(border_id);

        return String.format("redirect:/admin/border/%s/edit/%s?page=%s&searchType=%s&searchText=%s"
                , border.getBorderMaster().getId()
                , border_id
                , page
                , searchType
                , searchText);

    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable int id, HttpServletRequest request){

        BorderFile borderFile = borderFileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = borderFileService.loadFileAsResource(borderFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(borderFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, borderFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + newFileName + "\"")
                .body(resource);
    }

    // endregion
}
