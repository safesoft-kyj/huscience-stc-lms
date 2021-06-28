package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.Document;
import com.dtnsm.lms.domain.DocumentAccountOrder;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.LmsAlarmCourseType;
import com.dtnsm.lms.domain.constant.LmsAlarmGubun;
import com.dtnsm.lms.domain.datasource.MessageSource;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.MessageUtil;
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
    CourseAccountService courseAccountService;

    @Autowired
    private MailService mailService;

    @Autowired
    SignatureService signatureService;

    @Autowired
    CourseCertificateService courseCertificateService;

    @Autowired
    BinderLogService binderLogService;

    // 기안 처리
    public boolean documentRequestProcess(Account account, Document document) {

        // 결재 신청에 필요한 기본 검증을 진행한다.
        // 0: 계정이 존재하지 않음
        // 1: 상위결재권자가 지정되지 않음
        // 2: 관리자가 지정되지 않음
        // 9: 과정 신청 가능
        if (courseAccountService.accountVerification(account.getUserId()) != 9) return false;

        // 팀장/부서장 승인여부
        String isAppr1 = document.getTemplate().getIsTeamMangerApproval();
        // 관리자 승인여부
        String isAppr2 = document.getTemplate().getIsCourseMangerApproval();


        // 기안
        document.setAccount(account);
        document.setRequestDate(DateUtil.getTodayString());
        document.setFnWdate(DateUtil.getToday());
        document.setFnStatus("0");
        document.setFnCurrSeq(1);

        // 결재자수 Max 설정
        if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
            document.setFnFinalCount(2);
        } else if(isAppr1.equals("Y")) {
            document.setFnFinalCount(1);
        } else {
            document.setFnFinalCount(0);
            document.setFnStatus("1");      // 전자결재가 없으면 완료한것으로 처리한다.
        }

        Document saveDocument = documentService.save(document);

        // 내결재사항을 추가한다.
        DocumentAccountOrder documentAccountOrder = new DocumentAccountOrder();
        documentAccountOrder.setFnUser(account);
        documentAccountOrder.setSignature(signatureService.getSign(account.getUserId()));
        documentAccountOrder.setDocument(saveDocument);

        // fKind : 0:초기, 1:결재(팀장), 2. 합의(관리자)
        documentAccountOrder.setFnKind("1");
        documentAccountOrder.setFnStatus("1");
        documentAccountOrder.setFnSeq(0);
        documentAccountOrder.setFnNext("0");
        documentAccountOrder.setFnDate(DateUtil.getToday());
        documentAccountOrder.setFnComment("");
        documentAccountOrderService.save(documentAccountOrder);


        if (isAppr1.equals("Y")) {
            Account apprAccount2 = userService.getAccountByUserId(account.getParentUserId());
            DocumentAccountOrder documentAccountOrder2 = new DocumentAccountOrder();
            documentAccountOrder2.setFnUser(apprAccount2);
            documentAccountOrder2.setDocument(saveDocument);
            // fKind : 0:초기, 1:결재(팀장), 2. 합의(관리자)
            documentAccountOrder2.setFnKind("1");
            documentAccountOrder2.setFnStatus("0");
            documentAccountOrder2.setFnSeq(1);
            documentAccountOrder2.setFnNext("1");

            documentAccountOrderService.save(documentAccountOrder2);

            // 1차 결재자 메일 전송
//            MessageUtil.sendMail(LmsAlarmCourseType.Request, apprAccount2, document);

            MessageSource messageSource = MessageSource.builder()
                    .alarmGubun(LmsAlarmGubun.INFO)
                    .lmsAlarmCourseType(LmsAlarmCourseType.DocApproval)
                    .sender(account)
                    .receive(apprAccount2)
                    .course(saveDocument.getCourseAccount().getCourse())
                    .document(saveDocument)
                    .title(String.format("[LMS/교육참석보고서/%s] %s 결재 요청", account.getName(), saveDocument.getCourseAccount().getCourse().getTitle()))
                    .subject(String.format("[LMS/교육참석보고서/%s] %s 결재 요청", account.getName(), saveDocument.getCourseAccount().getCourse().getTitle()))
                    .content("")
                    .build();

            MessageUtil.sendNotificationMessage(messageSource, true);
        }

        if (isAppr2.equals("Y")) {
            Account apprAccount3 = userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId());
            DocumentAccountOrder documentAccountOrder3 = new DocumentAccountOrder();
            documentAccountOrder3.setFnUser(apprAccount3);
            documentAccountOrder3.setDocument(saveDocument);
            // fKind : 0:초기, 1:결재(팀장), 2. 합의(관리자)
            documentAccountOrder3.setFnKind("2");
            documentAccountOrder3.setFnStatus("0");
            documentAccountOrder3.setFnSeq(2);

            documentAccountOrderService.save(documentAccountOrder3);


            // TODO : 전자결재 생성시 추가로 해야할 사항
        }

        // 알림 등록

        return true;
    }

    // 1차 승인 처리
    public boolean documentApproval1Proces(DocumentAccountOrder documentAccountOrder) {

        int finalCount = documentAccountOrder.getDocument().getFnFinalCount();

        documentAccountOrder.setSignature(signatureService.getSign(documentAccountOrder.getFnUser().getUserId()));
        documentAccountOrder.setFnDate(DateUtil.getToday());
        documentAccountOrder.setFnNext("0");
        documentAccountOrder.setFnStatus("1");
        documentAccountOrder.setFnComment("");

        documentAccountOrder = documentAccountOrderService.save(documentAccountOrder);

        Document document = documentAccountOrder.getDocument();

        if(finalCount == documentAccountOrder.getFnSeq()) {   // 종결처리
            //  승인: 1, 기각 : 2
            document.setFnStatus("1");    // 0 진행중, 1:승인, 2:기각
            document.setIsCommit("1");   // 0 진행중 1:종결

            Document document1 = documentService.save(document);

            // 교육참석보고서 이면 연결된 교육과정을 종결시킨다.
            // TODO : 교육참석보고서 ID가 1에서 다르게 변경될 경우 수정 요함
            if(document1.getTemplate().getId() == 1 && document1.getCourseAccount() != null) {

                CourseAccount courseAccount = document1.getCourseAccount();
                courseAccount.setCourseStatus(CourseStepStatus.complete);
                courseAccount.setIsAttendance("1");
                courseAccount.setIsCommit("1");
                courseAccount.setReportStatus("1");

                if (courseAccount.getCourse().getIsCerti().equals("Y")) {
                    String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
                    courseAccount.setCertificateNo(certificateNo);
                } else {
                    courseAccount.setCertificateNo("");
                }

                courseAccountService.save(courseAccount);

                // TODO : Training Log 처리
                // 강의별로 로그를 생성시킨다.
                binderLogService.createTrainingLog(courseAccount);

                //            // 최종 승인이면 기안자에게 메일 전송
//                MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Approval, document.getAccount(), document1);
//
//            // 최종 승인이면 관리자에게 메일 전송
//            MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Approval, courseManagerService.getCourseManager().getAccount(), document1);

                String subject = String.format("[LMS/교육참석보고서/승인] %s", document1.getCourseAccount().getCourse().getTitle());


                // 최종 승인이면 기안자에게 메일 전송
                MessageSource messageSource = MessageSource.builder()
                        .alarmGubun(LmsAlarmGubun.INFO)
                        .lmsAlarmCourseType(LmsAlarmCourseType.DocApprovalTeam)
                        .sender(documentAccountOrder.getFnUser()) // 승인자
                        .receive(document1.getAccount())
                        .course(document1.getCourseAccount().getCourse())
                        .document(document1)
                        .title(subject)
                        .subject(subject)
                        .content("")
                        .build();

                MessageUtil.sendNotificationMessage(messageSource, true);

                // 관리자에게 메일 전송
                messageSource = MessageSource.builder()
                        .alarmGubun(LmsAlarmGubun.INFO)
                        .lmsAlarmCourseType(LmsAlarmCourseType.DocApprovalTeam)
                        .sender(documentAccountOrder.getFnUser()) // 승인자
                        .receive(courseManagerService.getCourseManager().getAccount())
                        .course(document1.getCourseAccount().getCourse())
                        .document(document1)
                        .title(subject)
                        .subject(subject)
                        .content("")
                        .build();

                MessageUtil.sendNotificationMessage(messageSource, true);
            }

        } else {
            DocumentAccountOrder nextOrder = documentAccountOrderService.getByFnoAndSeq(documentAccountOrder.getDocument().getId(), documentAccountOrder.getFnSeq()+1);
            if (nextOrder != null) {

                nextOrder.setFnNext("1");
                nextOrder = documentAccountOrderService.save(nextOrder);

                // 마스터에 다음 결재자 순번을 업데이트 한다.
                document.setFnCurrSeq(nextOrder.getFnSeq());
                documentService.save(document);

//                MessageUtil.sendMail(LmsAlarmCourseType.Approval, nextOrder.getFnUser(), document);

                String subject = String.format("[LMS/교육참석보고서/승인] %s", document.getCourseAccount().getCourse().getTitle());

                MessageSource messageSource = MessageSource.builder()
                        .alarmGubun(LmsAlarmGubun.INFO)
                        .lmsAlarmCourseType(LmsAlarmCourseType.DocApprovalTeam)
                        .sender(documentAccountOrder.getFnUser()) // 승인자
                        .receive(nextOrder.getFnUser())
                        .course(document.getCourseAccount().getCourse())
                        .document(document)
                        .title(subject)
                        .subject(subject)
                        .content("")
                        .build();

                MessageUtil.sendNotificationMessage(messageSource, true);
            }
        }

        return true;
    }

    // 전자결재 1차 기각 처리
    public void documentReject1Proces(DocumentAccountOrder documentAccountOrder) {

        int finalCount = documentAccountOrder.getDocument().getFnFinalCount();

        documentAccountOrder.setSignature(signatureService.getSign(documentAccountOrder.getFnUser().getUserId()));
        documentAccountOrder.setFnDate(DateUtil.getToday());
        documentAccountOrder.setFnNext("0");
        documentAccountOrder.setFnStatus("2");

        documentAccountOrder = documentAccountOrderService.save(documentAccountOrder);

        Document document = documentAccountOrder.getDocument();

        document.setFnStatus("2");

        document = documentService.save(document);


        // 교육과정 참석보고서 작성 가능 상태로 변경
        CourseAccount courseAccount = document.getCourseAccount();
        courseAccount.setReportStatus("9");
        courseAccountService.save(courseAccount);

//
//        // 기안자에게 메일 전송
//        MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Reject, document.getAccount(), document);
//
//        // 관리자에게 메일 전송
//        MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Reject, courseManagerService.getCourseManager().getAccount(), document);

        // 기안자에게 메일 전송
        MessageSource messageSource = MessageSource.builder()
                .alarmGubun(LmsAlarmGubun.INFO)
                .lmsAlarmCourseType(LmsAlarmCourseType.DocReject)
                .sender(documentAccountOrder.getFnUser()) // 승인자
                .receive(document.getAccount())
                .course(document.getCourseAccount().getCourse())
                .document(document)
                .title(String.format("[LMS/교육참석보고서/반려] %s", document.getCourseAccount().getCourse().getTitle()))
                .subject(String.format("[LMS/교육참석보고서/반려] %s", document.getCourseAccount().getCourse().getTitle()))
                .content("")
                .build();

        MessageUtil.sendNotificationMessage(messageSource, true);

        // 관리자에게 메일 전송
//        messageSource = MessageSource.builder()
//                .alarmGubun(LmsAlarmGubun.INFO)
//                .lmsAlarmCourseType(LmsAlarmCourseType.Reject)
//                .sender(documentAccountOrder.getFnUser()) // 승인자
//                .receive(courseManagerService.getCourseManager().getAccount())
//                .document(document)
//                .title("교육참석보고서 반려")
//                .subject(String.format("%s님의 %s 반려 되었습니다.", document.getAccount().getName(), document.getTitle()))
//                .content("교육참석보고서 반려")
//                .build();
//
//        MessageUtil.sendNotificationMessage(messageSource, true);
    }

    // 전자결재 2차 승인 처리
    public void documentApproval2Proces(DocumentAccountOrder documentAccountOrder) {

        int finalCount = documentAccountOrder.getDocument().getFnFinalCount();

        documentAccountOrder.setSignature(signatureService.getSign(documentAccountOrder.getFnUser().getUserId()));
        documentAccountOrder.setFnDate(DateUtil.getToday());
        documentAccountOrder.setFnNext("0");
        documentAccountOrder.setFnStatus("1");
        documentAccountOrder.setFnComment("");

        documentAccountOrder = documentAccountOrderService.save(documentAccountOrder);

        Document document = documentAccountOrder.getDocument();

        if(finalCount == documentAccountOrder.getFnSeq()) {   // 종결처리
            //  승인: 1, 기각 : 2
            document.setFnStatus("1");
            document.setIsCommit("1");   // 0 진행중 1:종결

            Document document1 = documentService.save(document);

            // 교육참석보고서 이면 연결된 교육과정을 종결시킨다.
            // TODO : 교육참석보고서 ID가 다르게 변경될 경우 수정 요함
            if(document1.getTemplate().getId() == 1 && document1.getCourseAccount() != null) {

                CourseAccount courseAccount = document1.getCourseAccount();

                if (courseAccount.getCourse().getIsCerti().equals("Y")) {
                    String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
                    if(certificateNo.isEmpty()){
                        return;
                    }else{
                        courseAccount.setCertificateNo(certificateNo);
                    }
                } else {
                    courseAccount.setCertificateNo("");
                }

                courseAccount.setCourseStatus(CourseStepStatus.complete);
                courseAccount.setIsAttendance("1");
                courseAccount.setIsCommit("1");
                courseAccount.setReportStatus("1");

                courseAccountService.save(courseAccount);

                // TODO : Training Log 처리
                // 강의별로 로그를 생성시킨다.
                binderLogService.createTrainingLog(courseAccount);

                String subject = String.format("[LMS/교육참석보고서/승인] %s", document1.getCourseAccount().getCourse().getTitle());

                // 기안자에게 메일 전송
                MessageSource messageSource = MessageSource.builder()
                        .alarmGubun(LmsAlarmGubun.INFO)
                        .lmsAlarmCourseType(LmsAlarmCourseType.DocApprovalManger)
                        .sender(documentAccountOrder.getFnUser()) // 승인자
                        .receive(document1.getAccount())
                        .course(document1.getCourseAccount().getCourse())
                        .document(document1)
                        .title(subject)
                        .subject(subject)
                        .content("")
                        .build();

                MessageUtil.sendNotificationMessage(messageSource, true);

                // 관리자에게 메일 전송
//                messageSource = MessageSource.builder()
//                        .alarmGubun(LmsAlarmGubun.INFO)
//                        .lmsAlarmCourseType(LmsAlarmCourseType.do)
//                        .sender(documentAccountOrder.getFnUser()) // 승인자
//                        .receive(courseManagerService.getCourseManager().getAccount())
//                        .document(document1)
//                        .title(subject)
//                        .subject(subject)
//                        .content("")
//                        .build();

//                MessageUtil.sendNotificationMessage(messageSource, true);
            }

//            // 최종 승인이면 기안자에게 메일 전송
//            MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Approval, document.getAccount(), document1);
//
//            // 관리자에게 메일 전송
//            MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Approval, courseManagerService.getCourseManager().getAccount(), document1);

        } else {
            DocumentAccountOrder nextOrder = documentAccountOrderService.getByFnoAndSeq(documentAccountOrder.getDocument().getId(), documentAccountOrder.getFnSeq()+1);
            if (nextOrder != null) {

                nextOrder.setFnNext("1");
                nextOrder = documentAccountOrderService.save(nextOrder);

                // 마스터에 다음 결재자 순번을 업데이트 한다.
                document.setFnCurrSeq(nextOrder.getFnSeq());
                documentService.save(document);

                // 알람 및 메일 전송
//                MessageUtil.sendMail(LmsAlarmCourseType.Approval, nextOrder.getFnUser(), document);

                String subject = String.format("[LMS/교육참석보고서/승인] %s", document.getCourseAccount().getCourse().getTitle());

                MessageSource messageSource = MessageSource.builder()
                        .alarmGubun(LmsAlarmGubun.INFO)
                        .lmsAlarmCourseType(LmsAlarmCourseType.DocApprovalManger)
                        .sender(documentAccountOrder.getFnUser()) // 승인자
                        .receive(nextOrder.getFnUser())
                        .course(document.getCourseAccount().getCourse())
                        .document(document)
                        .title(subject)
                        .subject(subject)
                        .content("")
                        .build();

                MessageUtil.sendNotificationMessage(messageSource, true);
            }
        }
    }

    // 전자결재 2차 기각 처리(참석보고서는 관리자 승인이 없음으로 사용되지 않음)
    public void documentReject2Proces(DocumentAccountOrder documentAccountOrder) {

        int finalCount = documentAccountOrder.getDocument().getFnFinalCount();

        documentAccountOrder.setSignature(signatureService.getSign(documentAccountOrder.getFnUser().getUserId()));
        documentAccountOrder.setFnDate(DateUtil.getToday());
        documentAccountOrder.setFnNext("0");
        documentAccountOrder.setFnStatus("2");

        documentAccountOrder = documentAccountOrderService.save(documentAccountOrder);

        Document document = documentAccountOrder.getDocument();

        document.setFnStatus("2");

        documentService.save(document);

//        // 기안자에게 메일 전송
//        MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Reject, document.getAccount(), document);
//
//        // 관리자에게 메일 전송
//        MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Reject, courseManagerService.getCourseManager().getAccount(), document);

        // 기안자에게 메일 전송
        MessageSource messageSource = MessageSource.builder()
                .alarmGubun(LmsAlarmGubun.INFO)
                .lmsAlarmCourseType(LmsAlarmCourseType.DocReject)
                .sender(documentAccountOrder.getFnUser()) // 승인자
                .receive(document.getAccount())
                .course(document.getCourseAccount().getCourse())
                .document(document)
                .title(String.format("[LMS/교육참석보고서/반려] %s", document.getCourseAccount().getCourse().getTitle()))
                .subject(String.format("[LMS/교육참석보고서/반려] %s", document.getCourseAccount().getCourse().getTitle()))
                .content("")
                .build();

        MessageUtil.sendNotificationMessage(messageSource, true);

        // 관리자에게 메일 전송
//        messageSource = MessageSource.builder()
//                .alarmGubun(LmsAlarmGubun.INFO)
//                .lmsAlarmCourseType(LmsAlarmCourseType.Reject)
//                .sender(documentAccountOrder.getFnUser()) // 승인자
//                .receive(courseManagerService.getCourseManager().getAccount())
//                .document(document)
//                .title("교육참석보고서 반려")
//                .subject(String.format("%s님의 %s 반려 되었습니다.", document.getAccount().getName(), document.getTitle()))
//                .content("교육참석보고서 반려")
//                .build();
//
//        MessageUtil.sendNotificationMessage(messageSource, true);
    }


    // 전자결재 메일 전송
//    public void sendMail(Account account, Document document, MailSendType mailSendType) {
//
//        Mail mail = new Mail();
//        mail.setEmail(account.getEmail());      // Email
//        mail.setObject(document.getTitle());      // Subject
//        mail.setMessage(document.getContent());   // Content
//        mailService.send(mail, mailSendType);
//    }
}
