

// menu-select.js
// ====================================================================
// 실행 메뉴를 액티브 시킨다.
// ====================================================================
    $(function() {
        var pathname = location.pathname;
        activeLink(pathname);
    });

    var recursionCount = 0;
    function activeLink(pathname) {
        recursionCount ++;
        if(recursionCount == 5) {
            return;
        }
        var $a = $("#mainnav-menu").find("a[href='"+pathname+"']");
        var li = $a.parents("li");

        if(li.length == 0) {
            pathname = pathname.substring(0, pathname.lastIndexOf("/"));
            return activeLink(pathname);
        } else {
            li.find("ul.collapse").addClass('in');
        }

        recursionCount = 0;
        li.addClass('active-sub');

        // 컨텐츠 상단 텍스트
        //setPageTitle(li.find("a").html());

        // 컨텐츠 상단 텍스트
        //setNav(pathname, li.find(".active-link").text());
    }

    function setPageTitle(pageTitle) {
        $("#page-title").html("<h1 class=\"page-header text-overflow\">"+pageTitle+"</h1>");
    }

    function setNav(pathname, pageTitle) {
        var $a = $("#mainnav-menu").find("a[href='"+pathname+"']");
        $("#breadcrumb").append("<li>"+$a.text()+"</li>");
    }

    function goToURL(url) {
        window.location.href = url + location.search;
    }