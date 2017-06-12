//sign_up
//验证账号
function checkAccount(node) {
    var errorMsg = document.getElementById("statu_account");
    var account = node.value;
    if (account.length == 0) {
        errorMsg.innerHTML = "用户名不能空";
        errorMsg.style.color = "red";
    } else if (account.length > 20) {
        errorMsg.innerHTML = "用户名不能超过20位";
        errorMsg.style.color = "red";
    }
}




//验证邮箱格式
function isEmail(strEmail) {
    if (strEmail.search(/^\w+((-\w+)丨(\.\w+))*\@[A-Za-z0-9]+(\.丨-)*[A-Za-z0-9]+\.[A-Za-z0-9]+$/) != -1)
        return true;
    else
        return false;
}

function checkMail(node) {
    var errorMsg = document.getElementById("check_mail");
    var mail = node.value;
    errorMsg.innerHTML = isEmail(mail) ? "" : "邮箱格式不正确或邮箱已被注册";
    errorMsg.style.color = isEmail(mail) ? "green" : "red";
}

//验证密码
function isPsw(strPsw) {
    if (strPsw.search(/^[\\u4e00-\\u9fa5_a-zA-Z0-9-]{6,20}$/) != -1)
        return true;
    else
        return false;
}

function checkPsw(node) {
    var errorMsg = document.getElementById("statu_pwd1");
    var pwd = node.value;
    errorMsg.innerHTML = isPsw(pwd) ? " " : "密码必须为6~20位，支持中英文,数字,-_";
    errorMsg.style.color = isPsw(pwd) ? "green" : "red";
}

//验证密码是否一致
function validate() {
    var value1 = document.getElementById("pwd1").value;
    var value2 = document.getElementById("pwd2").value;

    if (value1 == value2) {
        document.getElementById("statu_pwd2").innerHTML = "";
    } else {
        document.getElementById("statu_pwd2").innerHTML = "两次密码输入不一致";
        document.getElementById("statu_pwd2").style.color = "red";
    }



}

//login
$(document).ready(function() {
   // $.cookie("role");
    $('#login_button').click(function() {
        $.ajax({
            type: 'post',
            url: 'user/login',
            data: {
                "email": $('#login_account').val(),
                "password": $('#login_pwd').val(),
                "autologin": false
            },
            success: function(data) {
                if(data.result){
                    alertInfo(data.message);
                    userloading();
                    $('#pre_login').hide();
                    $('#after_login').show();
                    window.location.reload();
                }
                else{
                    alertWarning(data.message);
                }

            }
        });

    });
   //exit
 
    $("#exit").bind("click", function() {
    $.ajax({
        type: 'post',
        url: 'user/logout',
        success: function (result) {
           alertInfoWithJump(result.message, "../");
        },
        fail: function () {
            alert("failed");
        },
        error: function (response) {
            alert("shenhongfei error!!!");
        }
    });
})

})

function alertInfoWithJump(msg, url) {
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
                // window.location.href = url;
                window.location.href = "/home.html";
            }
        }]
    });
}