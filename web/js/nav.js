var ENV = '';

$(function() {
    //  导航栏hover
    // ============================================================
    $(".dropdown").hover(function() {
        $(this).find(".dropdown-toggle").dropdown('toggle');
    }, function() {
        $(this).find(".dropdown-toggle").dropdown('toggle');
    });
})


