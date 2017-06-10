ENV=''
//  ajaxSetup
// ============================================================
$.ajaxSetup({
    dataType: 'json',
});

var editor;

$(function() {
    //  编辑器初始化
    // ============================================================
    editor = UE.getEditor('editor', {
        serverUrl:  'news/add/ue',
        zIndex: 1,
        elementPathEnabled: false,
        wordCount: false
    });

    //  添加内容
    // ============================================================
    $("#add").bind("click", function() {
        if ($('#name').attr('edit') == 'true') {
            var data = {
                title: $("#name").val(),
                content: editor.getContent(),
                newsId: $('#name').attr('editId'),
            };
            if(checkContent(data) == true) modifyContent(data);
        } else {
            var data = {
                title: $("#name").val(),
                content: editor.getContent(),
            };
            if(checkContent(data) == true) addContent(data);
        }
    })

    //  初始化获取内容列表
    // ============================================================
    getContentList();

})

//  添加内容
// ============================================================
function addContent(data) {
    $.ajax({
            url: ENV + '/news/add/submit',
            method: 'post',
            data: data
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('添加失败！');
        })
        .done(function() {
            updateContentList()
            $("#name").val('');
            editor.setContent('');
            alertInfo('添加成功！');
        })
}

//  修改内容
// ============================================================
function modifyContent(data) {
    $.ajax({
            url: ENV + '/news/update/submit',
            method: 'post',
            data: data
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('修改失败！');
        })
        .done(function() {
            updateContentList()
            editor.setContent('');
            $("#name").val('');
            $("#name").attr('edit', false);
            $("#name").attr('editId', -1);
            alertInfo('修改成功！');
        })
}

function checkContent(data) {
    if (data.title == '') {
        alertInfo('标题不能为空！');
        return false;
    }
    if (data.content == '') {
        alertInfo('内容不能为空！');
        return false;
    }
    return true;
}

//  内容列表
// ============================================================
function getContentList() {
    $("#contentPage").pagination({
        pageIndex: 0,
        pageSize:10,
        total: 100,
        debug: false,
        showInfo: true,
        showJump: false,
        infoFormat: '{start} ~ {end}条，共{total}条',
        showPageSizes: false,
        pageElementSort: ['$page', '$size', '$jump', '$info'],
        remote: {
            url: ENV + '/news/list',
            pageParams: function(data) {
                return {
                    size: data.pageSize,
                    page: data.pageIndex,
                    sort: encodeURI("id,desc")
                };
            },
            totalName: 'page.totalElements',
            success: function(data) {
                updateContentTable(data.page.content);
            }
        }
    });
}

function updateContentList() {
    $("#contentPage").pagination('destroy');
    getContentList();
}

$("#contentPage").on("pageClicked", function(event, data) {
    updateContentTable(data.page.content);
}).on('jumpClicked', function(event, data) {
    updateContentTable(data.page.content);
}).on('pageSizeChanged', function(event, data) {
    updateContentTable(data.page.content);
});

//  更新表格
// ============================================================
function updateContentTable(content) {
    $("#contentTable").empty();
    for (var i = 0; i < content.length; i++) {
        var line = $('<tr itemId=' + content[i].id + '></tr>');
        line.append('<td><a href="/view.html?menu=news&id=' + content[i].id + '" target="_blank" title=' + content[i].title + ' >' + sliceTitle(content[i].title, 20) + '</a></td>');
        line.append('<td>' + transUTC(content[i].publishedAt) + '</td>');
        line.append('<td><a href="#" onclick="editItem(this)">修改</a></td>');
        line.append('<td><a href="#" onclick="deleteItem(this)">删除</a></td>');
        $('#contentTable').append(line);
    }
}

//  删除条目
// ============================================================
function deleteItem(item) {
    var id = $(item).parent().parent().attr('itemId');
    $.ajax({
            url: ENV + '/news/delete',
            method: 'post',
            data: {
                newsId: id
            },
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('删除失败!');
        })
        .done(function(data) {
            updateContentList();
            alertInfo('删除成功!');
        })
}

//  修改条目
// ============================================================
function editItem(item) {
    var id = $(item).parent().parent().attr('itemId');
    //  编辑器初始化
    // ============================================================
    editor = UE.getEditor('editor', {
        serverUrl: ENV + '/news/update/ue?newsId=' + id,
        zIndex: 1,
        elementPathEnabled: false,
        wordCount: false
    });

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
            $("#name").val(data.news.title);
            editor.setContent(data.news.content);
            $("#name").attr('edit', true);
            $("#name").attr('editId', id);
        })
}
