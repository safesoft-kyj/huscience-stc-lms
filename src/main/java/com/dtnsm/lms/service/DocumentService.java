package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.Document;
import com.dtnsm.lms.domain.DocumentFile;
import com.dtnsm.lms.repository.DocumentRepository;
import com.dtnsm.lms.repository.DocumentTemplateRepository;
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

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByAccount_UserId(userId, pageable);
    }

    // 제목 내용 Like 검색
    public Page<Document> getPageListByAccount_UserIdAndTitleLikeOrContentLike(String userId, String title, String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByAccount_UserIdAndTitleLikeOrContentLike(userId, "%" + title + "%", "%" + content + "%", pageable);
    }

    // 제목 Like 검색
    public Page<Document> getPageListByUserIdAndTitleLike(String userId, String title, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByAccount_UserIdAndTitleLike(userId, "%" + title + "%", pageable);
    }

    // 내용 Like 검색
    public Page<Document> getPageListByUserIdAndContentLike(String userId, String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

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

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAll(pageable);
    }

    public Page<Document> getPageList(String templateId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByTemplate_Id(templateId, pageable);
    }

    // 제목 내용 Like 검색
    public Page<Document> getPageListByTitleLikeOrContentLike(String title, String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByTitleLikeOrContentLike("%" + title + "%", "%" + content + "%", pageable);
    }

    // 제목 Like 검색
    public Page<Document> getPageListByTitleLike(String title, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return documentRepository.findAllByTitleLike("%" + title + "%", pageable);
    }

    // 내용 Like 검색
    public Page<Document> getPageListByContentLike(String content, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

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

    public long getCount() {
        return documentRepository.count();
    }
}
