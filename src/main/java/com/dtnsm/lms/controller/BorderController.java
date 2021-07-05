package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.BorderFile;
import com.dtnsm.lms.domain.QBorder;
import com.dtnsm.lms.repository.BorderRepository;
import com.dtnsm.lms.service.BorderFileService;
import com.dtnsm.lms.service.BorderMasterService;
import com.dtnsm.lms.service.BorderService;
import com.dtnsm.lms.service.FileService;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/border")
public class BorderController {

    @Autowired
    BorderMasterService borderMasterService;

    @Autowired
    BorderService borderService;

    @Autowired
    BorderRepository borderRepository;

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

        Page<Border> borders;

        searchText = searchText.trim();

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
        model.addAttribute("typeId", typeId);
        model.addAttribute("borders", borders);

        return "content/border/list";
    }


    @GetMapping("/{typeId}/view/{id}")
    public String viewPage(@PathVariable("id") long id
            , Model model, HttpSession session) {

        session.invalidate();

        Border border = borderService.getBorderById(id);
        borderService.updateViewCnt(id, SessionUtil.getUserId());

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
//        contentType = mimeTypesMap.getContentType(borderFile.getSaveName());

        contentType = borderFile.getMimeType();
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, borderFile.getFileName());


        return ResponseEntity.ok()
                //                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + newFileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);

    }
}