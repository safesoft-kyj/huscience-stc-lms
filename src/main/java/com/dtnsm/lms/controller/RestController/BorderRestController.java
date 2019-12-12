package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.BorderViewAccount;
import com.dtnsm.lms.service.BorderService;
import com.dtnsm.lms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/border")
public class BorderRestController {

    @Autowired
    BorderService borderService;

    //
    @PostMapping("/borderViewAccountList")
    public String borderViewAccountListPost(@RequestParam("borderId") long borderId){

        List<BorderViewAccount> borderAccounts = borderService.getAllBorderAccountByBorderId(borderId);

        if (borderAccounts.size() > 0) {
            StringBuilder sb = new StringBuilder();

            sb.append("<table>");

            for (BorderViewAccount borderAccount : borderAccounts) {
                sb.append("<tr>");
                sb.append("<td>" + borderAccount.getAccount().getName() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

            return sb.toString();
        } else {

            return "";
        }
    }

    @GetMapping("/borderViewAccountList")
    public String borderViewAccountListGet(@RequestParam("borderId") long borderId){

        List<BorderViewAccount> borderAccounts = borderService.getAllBorderAccountByBorderId(borderId);

        if (borderAccounts.size() > 0) {
            StringBuilder sb = new StringBuilder();

            sb.append("<table class='table'>");
            sb.append("<tr>");
            sb.append("<th class='text-center'>#</th>");
            sb.append("<th class='text-center'>조회자</th>");
            sb.append("<th class='text-center'>조회일</th>");
            sb.append("</tr>");

            int totlaSize = borderAccounts.size();
            int i = 0;
            for (BorderViewAccount borderAccount : borderAccounts) {
                sb.append("<tr>");
                sb.append("<td class='text-center'>" + (totlaSize - i) + "</td>");
                sb.append("<td class='text-center'>" + borderAccount.getAccount().getName() + "</td>");
                sb.append("<td class='text-center'>" + DateUtil.getDateToString(borderAccount.getCreatedDate(), "yyyy-MM-dd HH:mm") + "</td>");
                sb.append("</tr>");

                i++;
            }
            sb.append("</table>");

            return sb.toString();
        } else {

            return "";
        }
    }
}
