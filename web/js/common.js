//  ajaxSetup
// ============================================================
$.ajaxSetup({
    dataType: 'json',
});

var editor;
var role = 0; //角色标记
var menu;
var menuStr;
var menuUrl;
var itemType;

$(function() {

    //  处理角色
    // ============================================================
    var user= $.cookie("role");
    switch(user){
        case('USER'):
            $('#pre_login').hide();
            $('#after_login').show();role=0;break;
        case('MANAGER'):
        case('VIP'):
            $('#pre_login').hide();
            $('#after_login').show();role=1;break;
        default:
            role=0;
    }
    if (role == 0) {
        $('#edit-area').hide();
    } else {
        $('#edit-area').show();
    }

    //  设置面板
    // ============================================================
    setPanel();

    //  编辑器初始化
    // ============================================================
    editor = UE.getEditor('editor', {
        serverUrl: menuUrl + '/add/ue',
    });

    //  添加内容
    // ============================================================
    $("#add").bind("click", function() {
        if ($('#name').attr('edit') == 'true') {
            var data = {
                title: $("#name").val(),
                content: editor.getContent(),
                id: $('#name').attr('editId'),
            };
            if (checkContent(data) == true) modifyContent(data);
        } else {
            var data = {
                title: $("#name").val(),
                content: editor.getContent(),
                type: itemType,
            };
            if (checkContent(data) == true) addContent(data);
        }
    })

    //  初始化获取内容列表
    // ============================================================
    getContentList();

})

//  设置面版
// ============================================================
function setPanel() {
    var request = getRequest();
    if (request['menu'] == undefined) return;
    menuStr = request['menu'];
    menu = NAV[menuStr];
    var menuList = menu['list'];
    var type = request['type'];
    var item;

    // 设置左面板
    $('#panel-header').html(menu.name);
    $('#panel-list').empty();
    menuUrl = menu.url;
    for (var i = 0; i < menuList.length; i++) {
        if (menu.hasUrl == 'true') {
            $('#panel-list').append('<li class="list-group-item no-radius"><div style="color:#046eB8;cursor: pointer" onclick="document.location=\'' + menuList[i].url +'\'">' + menuList[i].title + '</div></li>');
        } else {
            var itemUrl = '/index.html?menu=' + menuStr + '&type=' + menuList[i].type;
            $('#panel-list').append('<li class="list-group-item no-radius"><div style="color:#046eB8;cursor: pointer" onclick="document.location=\'' + itemUrl +'\'">' + menuList[i].title + '</div></li>');
        }
        if (menuList[i].type == type) {
            item = menuList[i];
        }
    }

    //设置右面板
    $("#item-header").html(menu.name + ' - ' + item.title);
    $("#item-title").html(item.title);
    itemType = item.type;
}

//  添加内容
// ============================================================
function addContent(data) {
    $.ajax({
            url: menuUrl + '/add/submit',
            method: 'post',
            data: data
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('添加失败');
        })
        .done(function() {
            updateContentList()
            $("#name").val('');
            editor.execCommand('cleardoc');
            reInitEditor();
            alertInfo('添加成功');
        })
}

function reInitEditor() {
    editor.destroy();
    editor = UE.getEditor('editor', {
        serverUrl: menuUrl + '/add/ue',
    });
}

//  修改内容
// ============================================================
function modifyContent(data) {
    $.ajax({
            url: menuUrl + '/update/submit',
            method: 'post',
            data: data
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('修改失败');
        })
        .done(function() {
            updateContentList()
            editor.execCommand('cleardoc');
            $("#name").val('');
            reInitEditor();
            $("#name").attr('edit', false);
            $("#name").attr('editId', -1);
            alertInfo('修改成功');
        })
}

function checkContent(data) {
    if (data.title == '') {
        alertInfo('标题不能为空');
        return false;
    }
    if (data.content == '') {
        alertInfo('内容不能为空');
        return false;
    }
    return true;
}

//  内容列表
// ============================================================
function getContentList() {
    $("#contentPage").pagination({
        pageIndex: 0,
        pageSize: 10,
        total: 100,
        debug: false,
        showInfo: true,
        showJump: false,
        infoFormat: '共{total}条',
        showPageSizes: false,
        pageElementSort: ['$page', '$size', '$jump', '$info'],
        remote: {
            url: menuUrl + '/list',
            pageParams: function(data) {
                return {
                    size: data.pageSize,
                    page: data.pageIndex,
                    type: itemType,
                    //sort: encodeURI("id,desc")
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
    $("#contentHead").empty();
    if (role == 0) {
        var line = $('<tr></tr>');
        line.append('<th class="col-md-6">标题</th>');
        line.append('<th class="col-md-1">发布人</th>');
        line.append('<th class="col-md-2">时间</th>');
        $('#contentHead').append(line);
    } else {
        var line = $('<tr></tr>');
        line.append('<th class="col-md-4">标题</th>');
        line.append('<th class="col-md-1">发布人</th>');
        line.append('<th class="col-md-2">时间</th>');
        line.append('<th class="col-md-1">修改</th>');
        line.append('<th class="col-md-1">删除</th>');
        $('#contentHead').append(line);
    }

    $("#contentTable").empty();
    for (var i = 0; i < content.length; i++) {
        var line = $('<tr itemId=' + content[i].id + '></tr>');
        line.append('<td><a href="/view.html?menu=' + menuStr + '&type=' + itemType + '&id=' + content[i].id + '" target="_blank" title=' + content[i].title + ' >' + sliceTitle(content[i].title, 20) + '</a></td>');
        line.append('<td>' + content[i].author.username + '</td>');
        line.append('<td>' + transUTC(content[i].modifiedAt) + '</td>');
        if (role == 1) {
            line.append('<td><a href="#" onclick="editItem(this)">修改</a></td>');
            line.append('<td><a href="#" onclick="deleteItem(this)">删除</a></td>');
        }
        $('#contentTable').append(line);
    }
}

//  删除条目
// ============================================================
function deleteItem(item) {
    var id = $(item).parent().parent().attr('itemId');
    $.ajax({
            url: menuUrl + '/delete',
            method: 'post',
            data: {
                id: id
            },
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('删除失败');
        })
        .done(function(data) {
            updateContentList();
            if ($("#name").attr('edit') == 'true' && $("#name").attr('editId') == id) {
                editor.execCommand('cleardoc');
                $("#name").val('');
                $("#name").attr('edit', false);
                $("#name").attr('editId', -1);
            }
            alertInfo('删除成功');
        })
}

//  修改条目
// ============================================================
function editItem(item) {
    var id = $(item).parent().parent().attr('itemId');
    //  编辑器初始化
    // ============================================================
    editor = UE.getEditor('editor', {
        serverUrl: menuUrl + '/update/ue?id=' + id,
    });

    $.ajax({
            url: menuUrl + '/get',
            method: 'get',
            data: {
                id: id
            },
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('获取失败');
        })
        .done(function(data) {
            $("#name").val(data.item.title);
            editor.setContent(data.item.content);
            $("#name").attr('edit', true);
            $("#name").attr('editId', id);
        })
}
