$(function() {
    getActivityContentList();
})

// 查看人员名单
// ============================================================
function parListContent() {
    var request = getRequest();
    if (request['menu'] == undefined) return;
    var id = request['id'];
    $("#item-header").val('活动 - 活动报名 - 人员名单');
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
        })
    $.ajax({
        url: ENV + '/activity/list-members',
        method: 'get',
        data: {
            id: id
        }
    })
        .fail(function(jqXHR, textStatus) {
            alertWarning('获取失败!');
        })
        .done(function(data) {
            // $("#item-title").html(data.news.title);
            for(var i=0;i<data.page.content.length;i++){
                $('#parList').append('<tr><td>'+data.page.content[i].realname+'</td>'+'<td>'+data.page.content[i].gender+'</td>'+'<td>'+data.page.content[i].age+'</td>'+'<td>'+data.page.content[i].address+'</td>'+'<td>'+data.page.content[i].company+'</td>'+'<td>'+data.page.content[i].email+'</td>'+'<td>'+data.page.content[i].phone+'</td></tr>')
            }
        })
}

//  内容列表
// ============================================================
function getActivityContentList() {
    var request = getRequest();
    var id = request['id'];
    $("#item-header").val('活动 - 活动报名 - 人员名单');
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
            if(data.result==true)
            $("#item-title").html(data.activity.title);
            else
                $("#item-title").html("内容已不存在");
        });
    $("#contentPage").pagination({
        pageIndex: 0,
        pageSize: 10,
        total: 100,
        debug: false,
        showInfo: true,
        showJump: false,
        infoFormat: '{start} ~ {end}条，共{total}条',
        showPageSizes: false,
        pageElementSort: ['$page', '$size', '$jump', '$info'],
        remote: {
            url:  '/activity/list-members',
            pageParams: function(data) {
                return {
                    id: id,
                };
            },
            totalName: 'page.totalElements',
            success: function(data) {
                if(data.result==true)
                updateActivityContentTable(data.page.content);
            }
        }
    });
}

// function updateContentList() {
//     $("#contentPage").pagination('destroy');
//     getActivityContentList();
// }

$("#contentPage").on("pageClicked", function(event, data) {
    updateActivityContentTable(data.page.content);
}).on('jumpClicked', function(event, data) {
    updateActivityContentTable(data.page.content);
}).on('pageSizeChanged', function(event, data) {
    updateActivityContentTable(data.page.content);
});

//  更新表格
// ============================================================
function updateActivityContentTable(content) {
    $("#contentHead").empty();
        var line = $('<tr></tr>');
        line.append('<th width="70px">姓名</th>');
        line.append('<th width="70px">性别</th>');
        line.append('<th width="70px">年龄</th>');
        line.append('<th width="110px">家庭住址</th>');
        line.append('<th width="110px">所属单位</th>');
        line.append('<th width="90px">邮箱</th>');
        line.append('<th width="60px">联系电话</th>');
        $('#contentHead').append(line);


    $("#contentTable").empty();
    for (var i = 0; i < content.length; i++) {
        var line = $('<tr></tr>');
        line.append('<td>'+content[i].realname+'</td>');
        line.append('<td>'+content[i].gender+'</td>');
        line.append('<td>'+content[i].age+'</td>');
        line.append('<td>'+content[i].address+'</td>');
        line.append('<td>'+content[i].company+'</td>');
        line.append('<td>'+content[i].email+'</td>');
        line.append('<td>'+content[i].phone+'</td>');
        $('#contentTable').append(line);
    }
}
