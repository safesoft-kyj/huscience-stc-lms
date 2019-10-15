package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/send/mail")
@CrossOrigin("*")
public class MailRestController {

    @Autowired
    private MailService mailService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> sendMail(@Valid @RequestBody Mail mail, Errors errors){
        if(errors.hasErrors()){
            return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return mailService.sendMail(mail);
    }

}