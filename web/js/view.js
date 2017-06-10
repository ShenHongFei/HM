window.onload = viewContent();

//  查看内容
// ============================================================
function viewContent() {
    var request = getRequest();
    if (request['menu'] == undefined) return;
    var id = request['id'];
    $("#item-header").val('活动 - 相关新闻');
    $.ajax({
            url: ENV + '/activity/get',
            method: 'get',
            data: {
                id: id
            },
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('获取失败!');
        })
        .done(function(data) {
            $("#item-title").html(data.activity.title);
            $("#item-time").html(transUTC(data.activity.modifiedAt));
            $("#item-content").html(data.activity.content);

            $("#sigEnroll").attr("href",'/enroll.html?menu=activity&id=' + id);
            $("#sigForget").attr("href",'/forSig.html?menu=activity&id=' + id);
        })
}
