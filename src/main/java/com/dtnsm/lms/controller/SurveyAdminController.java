package com.dtnsm.lms.controller;

import com.dtnsm.lms.component.ExcelReader;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.FileUtil;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/survey")
public class SurveyAdminController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyFileService fileService;


    @Autowired
    ExcelReader excelReader;

    private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private PageInfo pageInfo = new PageInfo();

    public SurveyAdminController() {
        pageInfo.setParentId("m-course");
        pageInfo.setParentTitle("교육과정기준정보");
    }

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageId("m-survey-list");
        pageInfo.setPageTitle("설문");
        model.addAttribute(pageInfo);
        model.addAttribute("surveyList", surveyService.getList());

        return "admin/survey/list";
    }


    @GetMapping("/add")
    public String noticeAdd(Model model) {

        Survey survey = new Survey();

        pageInfo.setPageTitle("설문");

        model.addAttribute(pageInfo);
        model.addAttribute("survey", survey);

        return "admin/survey/add";
    }

    @PostMapping("/add-post")
    public String noticeAddPost(@Valid Survey survey
            , @RequestParam("file") MultipartFile file
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/survey/add";
        }

         // Section 저장
        Survey quiz1 = surveyService.saveSurvey(survey);

        // 문제파일을 업로드 하고 테이블에 insert 한다.
        if (file != null) {

            SurveyFile uploadFile = fileService.storeFile(file, quiz1);

            try {
                List<SurveyQuestion> courseQuizQuestions = excelReader.readFileToList(file, SurveyQuestion::fromQuiz);

                for (SurveyQuestion quizQuestion : courseQuizQuestions) {
                    // 시험문제와 연결한다.

                    if (!quizQuestion.getQuestion().isEmpty()) {
                        quizQuestion.setSurvey(quiz1);
                        surveyService.saveSurveyQuestion(quizQuestion);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin/survey/list";
    }

    @GetMapping("/edit/{id}")
    public String noticeEdit(@PathVariable("id") long id, Model model) {

        Survey survey = surveyService.getSurveyById(id);

        pageInfo.setPageTitle("설문");

        model.addAttribute(pageInfo);
        model.addAttribute("survey", survey);
        model.addAttribute("id", survey.getId());

        return "admin/survey/edit";
    }

    @PostMapping("/edit-post/{id}")
    public String noticeEditPost(@PathVariable("id") long id
            , @Valid Survey survey
            , @RequestParam("file") MultipartFile file
            , BindingResult result) {
        if(result.hasErrors()) {
            survey.setId(id);
            return "/admin/survey/list";
        }

        Survey oldSurvey = surveyService.getSurveyById(id);

        survey.setSurveyFiles(oldSurvey.getSurveyFiles());
        survey.setSurveyQuestions(oldSurvey.getSurveyQuestions());
        survey.setIsActive(oldSurvey.getIsActive());

        Survey survey1 = surveyService.saveSurvey(survey);

        // 문제파일을 업로드 하고 테이블에 insert 한다.
        if (file != null) {

            SurveyFile uploadFile = fileService.storeFile(file, survey1);

            try {
                List<SurveyQuestion> surveyQuestions = excelReader.readFileToList(file, SurveyQuestion::fromQuiz);

                for (SurveyQuestion quizQuestion : surveyQuestions) {
                    // 시험문제와 연결한다.
                    if (!quizQuestion.getQuestion().isEmpty()) {
                        quizQuestion.setSurvey(survey1);
                        surveyService.saveSurveyQuestion(quizQuestion);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin/survey/list";
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id") long id) {

        Survey survey = surveyService.getSurveyById(id);

        surveyService.deleteSurvey(survey);

        return "redirect:/admin/survey/list";
    }

    @GetMapping("/delete-file/{survey_id}/{file_id}")
    public String noticeDeleteFile(@PathVariable long survey_id, @PathVariable long file_id, HttpServletRequest request){

        // db및 파일 삭제
        fileService.deleteFile(file_id);

        return "redirect:/admin/survey/edit/" + survey_id;

    }

    @GetMapping("/download-file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable long id, HttpServletRequest request){

        SurveyFile surveyFile =  fileService.getUploadFile(id);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(surveyFile.getSaveName());

        // Try to determine file's content type
        String contentType = mimeTypesMap.getContentType(surveyFile.getSaveName());
        // contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());


        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 한글파일명 깨짐 현상 해소
        String newFileName = FileUtil.getNewFileName(request, surveyFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + "\"")
                .body(resource);
    }

    @GetMapping("/updateActive/{id}")
    public String updateActive(@PathVariable("id") long id) {

        // 모든 설문을 초기화 한다.
//        for(Survey survey1 : surveyService.getList()) {
//            survey1.setIsActive(0);
//            surveyService.saveSurvey(survey1);
//        }

        // 요청된 설문을 기본 설문으로 변경한다.
        Survey survey = surveyService.getSurveyById(id);

        if (survey.getIsActive() == 0) {
            // 교육과정을 신청할 수 있는 상태로 변경한다.
            survey.setIsActive(1);
        } else if (survey.getIsActive() == 1) {
            survey.setIsActive(0);
        }

        surveyService.saveSurvey(survey);

        return "redirect:/admin/survey/list/";
    }
}
