package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.mybatis.dto.CalendarVO;
import com.dtnsm.lms.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/info/api")
public class CalendarRestController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CalendarRestController.class);

    @Autowired
    CourseService courseService;

    @GetMapping("/month/requestCalendar")
    public String monthRequestCalendar(@RequestParam("start") String start, @RequestParam("end") String end) {

        String jsonMsg = null;
        try {
            List<Course> courses = courseService.getCourseByRequestFromDateBetween(start, end);

            List<CalendarVO> events = new ArrayList<CalendarVO>();

            CalendarVO event;
            String color, url;
            for(Course course : courses) {

                url = "/course/view/" + course.getId();
                switch (course.getCourseMaster().getId()) {
                    case "BC0101":  // self training
                        color = "#FFA726";
                        break;
                    case "BC0102":  // class training
                        color = "#8BC34A";
                        break;
                    case "BC0103":  // 부서별 교육
                        color = "#26A69A";
                        break;
                    case "BC0104":  // 외부 교육
                        color = "#BA68C8";
                        break;
                    default:
                        color = "#ff0000";
                }

                event = new CalendarVO();
                event.setTitle(course.getTitle());
                event.setStart(course.getRequestFromDate());
                // allDay 가 true 인 경우 하루를 더해야 함
                if (event.isAllDay()) {
                    event.setEnd(DateUtil.getStringDateAddDay(course.getRequestToDate(), 1));
                } else {
                    event.setEnd(course.getRequestToDate());
                }
                event.setColor(color);
                event.setUrl(url);
                events.add(event);
        }

        // FullCalendar
            ObjectMapper mapper = new ObjectMapper();
            jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return jsonMsg;
    }

    @GetMapping("/month/calendar")
    public String monthCalendar(@RequestParam("start") String start, @RequestParam("end") String end) {

        String jsonMsg = null;
        try {
            List<Course> courses = courseService.getCourseByFromDateBetween(start, end);

            List<CalendarVO> events = new ArrayList<CalendarVO>();

            CalendarVO event;
            String color, url;
            for(Course course : courses) {

                url = "/course/view/" + course.getId();
                switch (course.getCourseMaster().getId()) {
                    case "BC0101":  // self training
                        color = "#FFA726";
                        break;
                    case "BC0102":  // class training
                        color = "#8BC34A";
                        break;
                    case "BC0103":  // 부서별 교육
                        color = "#26A69A";
                        break;
                    case "BC0104":  // 외부 교육
                        color = "#BA68C8";
                        break;
                    default:
                        color = "#ff0000";
                }

                event = new CalendarVO();
                event.setTitle(course.getTitle());
                event.setStart(course.getFromDate());
                // allDay 가 true 인 경우 하루를 더해야 함
                if (event.isAllDay()) {
                    event.setEnd(DateUtil.getStringDateAddDay(course.getToDate(), 1));
                } else {
                    event.setEnd(course.getToDate());
                }
                event.setColor(color);
                event.setUrl(url);
                events.add(event);
            }

            // FullCalendar
            ObjectMapper mapper = new ObjectMapper();
            jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return jsonMsg;
    }


    // Home 달력 교육요청시작일자로 요청일자 조회
    @GetMapping("/home/calendar")
    public List<String> homeCalendar(@RequestParam("start") String start, @RequestParam("end") String end) {

        List<String> dates = new ArrayList<String>();

        List<Course> courses = courseService.getCourseByRequestFromDateBetween(start, end);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outFormat = new SimpleDateFormat("MM/dd/yyyy");

        Date date = null;
        for(Course course : courses) {

            try {
                date = format.parse(course.getRequestFromDate());
                dates.add(outFormat.format(date));

//                date = format.parse(course.getRequestToDate());
//                dates.add(outFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    // Home 달력 교육요청시작일자로 데이터 조회
    @GetMapping("/home/calendarData")
    public List<String> homeCalendarData(@RequestParam("start") String start) {

        List<String> dates = new ArrayList<String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outFormat = new SimpleDateFormat("MM/dd/yyyy");

        String searchDate = "";
        try {
            Date date = outFormat.parse(start);
            searchDate = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Course> courses = courseService.getCourseByRequestFromDateBetween(searchDate, searchDate);

        Date date = null;
        StringBuilder sb;
        sb = new StringBuilder();

        sb.append("<table id=\"customer-list-page5\" class=\"table\" cellspacing=\"0\" width=\"100%\">");
        for(Course course : courses) {

            //sb.append("[" + course.getCourseMaster().getCourseName() + "]");
            sb.append("<tr><td>" + course.getRequestFromDate() + "</td><td><a href='/course/view/" + course.getId() + "'>");
            if (course.getTitle().length() > 30) {
                sb.append(course.getTitle().substring(0, 30) + "..</a></td>") ;
            } else {
                sb.append(course.getTitle() + "</a></td>");
            }
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        dates.add(sb.toString());
        return dates;

    }

}
