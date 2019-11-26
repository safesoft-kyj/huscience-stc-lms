package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.Document;
import com.dtnsm.lms.domain.DocumentAccountOrder;
import com.dtnsm.lms.domain.constant.MailSendType;
import com.dtnsm.lms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalDocumentProcessService {

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentAccountOrderService documentAccountOrderService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CourseManagerService courseManagerService;

    @Autowired
    private MailService mailService;

    @Autowired
    SignatureService signatureService;

    // 기안 처리
    public void documentRequestProcess(Account account, Document document) {

        // 팀장/부서장 승인여부
        String isAppr1 = document.getTemplate().getIsTeamMangerApproval();
        // 관리자 승인여부
        String isAppr2 = document.getTemplate().getIsCourseMangerApproval();


        // 기안
        document.setAccount(account);
        document.setRequestDate(DateUtil.getTodayString());
        document.setFWdate(DateUtil.getToday());
        document.setFStatus("0");
        document.setFCurrSeq(1);

        // 결재자수 Max 설정
        if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
            document.setFFinalCount(2);
        } else if(isAppr1.equals("Y")) {
            document.setFFinalCount(1);
        } else {
            document.setFFinalCount(0);
            document.setFStatus("1");      // 전자결재가 없으면 완료한것으로 처리한다.
        }

        Document saveDocument = documentService.save(document);


        // 내결재사항을 추가한다.
        DocumentAccountOrder documentAccountOrder = new DocumentAccountOrder();
        documentAccountOrder.setFUser(account);
        documentAccountOrder.setSignature(signatureService.getSign(account.getUserId()));
        documentAccountOrder.setDocument(saveDocument);

        // fKind : 0:초기, 1:결재, 2. 합의, 3:확인
        documentAccountOrder.setFKind("1");
        documentAccountOrder.setFStatus("1");
        documentAccountOrder.setFSeq(0);
        documentAccountOrder.setFNext("0");
        documentAccountOrder.setFDate(DateUtil.getToday());
        documentAccountOrder.setFComment("");
        documentAccountOrderService.save(documentAccountOrder);


        if (isAppr1.equals("Y")) {
            Account apprAccount2 = userService.getAccountByUserId(account.getParentUserId());
            DocumentAccountOrder documentAccountOrder2 = new DocumentAccountOrder();
            documentAccountOrder2.setFUser(apprAccount2);
            documentAccountOrder2.setDocument(saveDocument);
            // fKind : 0:초기, 1:결재, 2. 합의, 3:확인
            documentAccountOrder2.setFKind("1");
            documentAccountOrder2.setFStatus("0");
            documentAccountOrder2.setFSeq(1);
            documentAccountOrder2.setFNext("1");

            documentAccountOrderService.save(documentAccountOrder2);

            // 1차 결재자 메일 전송
            sendMail(apprAccount2, document, MailSendType.REQUEST);
        }

        if (isAppr2.equals("Y")) {
            Account apprAccount3 = userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId());
            DocumentAccountOrder documentAccountOrder3 = new DocumentAccountOrder();
            documentAccountOrder3.setFUser(apprAccount3);
            documentAccountOrder3.setDocument(saveDocument);
            documentAccountOrder3.setFKind("1");
            documentAccountOrder3.setFStatus("0");
            documentAccountOrder3.setFSeq(2);

            documentAccountOrderService.save(documentAccountOrder3);


            // TODO : 전자결재 생성시 추가로 해야할 사항
        }

        // 알림 등록
    }

    // 1차 승인 처리
    public void documentApproval1Proces(DocumentAccountOrder documentAccountOrder) {

        int finalCount = documentAccountOrder.getDocument().getFFinalCount();

        documentAccountOrder.setSignature(signatureService.getSign(documentAccountOrder.getFUser().getUserId()));
        documentAccountOrder.setFDate(DateUtil.getToday());
        documentAccountOrder.setFNext("0");
        documentAccountOrder.setFStatus("1");
        documentAccountOrder.setFComment("");

        documentAccountOrder = documentAccountOrderService.save(documentAccountOrder);

        Document document = documentAccountOrder.getDocument();

        if(finalCount == documentAccountOrder.getFSeq()) {   // 종결처리
            //  승인: 1, 기각 : 2
            document.setFStatus("1");    // 0 진행중, 1:승인, 2:기각
            document.setIsCommit("1");   // 0 진행중 1:종결

            documentService.save(document);

            // 최종 승인이면 기안자에게 메일 전송
            sendMail(document.getAccount(), document, MailSendType.APPROVAL1);
        } else {
            DocumentAccountOrder nextOrder = documentAccountOrderService.getByFnoAndSeq(documentAccountOrder.getDocument().getId(), documentAccountOrder.getFSeq()+1);
            if (nextOrder != null) {

                nextOrder.setFNext("1");
                nextOrder = documentAccountOrderService.save(nextOrder);

                // 마스터에 다음 결재자 순번을 업데이트 한다.
                document.setFCurrSeq(nextOrder.getFSeq());
                documentService.save(document);

                sendMail(nextOrder.getFUser(), document, MailSendType.APPROVAL1);
            }
        }
    }

    // 전자결재 1차 기각 처리
    public void documentReject1Proces(DocumentAccountOrder documentAccountOrder) {

        int finalCount = documentAccountOrder.getDocument().getFFinalCount();

        documentAccountOrder.setSignature(signatureService.getSign(documentAccountOrder.getFUser().getUserId()));
        documentAccountOrder.setFDate(DateUtil.getToday());
        documentAccountOrder.setFNext("0");
        documentAccountOrder.setFStatus("2");

        documentAccountOrder = documentAccountOrderService.save(documentAccountOrder);

        Document document = documentAccountOrder.getDocument();

        document.setFStatus("2");

        documentService.save(document);

        // 기안자에게 메일 전송
        sendMail(document.getAccount(), document, MailSendType.REJECT);
    }

    // 전자결재 2차 승인 처리
    public void documentApproval2Proces(DocumentAccountOrder documentAccountOrder) {

        int finalCount = documentAccountOrder.getDocument().getFFinalCount();

        documentAccountOrder.setSignature(signatureService.getSign(documentAccountOrder.getFUser().getUserId()));
        documentAccountOrder.setFDate(DateUtil.getToday());
        documentAccountOrder.setFNext("0");
        documentAccountOrder.setFStatus("1");
        documentAccountOrder.setFComment("");

        documentAccountOrder = documentAccountOrderService.save(documentAccountOrder);

        Document document = documentAccountOrder.getDocument();

        if(finalCount == documentAccountOrder.getFSeq()) {   // 종결처리
            //  승인: 1, 기각 : 2
            document.setFStatus("1");
            document.setIsCommit("1");   // 0 진행중 1:종결

            document = documentService.save(document);

            // TODO : 외부교육 2차 외부교육 참석보고서 승인 처리시 바인더 로그 처리 (8: 외부교육참석보고서, 217:부서별교육신청서)
            if(document.getTemplate().getId() == 8) {


            }


            // 최종 승인이면 기안자에게 메일 전송
            sendMail(document.getAccount(), document, MailSendType.APPROVAL2);
        } else {
            DocumentAccountOrder nextOrder = documentAccountOrderService.getByFnoAndSeq(documentAccountOrder.getDocument().getId(), documentAccountOrder.getFSeq()+1);
            if (nextOrder != null) {

                nextOrder.setFNext("1");
                nextOrder = documentAccountOrderService.save(nextOrder);

                // 마스터에 다음 결재자 순번을 업데이트 한다.
                document.setFCurrSeq(nextOrder.getFSeq());
                documentService.save(document);

                sendMail(nextOrder.getFUser(), document, MailSendType.APPROVAL2);
            }
        }
    }

    // 전자결재 2차 기각 처리
    public void documentReject2Proces(DocumentAccountOrder documentAccountOrder) {

        int finalCount = documentAccountOrder.getDocument().getFFinalCount();

        documentAccountOrder.setSignature(signatureService.getSign(documentAccountOrder.getFUser().getUserId()));
        documentAccountOrder.setFDate(DateUtil.getToday());
        documentAccountOrder.setFNext("0");
        documentAccountOrder.setFStatus("2");

        documentAccountOrder = documentAccountOrderService.save(documentAccountOrder);

        Document document = documentAccountOrder.getDocument();

        document.setFStatus("2");

        documentService.save(document);

        // 기안자에게 메일 전송
        sendMail(document.getAccount(), document, MailSendType.REJECT);
    }


    // 전자결재 메일 전송
    public void sendMail(Account account, Document document, MailSendType mailSendType) {

        Mail mail = new Mail();
        mail.setEmail(account.getEmail());      // Email
        mail.setObject(document.getTitle());      // Subject
        mail.setMessage(document.getContent());   // Content
        mailService.send(mail, mailSendType);
    }
}
