package com.dtnsm.lms.controller.RestController;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.mybatis.dto.DepartTreeVO;
import com.dtnsm.lms.mybatis.service.DepartMapperService;
import com.dtnsm.lms.service.SignatureService;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sign")
public class SignRestController {

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private SignatureService signatureService;

    @PostMapping("/save")
    public boolean signSave(@RequestParam(value="sign",  defaultValue = "") String sign){

       if (!sign.isEmpty())  {
           Signature signature = signatureService.getSignature(SessionUtil.getUserId());

           signature.setBase64signature(sign);
           signatureRepository.save(signature);
           return true;
       }
        return false;
    }
}
