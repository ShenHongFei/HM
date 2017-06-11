var menu;
var menuStr;
var menuUrl;
var itemType;

$(function() {
    setPanel();
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

    //  设置左面板
    $('#panel-header').html(menu.name);
    $('#panel-list').empty();
    menuUrl = menu.url;
    for (var i = 0; i < menuList.length; i++) {
        if (menu.hasUrl == 'true') {
            $('#panel-list').append('<li class="list-group-item no-radius"><a href="' + menuList[i].url +'">' + menuList[i].title + '</a></li>');
        } else {
            var itemUrl = '/index.html?menu=' + menuStr + '&type=' + menuList[i].type;
            $('#panel-list').append('<li class="list-group-item no-radius"><a href="' + itemUrl +'">' + menuList[i].title + '</a></li>');
        }
        if (menuList[i].type == type) {
            item = menuList[i];
        }
    }

    //  设置右面板
    $("#item-header").html(menu.name + ' - ' + item.title);
    itemType = item.type;

    //  查询内容
    if (request['id'] == undefined) return;
    viewContent(request['id']);
}

//  查看内容
// ============================================================
function viewContent(id) {
    $.ajax({
            url: menuUrl + '/get',
            method: 'get',
            data: {
                id: id
            },
        })
        .fail(function(jqXHR, textStatus) {
            alertWarning('获取失败!');
        })
        .done(function(data) {
            $("#item-title").html(data.item.title);
            $("#item-time").html(transUTC(data.item.modifiedAt));
            $("#item-content").html(data.item.content);
        })
}
