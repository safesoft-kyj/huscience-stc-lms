package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.BorderFile;
import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.service.BorderFileService;
import com.dtnsm.lms.service.BorderMasterService;
import com.dtnsm.lms.service.BorderService;
import com.dtnsm.lms.service.FileService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
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
import org.springframework.web.bind.annotation.*;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/border")
public class BorderController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BorderController.class);

    @Autowired
    BorderMasterService borderMasterService;

    @Autowired
    BorderService borderService;

    @Autowired
    private BorderFileService borderFileService;

    @Autowired
    private FileService fileService;

    private PageInfo pageInfo = new PageInfo();

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    BorderFile borderFile;
    Border oldBorder;
    Resource resource;
    String contentType;

    public BorderController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("학습지원");
    }

    @GetMapping("/list/{typeId}")
    public String listPage(@PathVariable("typeId") String typeId
            , @RequestParam(value = "searchType", defaultValue = "all") String searchType
            , @RequestParam(value = "searchText", defaultValue = "") String searchText
            , @PageableDefault Pageable pageable
            , Model model) {

        // 초기생성만 되고 타이틀이 없는 경우 삭제
        //borderService.deleteBlankBorder();

        String borderName = borderMasterService.getById(typeId).getBorderName();
        pageInfo.setPageTitle(borderName);

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

        return "content/border/list";
    }


    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        oldBorder = borderService.getBorderById(id);

        oldBorder.setViewCnt(oldBorder.getViewCnt() + 1);

        Border border= borderService.save(oldBorder);

        pageInfo.setPageTitle(border.getBorderMaster().getBorderName());

        model.addAttribute(pageInfo);
        model.addAttribute("border", border);

        return "content/border/view";
    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable int id, HttpServletRequest request){

        borderFile = borderFileService.getUploadFile(id);

        // Load file as Resource
        resource = borderFileService.loadFileAsResource(borderFile.getSaveName());

        // Try to determine file's content type
        contentType = mimeTypesMap.getContentType(borderFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
//        if(contentType.equals("")) {
//            contentType = "application/octet-stream";
//        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, borderFile.getFileName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);

    }
}