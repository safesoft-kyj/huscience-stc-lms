package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.Border;
import com.dtnsm.lms.domain.BorderFile;
import com.dtnsm.lms.domain.BorderViewAccount;
import com.dtnsm.lms.domain.ScheduleViewAccount;
import com.dtnsm.lms.service.BorderService;
import com.dtnsm.lms.service.ScheduleService;
import com.dtnsm.lms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleRestController {

    @Autowired
    BorderService borderService;

    @Autowired
    ScheduleService scheduleService;

    //
    @PostMapping("/ViewAccountList")
    public String borderViewAccountListPost(@RequestParam("scheduleId") long scheduleId){

        List<ScheduleViewAccount> scheduleViewAccounts = scheduleService.getAllScheduleAccountByScheduleId(scheduleId);
        if (scheduleViewAccounts.size() > 0) {
            StringBuilder sb = new StringBuilder();

            sb.append("<table>");

            for (ScheduleViewAccount viewAccount : scheduleViewAccounts) {
                sb.append("<tr>");
                sb.append("<td>" + viewAccount.getAccount().getName() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

            return sb.toString();
        } else {

            return "";
        }
    }

    @GetMapping("/ViewAccountList")
    public String borderViewAccountListGet(@RequestParam("scheduleId") long scheduleId){
        List<ScheduleViewAccount> scheduleViewAccounts = scheduleService.getAllScheduleAccountByScheduleId(scheduleId);

        if (scheduleViewAccounts.size() > 0) {
            StringBuilder sb = new StringBuilder();

            sb.append("<table class='table'>");
            sb.append("<tr>");
            sb.append("<th class='text-center'>#</th>");
            sb.append("<th class='text-center'>조회자</th>");
            sb.append("<th class='text-center'>조회일</th>");
            sb.append("</tr>");

            int totlaSize = scheduleViewAccounts.size();
            int i = 0;
            for (ScheduleViewAccount viewAccount : scheduleViewAccounts) {
                sb.append("<tr>");
                sb.append("<td class='text-center'>" + (totlaSize - i) + "</td>");
                sb.append("<td class='text-center'>" + viewAccount.getAccount().getName() + "</td>");
                sb.append("<td class='text-center'>" + DateUtil.getDateToString(viewAccount.getCreatedDate(), "yyyy-MM-dd HH:mm") + "</td>");
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
