
//not event f5  event.clientY < 0
//event.altKey When press Alt +F4
//event.ctrlKey When press Ctrl +F4
//event.clientY 107 or 129 is  Alt F4 postion on window screen it may change base on screen resolution
$(window).bind('beforeunload', function() {

    alert('logout');
    if ((event.clientY < 0) ||(event.altKey) ||(event.ctrlKey)||((event.clientY < 129) && (event.clientY>107))) {
        $.ajax({
            url : "/logout",  //스프링시큐리티 에서 적용시켜놓은 custom logout url
            type: 'POST' // GET, PUT
        });
    }
});

