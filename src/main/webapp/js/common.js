//  导航栏
// ============================================================
$(function() {
    $(".dropdown").hover(function() {
        $(this).find(".dropdown-toggle").dropdown('toggle');
        console.log(1);
    }, function() {
        $(this).find(".dropdown-toggle").dropdown('toggle');
        console.log(2);
    });
});

//  添加新闻
// ============================================================
