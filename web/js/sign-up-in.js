//sign_up
//验证邮箱格式
function isEmail(strEmail) {
    if (strEmail.search(/^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/)!=-1)
        return true;
    else
        return false;
}

function checkMail(node) {
    var errorMsg = document.getElementById("check_mail");
    var mail = node.value;
    errorMsg.innerHTML = isEmail(mail) ? "" : "邮箱格式不正确";
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
    errorMsg.innerHTML = isPsw(pwd) ? " " : "密码长度为6~20位，仅支持英文字母,数字-和_";
    errorMsg.style.color = isPsw(pwd) ? "green" : "red";
}



//回验密码是否一致。 两处输入密码出加onblur校验是否一致，解决第二次未输入就校验问题
var flag=false;

function pwd1Check(){
    if (flag) {
        validate();
    }
}

function pwd2Check(){
    flage=true;
    validate();
}

//验证账号
function checkAccount(node) {
    var errorMsg = document.getElementById("statu_account");
    var account = $.trim(node.value);
    if (account.length==0) {
        errorMsg.innerHTML = "用户名不能空";
        errorMsg.style.color = "red";
    } else if (account.length > 20) {
        errorMsg.innerHTML = "用户名不能超过20位";
        errorMsg.style.color = "red";
    }
    else{
        errorMsg.innerHTML = "";
    }
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
                    self.location='home.html';
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
           alertInfoWithJump(result.message, "home.html");
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