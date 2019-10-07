
// Form-File-Upload.js
// ====================================================================
// This file should not be included in your project.
// This is just a sample how to initialize plugins or components.
//
// - ThemeOn.net -


$(document).on('nifty.ready', function() {

    // DROPZONE.JS WITH BOOTSTRAP'S THEME
    // =================================================================
    // Require Dropzone
    // http://www.dropzonejs.com/
    // =================================================================
    // Get the template HTML and remove it from the doumenthe template HTML and remove it from the doument
    var previewNode = document.querySelector("#dz-template");
    previewNode.id = "";
    var previewTemplate = previewNode.parentNode.innerHTML;
    previewNode.parentNode.removeChild(previewNode);

    var uploadBtn = $('#dz-upload-btn');
    var removeBtn = $('#dz-remove-btn');
    var myDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
        url: "/admin/border/file-post", // Set the url
        thumbnailWidth: 50,
        thumbnailHeight: 50,
        parallelUploads: 20,
        previewTemplate: previewTemplate,
        autoQueue: false, // Make sure the files aren't queued until manually added
        previewsContainer: "#dz-previews", // Define the container to display the previews
        clickable: ".fileinput-button" // Define the element that should be used as click trigger to select files.
    });


    myDropzone.on("addedfile", function(file) {
        // Hookup the button
        // uploadBtn.prop('disabled', false);
        removeBtn.prop('disabled', false);
    });

    // Update the total progress bar
    myDropzone.on("totaluploadprogress", function(progress) {
        $("#dz-total-progress .progress-bar").css({'width' : progress + "%"});
    });

    myDropzone.on("sending", function(data, xhr, formData) {
        // Show the total progress bar when upload starts
        document.querySelector("#dz-total-progress").style.opacity = "1";

        formData.append("id", $('#id').val());
    });

    // Hide the total progress bar when nothing's uploading anymore
    myDropzone.on("queuecomplete", function(progress) {
        document.querySelector("#dz-total-progress").style.opacity = "0";
    });


    // Setup the buttons for all transfers
    uploadBtn.on('click', function() {
        //Upload all files
        myDropzone.enqueueFiles(myDropzone.getFilesWithStatus(Dropzone.ADDED));

        // $("#submit-all").trigger("click");

        saveContent();

        location.href = "/admin/border/list";
    });

    removeBtn.on('click', function() {
        myDropzone.removeAllFiles(true);
        // uploadBtn.prop('disabled', true);
        removeBtn.prop('disabled', true);
    });


    //전역변수
    var obj = [];
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

    function saveContent() {

        //id가 smarteditor인 textarea에 에디터에서 대입
        obj.getById["editor"].exec("UPDATE_CONTENTS_FIELD", []);

        //폼 submit
        $("#boardFrm").submit();
    }

    //전송버튼
    // $("#submit-all").click(function(){
    //
    // });

});

