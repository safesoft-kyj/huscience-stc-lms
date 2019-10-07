<?php
if (!defined('_GNUBOARD_')) exit; // 개별 페이지 접근 불가
/**
 Made By Vorfeed 
 문의사항 Sir.kr
**/
function editor_html($id, $content, $is_dhtml_editor=true)
{
    global $g5, $config, $w, $board, $write;
    static $js = true;

    if( 
        $is_dhtml_editor && $content && 
        (
        (!$w && (isset($board['bo_insert_content']) && !empty($board['bo_insert_content'])))
        || ($w == 'u' && isset($write['wr_option']) && strpos($write['wr_option'], 'html') === false )
        )
    ){       //글쓰기 기본 내용 처리
        if( preg_match('/\r|\n/', $content) && $content === strip_tags($content, '<a><strong><b>') ) {  //textarea로 작성되고, html 내용이 없다면
            $content = nl2br($content);
        }
    }

    $editor_url = G5_EDITOR_URL.'/'.$config['cf_editor'];

    $html = "";
    $html .= "<span class=\"sound_only\">웹에디터 시작</span>";
    if ($is_dhtml_editor && $js) {
        //$html .= "\n".'<script>var g5_editor_url = "'.$editor_url.'";</script>';
        $html .= "\n".'<script src="'.$editor_url.'/bower_components/jquery/dist/jquery.js"></script>';
        $html .= "\n".'<script src="'.$editor_url.'/bower_components/tui-code-snippet/dist/tui-code-snippet.js"></script>';
        $html .= "\n".'<script src="'.$editor_url.'/bower_components/markdown-it/dist/markdown-it.js"></script>';
        $html .= "\n".'<script src="'.$editor_url.'/bower_components/to-mark/dist/to-mark.js"></script>';
        $html .= "\n".'<script src="'.$editor_url.'/bower_components/codemirror/lib/codemirror.js"></script>';
        $html .= "\n".'<script src="'.$editor_url.'/bower_components/highlightjs/highlight.pack.js"></script>';
        $html .= "\n".'<script src="'.$editor_url.'/bower_components/squire-rte/build/squire-raw.js"></script>';
        $html .= "\n".'<script src="'.$editor_url.'/bower_components/tui-editor/dist/tui-editor-Editor-all.min.js"></script>';
        $html .= "\n".'<link rel="stylesheet" href="'.$editor_url.'/bower_components/codemirror/lib/codemirror.css">';
        $html .= "\n".'<link rel="stylesheet" href="'.$editor_url.'/bower_components/highlightjs/styles/github.css">';
        $html .= "\n".'<link rel="stylesheet" href="'.$editor_url.'/bower_components/tui-editor/dist/tui-editor.css">';
        $html .= "\n".'<link rel="stylesheet" href="'.$editor_url.'/bower_components/tui-editor/dist/tui-editor-contents.css">';
        $js = false;
    }
    $tuieditor_class = $is_dhtml_editor ? "tuieditor" : "";
    $html .= "\n"."<div id='tui-editor'></div>";
    $html .= "\n"."<textarea id=\"$id\" name=\"$id\" class=\"$tuieditor\" maxlength=\"65536\" style=\"display:none;\">$content</textarea>";
    $html .= "\n".'<script>';
    $html .= "\n".'var editorElement = document.getElementById("tui-editor");';
    $html .= "\n".'var tuieditor =  new tui.Editor({';
    $html .= "\n"."initialEditType: 'markdown',";
    $html .= "\n"."previewStyle: 'vertical',";
    $html .= "\n"."height: 300,";
    $html .= "\n"."language: 'ko_KR',";
    $html .= "\n"."el: editorElement,";
    $html .= "\n"."toolbarItems: [";
    $html .= "\n"."'heading',";
    $html .= "\n"."'bold',";
    $html .= "\n"."'italic',";
    $html .= "\n"."'strike',";
    $html .= "\n"."'divider',";
    $html .= "\n"."'hr',";
    $html .= "\n"."'quote',";
    $html .= "\n"."'divider',";
    $html .= "\n"."'ul',";
    $html .= "\n"."'ol',";
    $html .= "\n"."//'task',"; //사용불가
    $html .= "\n"."'indent',";
    $html .= "\n"."'outdent',";
    $html .= "\n"."'divider',";
    $html .= "\n"."'table',";
    $html .= "\n"."'image',";
    $html .= "\n"."'link',";
    $html .= "\n"."'divider',";
    $html .= "\n"."'code',";
    $html .= "\n"."'codeblock'";
    $html .= "\n"."],";
    //$html .= "\n"."initialValue: '테스트',";
    $html .= "\n"."hooks: {";
    $html .= "\n"."addImageBlobHook: function(fileOrBlob, callback, source){";
    $html .= "\n"."console.log('fileOrBlob', fileOrBlob);";
    $html .= "\n"."console.log('source', source);";
    $html .= "\n"."var text = sendFile(fileOrBlob, callback);";
    $html .= "\n"."console.log('샌드파일', text);";
    $html .= "\n"."return false;";
    $html .= "\n"."}";
    $html .= "\n"."}";
    $html .= "\n"."});";        
    $html .= "\n"."tuieditor.setHtml(document.getElementById('".$id."').value);";       
    $html .= "\n".'function sendFile(file,callback) {';
        $html .= "\n".'var data = new FormData();';
        $html .= "\n".'data.append("tuieditorFile", file);';
        $html .= "\n".'$.ajax({';
           $html .= "\n".'data: data,';
           $html .= "\n".'type: "POST",';
           $html .= "\n".'url: "'.$editor_url.'/upload.php",';
           $html .= "\n".'cache: false,';
           $html .= "\n".'contentType: false,';
           $html .= "\n".'processData: false,';
           $html .= "\n".'success: function(data) {';
           $html .= "\n".'var obj =  JSON.parse(data);';
             $html .= "\n".'if (obj.success) {';
                $html .= "\n".'callback(obj.save_url, "alt text");';
             $html .= "\n".'} else {';
                $html .= "\n".'switch(parseInt(obj.error)) {';
                $html .= "\n".'case 1: alert("업로드 용량 제한에 걸렸습니다."); break; ';
                    $html .= "\n".'case 2: alert("MAX_FILE_SIZE 보다 큰 파일은 업로드할 수 없습니다."); break;';
                    $html .= "\n".'case 3: alert("파일이 일부분만 전송되었습니다."); break;';
                    $html .= "\n".'case 4: alert("파일이 전송되지 않았습니다."); break;';
                    $html .= "\n".'case 6: alert("임시 폴더가 없습니다."); break;';
                    $html .= "\n".'case 7: alert("파일 쓰기 실패"); break;';
                    $html .= "\n".'case 8: alert("알수 없는 오류입니다."); break;';
                    $html .= "\n".'case 100: alert("이미지 파일이 아닙니다.(jpeg, jpg, gif, bmp, png 만 올리실 수 있습니다.)"); break; ';
                    $html .= "\n".'case 101: alert("이미지 파일이 아닙니다.(jpeg, jpg, gif, bmp, png 만 올리실 수 있습니다.)"); break; ';
                    $html .= "\n".'case 102: alert("0 byte 파일은 업로드 할 수 없습니다."); break;';
                $html .= "\n".'}';
             $html .= "\n".'}';
           $html .= "\n".'}';
       $html .= "\n".'});';
    $html .= "\n".'}';
    $html .= "\n".'</script>';
    $html .= "\n<span class=\"sound_only\">웹 에디터 끝</span>";
    return $html;
}


// textarea 로 값을 넘긴다. javascript 반드시 필요
function get_editor_js($id, $is_dhtml_editor=true)
{
    if ($is_dhtml_editor) {
        return "var {$id}_editor_data = tuieditor.getHtml();\ndocument.getElementById('{$id}').value = {$id}_editor_data;\n";
    } else {
        return "var {$id}_editor = document.getElementById('{$id}');\n";
    }
}


//  textarea 의 값이 비어 있는지 검사
function chk_editor_js($id, $is_dhtml_editor=true)
{
    if ($is_dhtml_editor) {
        return "if (!tuieditor.getHtml()) { alert(\"내용을 입력해 주십시오.\"); return false;}\n";
    } else {
        return "if (!{$id}_editor.value) { alert(\"내용을 입력해 주십시오.\"); {$id}_editor.focus(); return false; }\n";
    }
}
?>