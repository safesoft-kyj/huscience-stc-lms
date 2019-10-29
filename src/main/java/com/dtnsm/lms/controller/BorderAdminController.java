package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.BorderFile;
import com.dtnsm.lms.domain.BorderMaster;
import com.dtnsm.lms.service.BorderFileService;
import com.dtnsm.lms.service.BorderMasterService;
import com.dtnsm.lms.service.BorderService;
import com.dtnsm.lms.service.FileService;
import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
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
    private BorderFileService borderFileService;

    @Autowired
    private FileService fileService;

    private PageInfo pageInfo = new PageInfo();

    private BorderMaster borderMaster;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    public BorderAdminController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("공지사항");
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

    @GetMapping("/list/{typeId}")
    public String listPage(@PathVariable("typeId") String typeId
            , @RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @PageableDefault Pageable pageable
            , Model model) {

        // 초기생성만 되고 타이틀이 없는 경우 삭제
        //borderService.deleteBlankBorder();

        String borderName = borderMasterService.getById(typeId).getBorderName();
        pageInfo.setPageTitle(borderName + "조회");

        Page<Border> borders;

        if(searchType.equals("all") && searchText.equals("")) {
            borders = borderService.getPageList(typeId, pageable);
        } else if (searchType.equals("all") && !searchText.equals("")) {
            borders = borderService.getPageListByTitleLikeOrContentLike(typeId, searchText, searchText, pageable);
        } else if (searchType.equals("subject")) {
            borders = borderService.getPageListByTitleLike(typeId, searchText, pageable);
        } else {
            borders = borderService.getPageListByContentLike(typeId, searchText, pageable);
        }


        model.addAttribute(pageInfo);
        model.addAttribute("borders", borders);
        model.addAttribute("typeId", typeId);

        return "admin/border/list";
    }


    @GetMapping("/view/{id}")
    public String noticeView(@PathVariable("id") long id, Model model) {

        Border oldBorder = borderService.getBorderById(id);
        oldBorder.setViewCnt(oldBorder.getViewCnt() + 1);

        Border border= borderService.save(oldBorder);

        pageInfo.setPageTitle(border.getBorderMaster().getBorderName() + " 상세");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);

        return "admin/border/view";
    }

    @GetMapping("/add/{typeId}")
    public String noticeAdd(@PathVariable("typeId") String typeId, Model model) {

        this.borderMaster = borderMasterService.getById(typeId);
        Border border = new Border();
        border.setBorderMaster(this.borderMaster);

        //Border border = borderService.save(new Border("", "", this.borderMaster));

        pageInfo.setPageTitle(borderMaster.getBorderName() + " 등록");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);
        model.addAttribute("id", typeId);

        return "admin/border/add";
    }

    @PostMapping("/add-post")
    @Transactional
    public String noticeAddPost(@Valid Border border, @RequestParam("files") MultipartFile[] files, BindingResult result) {
        if(result.hasErrors()) {
            return "admin/border/add";
        }

        border.setBorderMaster(borderMasterService.getById(border.getBorderMaster().getId()));
        Border border1 = borderService.save(border);

        Arrays.asList(files)
                .stream()
                .map(file -> borderFileService.storeFile(file, border1))
                .collect(Collectors.toList());


        if(border1.getBorderMaster().getIsMail().equals("Y")) {
            // 메일보내기
            Mail mail = new Mail();
            mail.setEmail("ks.hwang@safesoft.co.kr");
            mail.setMessage(border1.getContent());
            mail.setObject(border1.getTitle());

            mailService.send(mail);
        }

        return "redirect:/admin/border/list/" + border1.getBorderMaster().getId();
    }

    // 첨부파일 업로드
    @PostMapping("/file-post")
    public String filePost(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {

        Border border = borderService.getBorderById(id);

        borderFileService.storeFile(file, border);

        return "redirect:/admin/border/list/" + border.getBorderMaster().getId();
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        Border border = borderService.getBorderById(id);

        pageInfo.setPageTitle(border.getBorderMaster().getBorderName() + " 수정");

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);
        model.addAttribute("id", border.getId());

        return "admin/border/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id, @Valid Border border, @RequestParam("files") MultipartFile[] files, BindingResult result) {
        if(result.hasErrors()) {
            border.setId(id);
            return "/admin/border/list/" + border.getBorderMaster().getId();
        }

        List<BorderFile> borderFiles = borderService.getBorderById(id).getBorderFiles();
        border.setBorderFiles(borderFiles);

        Border border1 = borderService.save(border);

        Arrays.asList(files)
                .stream()
                .map(file -> borderFileService.storeFile(file, border1))
                .collect(Collectors.toList());

        return "redirect:/admin/border/list/" + border1.getBorderMaster().getId();
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Border border = borderService.getBorderById(id);

        String borderMastId = border.getBorderMaster().getId();

        borderService.delete(border);

        return "redirect:/admin/border/list/" + borderMastId;
    }


    @GetMapping("/delete-file/{border_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable int border_id, @PathVariable int file_id, HttpServletRequest request){

        // db및 파일 삭제
        borderFileService.deleteFile(file_id);

        return "redirect:/admin/border/edit/" + border_id;

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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }

    // endregion
}
