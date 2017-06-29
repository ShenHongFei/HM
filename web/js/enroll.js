window.onload = getProtocol();

// 获取协议
// ============================================================
function getProtocol(){
    $.ajax({
        url: '/activity/law/get',
        dataType:"text",
        method: 'get',
    })
        .fail(function(jqXHR, textStatus) {
            alertWarning('协议获取失败!');
        })
        .done(function(data) {
            $("#protocol").html(data);
        })
}