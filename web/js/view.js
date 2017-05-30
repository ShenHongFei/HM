window.onload = viewContent();

//  查看内容
// ============================================================
function viewContent() {
    var request = getRequest();
    if (request['menu'] == undefined) return;
    var id = request['id'];
    console.log(3);
    $("#item-header").val('活动 - 相关新闻');
    console.log(4);
    $.ajax({
            url: ENV + '/news/get',
            method: 'get',
            data: {
                newsId: id
            },
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('获取失败!');
        })
        .done(function(data) {
            $("#item-title").html(data.news.title);
            $("#item-time").html(transUTC(data.news.publishedAt));
            $("#item-content").html(data.news.content);
        })
}
