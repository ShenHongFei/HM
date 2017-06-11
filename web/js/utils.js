//  警示alert
// ============================================================
function alertWarning(msg) {
    msg = "<h3 style='text-align:center;'>" + msg + "</h3>";
    BootstrapDialog.show({
        type: BootstrapDialog.TYPE_DANGER,
        cssClass: 'set-dialog',
        title: "消息提示",
        message: msg,
        buttons: [{
            label: '关闭',
            action: function(dialogRef) {
                dialogRef.close();
            }
        }]
    });
}

//  提示alert
// ============================================================
function alertInfo(msg) {
    msg = "<h3 style='text-align:center;'>" + msg + "</h3>";
    BootstrapDialog.show({
        type: BootstrapDialog.TYPE_INFO,
        cssClass: 'set-dialog',
        title: "消息提示",
        message: msg,
        buttons: [{
            label: '关闭',
            action: function(dialogRef) {
                dialogRef.close();
            }
        }]
    });
}

//  提示alert活动版
// ============================================================
function alertInfo_a(msg) {
    msg = "<h3 style='text-align:center;'>" + msg + "</h3>";
    BootstrapDialog.show({
        type: BootstrapDialog.TYPE_INFO,
        cssClass: 'set-dialog',
        title: "消息提示",
        message: msg,
        buttons: [{
            label: '关闭',
            action: function(dialogRef) {
                dialogRef.close();
                window.close();
            }
        }]
    });
}


//  时间戳格式转换
// ============================================================
function transTimestamp(timestamp) {
    var date = new Date();
    date.setTime(timestamp);
    //return date.toLocaleString();
    //return date.toLocaleDateString();
    var result = date.getFullYear() + '年';
    result += (date.getMonth() + 1) + '月';
    result += date.getDate() + '日';
    result += date.getHours() + ':';
    result += date.getMinutes() + ':';
    result += date.getSeconds();
    console.log(date.toLocaleString());
    console.log(result);
    return result;
}

function transUTC(utc){
    var date = dateFns.parse(utc);
    // var result = date.getFullYear() + '年';
    // result += (date.getMonth() + 1) + '月';
    // result += date.getDate() + '日 ';
    // result += date.getHours() + ':';
    // result += date.getMinutes();
    var result = dateFns.format(date, 'YYYY-MM-DD HH:mm');
    return result;
}

//  限制标题长度
// ============================================================
function sliceTitle(title, length) {
    return title.length <= length ? title : title.slice(0, length) + '...';
}

//  获取url中参数
// ============================================================
function getRequest() {
    var url = window.location.search;
    var request = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            request[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return request;
}
