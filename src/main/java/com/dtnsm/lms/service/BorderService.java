package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.controller.BorderController;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.BorderViewAccount;
import com.dtnsm.lms.domain.BorderFile;
import com.dtnsm.lms.repository.BorderViewAccountRepository;
import com.dtnsm.lms.repository.BorderRepository;
import com.dtnsm.lms.util.DateUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorderService {

    @Autowired
    BorderRepository borderRepository;

    @Autowired
    BorderViewAccountRepository borderViewAccountRepository;

    @Autowired
    UserServiceImpl userService;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BorderController.class);

    @Autowired
    BorderFileService borderFileService;

    public BorderService(BorderRepository elcBorderRepository) {
        this.borderRepository = elcBorderRepository;
    }

    public List<Border> getList() {

        return borderRepository.findAll();
    }

    public List<Border> getListByBorderMasterId(String masterId) {
        return borderRepository.findAllByBorderMaster_Id(masterId);
    }

    public List<Border> getListTop5ByBorderMasterId(String masterId) {
        return borderRepository.findFirst5ByBorderMaster_Id(masterId, Sort.by(Sort.Direction.DESC, "isNotice", "createdDate"));
    }

    public Page<Border> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return borderRepository.findAll(pageable);
    }

    public Page<Border> getPageList(String typeId, Pageable pageable) {

        logger.info("수정 공지사항 갯수 : {}", updateNotice());

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

//        List<Sort> sorts = new ArrayList<>();
//        sorts.add(new Sort(Sort.Direction.DESC, "createdDate"));

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "isNotice", "createdDate"));


        return borderRepository.findAllByBorderMaster_Id(typeId, pageable);
    }

    // 제목 내용 Like 검색
    public Page<Border> getPageListByTitleLikeOrContentLike(String typeId, String title, String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "isNotice", "createdDate"));

        return borderRepository.findAllByBorderMaster_IdAndTitleLikeOrContentLike(typeId,"%" + title + "%", "%" + content + "%", pageable);
    }

    // 제목 Like 검색
    public Page<Border> getPageListByTitleLike(String typeId, String title, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "isNotice", "createdDate"));

        return borderRepository.findAllByBorderMaster_IdAndTitleLike(typeId, "%" + title + "%", pageable);
    }

    // 내용 Like 검색
    public Page<Border> getPageListByContentLike(String typeId, String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "isNotice", "createdDate"));

        return borderRepository.findAllByBorderMaster_IdAndContentLike(typeId, "%" + content + "%", pageable);
    }

    public Border getBorderById(Long id) {

        return borderRepository.findById(id).get();
    }

    public Border save(Border elBorder){

        return borderRepository.save(elBorder);
    }

    public void delete(Border border) {
        // 게시판 데이터및 파일 데이터 delete
        borderRepository.delete(border);

        // 첨부파일 삭제
        for(BorderFile borderFile : border.getBorderFiles()) {
            borderFileService.deleteFile(borderFile);
        }
    }

    // 게시판 조회수 증가
    public void updateViewCnt(long borderId, String userId) {

        List<BorderViewAccount> borderAccounts = borderViewAccountRepository.findAllByBorder_IdAndAccount_UserId(borderId, userId);


        // 최초 읽음수만 증가 시킨다.
        if(borderAccounts.size() > 0) return;


        Border border = getBorderById(borderId);
        Account account = userService.getAccountByUserId(userId);

        if (account == null || border == null) return;
        borderViewAccountRepository.save(new BorderViewAccount(border, account));

        border.setViewCnt(border.getViewCnt() + 1);
        borderRepository.save(border);

    }

    private List<Border> getBlankBorder() {
        return borderRepository.findAllByTitle("");
    }

    public void deleteBlankBorder() {

        for(Border border : getBlankBorder()) {
            this.delete(border);
        }
    }
    public long getCount() {
        return borderRepository.count();
    }

    public int updateNotice() {
        List<Border> borders = borderRepository.findAllByIsNoticeAndToDateLessThan("1", DateUtil.getTodayString());

        if (borders.size() > 0) {

            for(Border border : borders) {
                border.setIsNotice("0");

                borderRepository.save(border);
            }
        }

        return borders.size();
    }


    public List<BorderViewAccount> getAllBorderAccountByBorderId(long borderId) {
        List<BorderViewAccount> borderAccounts = borderViewAccountRepository.findAllByBorder_IdOrderByCreatedByDesc(borderId);
        return borderAccounts;
    }

}
