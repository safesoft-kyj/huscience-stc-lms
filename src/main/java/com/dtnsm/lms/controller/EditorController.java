package com.dtnsm.lms.controller;


import com.dtnsm.lms.domain.Editor;
import com.dtnsm.lms.properties.FileUploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Controller
@Slf4j
public class EditorController {

    @Autowired
    FileUploadProperties prop;

    @RequestMapping("/file_uploader")
    public String file_uploader(HttpServletRequest request, HttpServletResponse response, Editor editor){
        String return1=request.getParameter("callback");
        String return2="?callback_func=" + request.getParameter("callback_func");
        String return3="";
        String name = "";
        try {
            if(editor.getFiledata() != null && editor.getFiledata().getOriginalFilename() != null && !editor.getFiledata().getOriginalFilename().equals("")) {
                // 기존 상단 코드를 막고 하단코드를 이용
                name = editor.getFiledata().getOriginalFilename().substring(editor.getFiledata().getOriginalFilename().lastIndexOf(File.separator)+1);
                String filename_ext = name.substring(name.lastIndexOf(".")+1);
                filename_ext = filename_ext.toLowerCase();
                String[] allow_file = {"jpg","png","bmp","gif"};
                int cnt = 0;
                for(int i=0; i<allow_file.length; i++) {
                    if(filename_ext.equals(allow_file[i])){
                        cnt++;
                    }
                }
                if(cnt == 0) {
                    return3 = "&errstr="+name;
                } else {
                    //파일 기본경로
//                    String dftFilePath = request.getSession().getServletContext().getRealPath("/");
                    //파일 기본경로 _ 상세경로
//                    String filePath = dftFilePath + "resources"+ File.separator + "editor" + File.separator +"upload" + File.separator;

                    String filePath = prop.getBorderPhotoUploadDir();
                    File file = new File(filePath);
                    if(!file.exists()) {
                        file.mkdirs();
                    }
                    String realFileNm = "";
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                    String today= formatter.format(new java.util.Date());
                    realFileNm = today+ UUID.randomUUID().toString() + name.substring(name.lastIndexOf("."));
                    String rlFileNm = filePath + realFileNm;
                    ///////////////// 서버에 파일쓰기 /////////////////
                    editor.getFiledata().transferTo(new File(rlFileNm));
                    ///////////////// 서버에 파일쓰기 /////////////////
                    return3 += "&bNewLine=true";
                    return3 += "&sFileName="+ name;
                    return3 += "&sFileURL=/photoFile/"+realFileNm;
//                    return3 += "&sFileURL=/resources/editor/upload/"+realFileNm;
                }
            }else {
                return3 += "&errstr=error";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:"+return1+return2+return3;
    }

    @GetMapping("/photoFile/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request){

        //Mime 구하기
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String contentType = mimeTypesMap.getContentType(filename);

        // Fallback to the default content type if type could not be determined
        if(contentType.equals("")) {
            contentType = "application/octet-stream";
        }

        // 다운로드 파일 경로
        Path fileLocation = Paths.get(prop.getBorderPhotoUploadDir()).toAbsolutePath().normalize();
        Path filePath = fileLocation.resolve(filename).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }


    @RequestMapping("/file_uploader_html5")
    public void file_uploader_html5(HttpServletRequest request, HttpServletResponse response){


        try {
            //파일정보
            String sFileInfo = "";
            //파일명을 받는다 - 일반 원본파일명


            String filename = "test.png";
//            String filename = request.getHeader("file-name");
            //String filename = URLDecoder.decode(request.getHeader("file-name"), "UTF-8");

            //파일 확장자
//            String filename_ext = filename.substring(filename.lastIndexOf(".")+1);



            //확장자를소문자로 변경
//            filename_ext = filename_ext.toLowerCase();

            //이미지 검증 배열변수
            String[] allow_file = {"jpg","png","bmp","gif"};

            //돌리면서 확장자가 이미지인지
            int cnt = 1;
//            for(int i=0; i<allow_file.length; i++) {
//                if(filename_ext.equals(allow_file[i])){
//                    cnt++;
//                }
//            }

            //이미지가 아님
            if(cnt == 0) {
                PrintWriter print = response.getWriter();
                print.print("NOTALLOW_"+filename);
                print.flush();
                print.close();
            } else {
                //이미지이므로 신규 파일로 디렉토리 설정 및 업로드
                //파일 기본경로
//                String dftFilePath = request.getSession().getServletContext().getRealPath("/");
                //파일 기본경로 _ 상세경로
//                String filePath = dftFilePath + "resources" + File.separator + "editor" + File.separator +"multiupload" + File.separator;

                String filePath = prop.getBorderPhotoUploadDir();
                File file = new File(filePath);
                if(!file.exists()) file.mkdirs();
                String realFileNm = "";
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String today= formatter.format(new java.util.Date());
                realFileNm = today+UUID.randomUUID().toString() + filename.substring(filename.lastIndexOf("."));
                String rlFileNm = filePath + realFileNm;
                ///////////////// 서버에 파일쓰기 /////////////////
                InputStream is1 = request.getInputStream();
                OutputStream os1=new FileOutputStream(rlFileNm);
                int numRead;
                byte b[] = new byte[1000]; //[Integer.parseInt(request.getHeader("file-size"))];
                while((numRead = is1.read(b,0,b.length)) != -1){
                    os1.write(b,0,numRead);
                }
                if(is1 != null) {
                    is1.close();
                }
                os1.flush();
                os1.close();
                ///////////////// 서버에 파일쓰기 /////////////////

                // 정보 출력
                sFileInfo += "&bNewLine=true";
                // img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
                sFileInfo += "&sFileName="+ filename;;
                sFileInfo += "&sFileURL="+"/photoFile/"+realFileNm;
                PrintWriter print = response.getWriter();
                print.print(sFileInfo);
                print.flush();
                print.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
