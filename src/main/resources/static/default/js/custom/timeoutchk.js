var iSecond; //초단위로 환산
var timerchecker = null;

// window.onload = function() {
//     fncClearTime();
//     initTimer();
// }
//
// function fncClearTime() {
//     iSecond = 90;
// }

Lpad = function(str, len) {
    str = str + "";
    while (str.length < len) {
        str = "0" + str;
    }
    return str;
}

var intervalId;
var duration = 1;

function dailyMissionTimer() {
    iSecond = 1800;

    var timer = duration * iSecond;
    var hours, minutes, seconds;

    intervalId = setInterval(function () {
        hours = parseInt(timer / 3600, 10);
        minutes = parseInt(timer / 60 % 60, 10);
        seconds = parseInt(timer % 60, 10);

        // if (minutes == 1 && seconds == 0) {
        //       $.niftyNoty({
        //         type: 'info',
        //         container: 'floating',
        //         html: '<h4 class="alert-title">Your session is about to expired!</h4>' +
        //             '<p class="alert-message">You will be logged out in <span id="m-timer-sec" class="text-semibold text-danger">60</span> seconds.<br/' +
        //             '<br/>Do you want to stay signed in?</p>' +
        //             '<div class="mar-top"><button name="continue-session-btn" class="btn btn-primary" type="button">Continue Session</button></div>',
        //         closeBtn: true,
        //         floating: {
        //             position: 'top-right',
        //             animationIn: 'jelly',
        //             animationOut: 'fadeOut'
        //         },
        //         focus: true,
        //         timer: 0
        //     });
        // }

        if (minutes > 0 && seconds == 0) {
            $.ajax({
                url: '/ajax/keep-session',
                method: 'get',
                data: {r: Math.random()},
                success: function (res) {
                }
            });
        }

        hours = hours < 10 ? "0" + hours : hours;
        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        // $('#time-hour').text(hours);
        $('#time-min').text(minutes);
        $('#time-sec,#m-timer-sec').text(seconds);

        if (--timer < 0) {
            timer = 0;
            clearInterval(intervalId);
            sessionTimeout();
        }
    }, 1000);
}

function sessionTimeout() {
    alert('Your session has expired.');
    document.location.replace('/logout');
}


// initTimer = function() {
//
//     rHour = parseInt(iSecond / 3600);
//     rHour = rHour % 60;
//
//     rMinute = parseInt(iSecond / 60);
//     rMinute = rMinute % 60;
//
//     rSecond = iSecond % 60;
//
//     if (iSecond > 0) {
//          $('#time-min').text(Lpad(rMinute, 2));
//          $('#time-sec,#m-timer-sec').text(Lpad(rSecond, 2));
//         iSecond--;
//         timerchecker = setTimeout("initTimer()", 1000); // 1초 간격으로 체크
//
//
//     } else {
//         logoutUser();
//     }
// }

function refreshTimer() {
    // var xhr = initAjax();
    // xhr.open("POST", "/api/login/extension", false);
    // xhr.send();
    // fncClearTime();
    $("div.alert-wrap button.close").trigger('click');
    clearInterval(intervalId);
    dailyMissionTimer();
}
``
function logoutUser() {
    clearTimeout(timerchecker);
    var xhr = initAjax();
    xhr.open("POST", "/logout", false);
    xhr.send();
    location.reload();
}

function initAjax() { // 브라우저에 따른 AjaxObject 인스턴스 분기 처리
    var xmlhttp;
    if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();
    } else {// code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return xmlhttp;
}

$(document).ready(function () {
    $(document).on("click", "button[name='continue-session-btn']", function (e) {
        refreshTimer();
    });
});