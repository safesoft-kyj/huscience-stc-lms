package com.dtnsm.lms.controller.RestController;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.service.SignatureService;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/login")
public class LoginExtensionController {

    @PostMapping("/extension")
    public boolean loginExtension(HttpSession session){

        System.out.println("getId : " + session.getId());
        System.out.println("isNew : " + session.isNew());
        System.out.println("getCreationTime : " + session.getCreationTime());
        System.out.println("getLastAccessedTime : " + session.getLastAccessedTime());
        System.out.println("getMaxInactiveInterval : " + session.getMaxInactiveInterval());

        return true;
    }
}
