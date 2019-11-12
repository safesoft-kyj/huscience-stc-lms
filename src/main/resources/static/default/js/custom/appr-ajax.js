
// Form-File-Upload.js
// ====================================================================
// This file should not be included in your project.
// This is just a sample how to initialize plugins or components.
//
// - ThemeOn.net -


$(function() {

    var userId = $('#sec-user-name').text();
    var url = "/api/approval";

    // 학습시간 저장
    $.ajax({
        type: "GET",
        url: url,
        data: {
            'userId': userId,
        },
        dataType: 'JSON',
        async: false,
        success: function (data) {
            $('#course-my-approval').text(data['courseMyApproval']);
            $('#course-my-process').text(data['courseMyProcess']);
            $('#course-my-approval2').text(data['courseMyApproval']);
            $('#course-my-process2').text(data['courseMyProcess2']);
            $('#document-my-approval').text(data['documentMyApproval']);
            $('#document-my-process').text(data['documentMyProcess']);
            $('#document-my-approval2').text(data['documentMyApproval']);
            $('#document-my-process2').text(data['documentMyProcess2']);
        },
        error: function (e) {
            alert('ajax error');
            console.log(e.responseText);
        }
    });
});

