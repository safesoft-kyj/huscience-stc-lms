
    // DATA TABLES
    // =================================================================
    // Require Data Tables
    // -----------------------------------------------------------------
    // http://www.datatables.net/
    // =================================================================

    $(function(){
        //전역변수
        var obj = [];
        var obj2 = [];
        //스마트에디터 프레임생성
        nhn.husky.EZCreator.createInIFrame({
            oAppRef: obj,
            elPlaceHolder: "editor",
            sSkinURI: "/editor/SmartEditor2Skin.html",
            htParams : {
                // 툴바 사용 여부
                bUseToolbar : true,
                // 입력창 크기 조절바 사용 여부
                bUseVerticalResizer : true,
                // 모드 탭(Editor | HTML | TEXT) 사용 여부
                bUseModeChanger : true,
            }
        });

        //스마트에디터 프레임생성
        nhn.husky.EZCreator.createInIFrame({
            oAppRef: obj2,
            elPlaceHolder: "agenda",
            sSkinURI: "/editor/SmartEditor2Skin.html",
            htParams : {
                // 툴바 사용 여부
                bUseToolbar : true,
                // 입력창 크기 조절바 사용 여부
                bUseVerticalResizer : true,
                // 모드 탭(Editor | HTML | TEXT) 사용 여부
                bUseModeChanger : true,
            }
        });

        //전송버튼
        $("#submit-all").click(function(){

            var title = $("#title").val();

            if(!title) {
                alert('제목을 입력하세요');
                return;
            }

            //id가 smarteditor인 textarea에 에디터에서 대입
            obj.getById["editor"].exec("UPDATE_CONTENTS_FIELD", []);

            //id가 smarteditor인 textarea에 에디터에서 대입
            obj2.getById["agenda"].exec("UPDATE_CONTENTS_FIELD", []);
            //폼 submit
            $("#boardFrm").submit();
        });
    });