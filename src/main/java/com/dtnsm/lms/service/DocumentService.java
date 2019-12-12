package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.Document;
import com.dtnsm.lms.domain.DocumentFile;
import com.dtnsm.lms.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    DocumentRepository documentRepository;

    @Autowired
    DocumentFileService fileService;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    // region 사용자 조회
    public List<Document> getListByUserId(String userId) {
        return documentRepository.findAllByAccount_UserId(userId);
    }

    public Page<Document> getPageListByUserId(String userId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByAccount_UserId(userId, pageable);
    }

    // 제목 내용 Like 검색
    public Page<Document> getPageListByAccount_UserIdAndTitleLikeOrContentLike(String userId, String title, String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByAccount_UserIdAndTitleLikeOrContentLike(userId, "%" + title + "%", "%" + content + "%", pageable);
    }

    // 제목 Like 검색
    public Page<Document> getPageListByUserIdAndTitleLike(String userId, String title, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByAccount_UserIdAndTitleLike(userId, "%" + title + "%", pageable);
    }

    // 내용 Like 검색
    public Page<Document> getPageListByUserIdAndContentLike(String userId, String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByAccount_UserIdAndContentLike(userId, "%" + content + "%", pageable);
    }

    // endregion 사용자 조회


    // region 어드민 조회

    public List<Document> getList() {
        return documentRepository.findAll();
    }

    public List<Document> getListByTemplateId(String templateId) {
        return documentRepository.findAllByTemplate_Id(templateId);
    }

    public Page<Document> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAll(pageable);
    }

    public Page<Document> getPageList(String templateId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByTemplate_Id(templateId, pageable);
    }

    // 제목 내용 Like 검색
    public Page<Document> getPageListByTitleLikeOrContentLike(String title, String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByTitleLikeOrContentLike("%" + title + "%", "%" + content + "%", pageable);
    }

    // 제목 Like 검색
    public Page<Document> getPageListByTitleLike(String title, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByTitleLike("%" + title + "%", pageable);
    }

    // 내용 Like 검색
    public Page<Document> getPageListByContentLike(String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByContentLike("%" + content + "%", pageable);
    }

    // endregion

    public Document getById(Long id) {

        return documentRepository.findById(id).get();
    }

    public Document save(Document elBorder){

        return documentRepository.save(elBorder);
    }

    public void delete(Document document) {
        // 게시판 데이터및 파일 데이터 delete
        documentRepository.delete(document);

        // 첨부파일 삭제
        for(DocumentFile file : document.getDocumentFiles()) {
            fileService.deleteFile(file);
        }
    }

    // 게시판 조회수 증가
    public void updateViewCnt(Long id) {
        Document document = getById(id);
        document.setViewCnt(document.getViewCnt() + 1);
        documentRepository.save(document);
    }


    public Document getByDocumentIdAndUserId(long docId, String userId) {
        return documentRepository.findByIdAndAccount_UserId(docId, userId);
    }


    public List<Document> getDocumentAccountByDocId(long docId) {
        return documentRepository.findById(docId);
    }

    public List<Document> getCourseAccountByUserId(String userId) {
        return documentRepository.findByAccount_UserId(userId);
    }

    // 상태별 신청 조회
    public List<Document> getAllByStatus(String status) {

        return documentRepository.findByFnStatus(status);
    }

    // 상태별 신청 조회
    public Page<Document> getAllByStatus(String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findByFnStatus(status, pageable);
    }

    // 최종 완결(신청결재, 교육)
    public List<Document> getCourseAccountIsCommitByUserId(String userId, String isCommit) {
        return documentRepository.findAllByAccount_UserIdAndIsCommit(userId, isCommit);
    }

    // 내신청함
    public Page<Document> getListUserId(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findByAccount_UserId(userId, pageable);
    }

    //진행중 문서
    public Page<Document> getAllByStatus(String userId, String status, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findByAccount_UserIdAndFnStatus(userId, status, pageable);
    }

    //진행중 문서
    public List<Document> getAllByStatus(String userId, String status) {
        return documentRepository.findByAccount_UserIdAndFnStatus(userId, status);
    }


    //완결 조회(기각 제외)
    public Page<Document> getCustomByUserCommit(String userId, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findByAccount_UserIdAndFnStatus(userId, "1", pageable);
    }


    public List<Document> getCustomListByUserIdAndIsCommit(String userId, String isCommit) {

        return documentRepository.findByAccount_UserIdAndFnStatus(userId, isCommit);
    }

    // 2차결재자(과정 관리자) 미결, 완결 구분
    public Page<Document> getListApprUserId2AndIsAppr2(String isManagerApproval,  String userId, String status, String isAppr1, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findByAccount_UserIdAndFnStatus(userId, status, pageable);
    }

    // 과정유형, 사용자, 완결여부로 가져오기(과정 팦업창에서 사용)
    public List<Document> getAllByDocument_Template_IdAndIsCommit(int templateId, String isCommit) {
        return documentRepository.findAllByTemplate_IdAndIsCommit(templateId, isCommit);
    }

    // 과정유형, 사용자, 완결여부로 가져오기(과정 팦업창에서 사용)
    public Page<Document> getAllByDocument_Template_IdAndIsCommit(int templateId, String isCommit, Pageable pageable) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByTemplate_IdAndIsCommit(templateId, isCommit, pageable);
    }
}
