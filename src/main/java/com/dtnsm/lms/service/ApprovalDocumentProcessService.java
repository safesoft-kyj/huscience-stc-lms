package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import com.dtnsm.lms.domain.constant.MailSendType;
import com.dtnsm.lms.domain.constant.SurveyStatusType;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApprovalDocumentProcessService {

    @Autowired
    DocumentAccountService documentAccountService;

    @Autowired
    CourseAccountRepository courseAccountRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CourseManagerService courseManagerService;

    @Autowired
    private MailService mailService;

    @Autowired CourseAccountService courseAccountService;

    // 기안 처리
    public void documentRequestProcess(Account account, Document document) {

        DocumentAccount documentAccount = new DocumentAccount();
        documentAccount.setDocument(document);
        documentAccount.setAccount(account);
        documentAccount.setRequestDate(DateUtil.getTodayString());
        documentAccount.setRequestType(CourseRequestType.SPECIFY);
        documentAccount.setApprUserId1(userService.getAccountByUserId(account.getParentUserId()));
        documentAccount.setApprUserId2(userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId()));
        documentAccount.setIsTeamMangerApproval(document.getTemplate().getIsTeamMangerApproval());
        documentAccount.setIsCourseMangerApproval(document.getTemplate().getIsCourseMangerApproval());

        // 기본상태 지정
        documentAccount.setStatus(ApprovalStatusType.REQUEST_DONE);   // 결재상태
        documentAccount.setIsCommit("0"); // 결재 승인 프로세스 완료 상태
        documentAccount.setIsAppr1("N");  // 1차 결재 완료 여부
        documentAccount.setIsAppr2("N");  // 2차 결재 완료 여부

        // 1차 팀장 결재 유무
        if (document.getTemplate().getIsTeamMangerApproval().equals("N") && document.getTemplate().getIsCourseMangerApproval().equals("N")) {
            documentAccount.setStatus(ApprovalStatusType.APPROVAL_MANAGER_DONE);    // 팀장승인완료
            documentAccount.setIsAppr1("Y");
            documentAccount.setIsAppr2("Y");
            documentAccount.setIsCommit("1");
        }

        // 과정 결재 정보 생성
        DocumentAccount documentAccount1 = documentAccountService.save(documentAccount);


        // 1차 결재자 메일 전송
        if(documentAccount1.getIsTeamMangerApproval().equals("Y") && documentAccount1.getApprUserId1() != null) {
            sendMail(documentAccount1.getApprUserId1(), documentAccount1.getDocument(), MailSendType.REQUEST);
        }

        // 알림 등록
    }

    // 전자결재 1차 승인 처리
    public void documentApproval1Proces(DocumentAccount documentAccount) {

        if(documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getIsAppr1().equals("N") && documentAccount.getIsCommit().equals("0")) {

            documentAccount.setIsAppr1("Y");
            documentAccount.setApprDate1(DateUtil.getTodayString());
            documentAccount.setApprDateTime1(new Date());
            documentAccount.setStatus(ApprovalStatusType.APPROVAL_TEAM_DONE);

            // 최종승인이 팀장인 경우 상태를 종결한다.
            if (documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getIsCourseMangerApproval().equals("N"))
                documentAccount.setIsCommit("1");

            documentAccount = documentAccountService.save(documentAccount);
        }

        // 2차 결재자 메일 전송
        if(documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getApprUserId2() != null) {
            sendMail(documentAccount.getApprUserId2(), documentAccount.getDocument(), MailSendType.REQUEST);
        }

        // 최종 승인이면 기안자에게 메일 전송
        if(documentAccount.getIsCommit().equals("Y") && documentAccount.getAccount() != null) {
            sendMail(documentAccount.getAccount(), documentAccount.getDocument(), MailSendType.REQUEST);
        }
    }

    // 전자결재 1차 기각 처리
    public void documentReject1Proces(DocumentAccount documentAccount) {

        if(documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getIsAppr1().equals("N") && documentAccount.getIsCommit().equals("0")) {

            documentAccount.setIsAppr1("Y");
            documentAccount.setApprDate1(DateUtil.getTodayString());
            documentAccount.setApprDateTime1(new Date());
            documentAccount.setStatus(ApprovalStatusType.APPROVAL_TEAM_REJECT);
            documentAccount.setIsCommit("1"); // 2차 승인이 진행되지 않게 완료 처리한다.

            documentAccountService.save(documentAccount);
        }

        // 2차 결재자 메일 전송
        if(documentAccount.getIsTeamMangerApproval().equals("Y") && documentAccount.getApprUserId2() != null) {
            sendMail(documentAccount.getApprUserId2(), documentAccount.getDocument(), MailSendType.REQUEST);
        }

        // 최종 승인이면 기안자에게 메일 전송
        if(documentAccount.getIsCommit().equals("Y") && documentAccount.getAccount() != null) {
            sendMail(documentAccount.getAccount(), documentAccount.getDocument(), MailSendType.REQUEST);
        }
    }

    // 전자결재 2차 승인 처리
    public void documentApproval2Proces(DocumentAccount documentAccount) {

        // 2차승인여부가 Y이고 아직 미승인된 경우만 처리
        if(documentAccount.getIsCourseMangerApproval().equals("Y") && documentAccount.getIsAppr2().equals("N")) {
            documentAccount.setIsAppr2("Y");
            documentAccount.setApprDate2(DateUtil.getTodayString());
            documentAccount.setApprDateTime2(new Date());
            documentAccount.setStatus(ApprovalStatusType.APPROVAL_TEAM_DONE);
            documentAccount.setIsCommit("1");

            documentAccountService.save(documentAccount);
        }

        // 최종 승인이면 기안자에게 메일 전송
        if(documentAccount.getIsCommit().equals("Y") && documentAccount.getAccount() != null) {
            sendMail(documentAccount.getAccount(), documentAccount.getDocument(), MailSendType.REQUEST);
        }
    }

    // 전자결재 2차 기각 처리
    public void documentReject2Proces(DocumentAccount documentAccount) {

        // 2차승인여부가 Y이고 아직 미승인된 경우만 처리
        if(documentAccount.getIsCourseMangerApproval().equals("Y") && documentAccount.getIsAppr2().equals("N")) {
            documentAccount.setIsAppr2("Y");
            documentAccount.setApprDate2(DateUtil.getTodayString());
            documentAccount.setApprDateTime2(new Date());
            documentAccount.setStatus(ApprovalStatusType.APPROVAL_MANAGER_REJECT);
            documentAccount.setIsCommit("1");

            documentAccountService.save(documentAccount);
        }

        // 최종 승인이면 기안자에게 메일 전송
        if(documentAccount.getIsCommit().equals("Y") && documentAccount.getAccount() != null) {
            sendMail(documentAccount.getAccount(), documentAccount.getDocument(), MailSendType.REQUEST);
        }
    }


    // 전자결재 메일 전송
    public void sendMail(Account account, Document document, MailSendType mailSendType) {

        Mail mail = new Mail();
        mail.setEmail(account.getEmail());      // Email
        mail.setObject(document.getTitle());      // Subject
        mail.setMessage(document.getContent());   // Content
        mailService.send(mail, MailSendType.REQUEST);
    }
}
