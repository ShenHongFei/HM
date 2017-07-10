
// 上传游客信息
// ============================================================
function UploadPM() {
    if (checkph() && checkemail() && checkcom() && checknum() && checkname()) {
        var request = getRequest();
        if (request['menu'] == undefined) return;
        var id = request['id'];
        var oname = $('#tname').val();
        var osexual = $('#sexual').val();
        var ogender;
        var oage = $('#age').val();
        var ohome = $('#home').val();
        var ophone = $('#phone').val();
        var oemail=$('#email').val();
        var ocompany=$('#company').val();
        if(osexual=='男')
            ogender='MAN';
        else
            ogender='WOMAN';
        $.ajax({
            url: ENV + '/activity/signup',
            type: "post",
            data:{
                id:id,
                realname:oname,
                gender:ogender,
                age:oage,
                address:ohome,
                phone:ophone,
                email:oemail,
                company:ocompany
            }

        })
            .fail(function (jqXHR, textStatus) {
                alertWarning('提交失败!');
            })
            .done(function (data) {
                if(data.result==true)
                {$("#checkname").css("display","none");
                $("#checknum").css("display","none");
                $("#checkcom").css("display","none");
                $("#checkph").css("display","none");
                $("#checkemail").css("display","none");
                $(':input', '#touForm')
                    .not(':button, :submit, :reset, :hidden')
                    .val('')
                    .removeAttr('checked')
                    .removeAttr('selected');
                alertInfo_a("报名成功");}
                else
                    alertWarning("报名失败,"+data.message);
            })
    }
    else{
        alertWarning('请输入正确信息');
    }
}

// 上传查询信息
// ============================================================
function SearchPM() {
    if ( checkemail()) {
        var request = getRequest();
        if (request['menu'] == undefined) return;
        var id = request['id'];
        var oemail = $('#email').val();
        $.ajax({
            url: ENV + '/activity/query',
            type: "get",
            data:{
                id:id,
                email:oemail
            }
        })
            .fail(function (jqXHR, textStatus) {
                alertWarning('提交失败!');
            })
            .done(function (data) {
                $("#checkemail").css("display","none");
                $(':input', '#touForm')
                    .not(':button, :submit, :reset, :hidden')
                    .val('')
                    .removeAttr('checked')
                    .removeAttr('selected');
                if(data.result==true)
                    alertInfo_a("您已报名成功");
                else
                    alertWarning(data.message);
            })
    }
}


// 表单校验
// ============================================================
function checkname() {
    var name = document.getElementById("tname");
    var check = document.getElementById("checkname");
    regC=/^([\u4e00-\u9fa5]){2,7}$/;
    regE=/^[A-Za-z\s]+$/;
    //只能是中文，长度为2-7位
    if (name.value == "")
    {
        check.innerHTML = "名字不能为空" ;
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    else if(!regC.test(name.value)&&!regE.test(name.value))
    {
        check.innerHTML = "请输入正确名字" ;
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    else
    {
        check.style.display = "none" ;
        return true;
    }
}
function checknum() {
    var num = document.getElementById("age");
    var check = document.getElementById("checknum");
    reg=/^[0-9]*$/;
    if ((num.value >100 || num.value <3)||!reg.test(num.value))
    {
        check.innerHTML = "请输入正确年龄" ;
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    if (num.value == "")
    {
        check.innerHTML = "请输入年龄" ;
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    else
    {
        check.style.display = "none" ;
        return true;
    }
}
function checkcom() {
    var com = document.getElementById("company");
    var check = document.getElementById("checkcom");
    // reg=/^([\u4e00-\u9fa5])+$/;
    reg=/[a-zA-Z\u4E00-\u9FA5]+$/;
    if (com.value == "")
    {
        check.innerHTML = "所属单位不能为空" ;
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    else if (!reg.test(com.value))
    {
        check.innerHTML = "请输入正确所属单位" ;
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    else
    {
        check.style.display = "none" ;
        return true;
    }
}
function checkph() {
    var phone = document.getElementById("phone");
    var check = document.getElementById("checkph");
    var reg=(/[0-9\-\+\(\)\（\）\s]+/);
    var flag=0;
    var len_ph=phone.value.length;
    var r=phone.value.match(reg);
    if(r==null){
        var flag=1;}
    else{
        var num=r[0].length;}
    if (phone.value == "")
    {
        check.innerHTML = "电话不能为空" ;
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    else if (flag==1||num!=len_ph)
    {
        check.innerHTML = "请输入正确电话";
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    else
    {
        check.style.display = "none" ;
        return true;
    }
}
function checkemail() {
    var request = getRequest();
    var id = request['id'];
    var email = document.getElementById("email");
    var check = document.getElementById("checkemail");
    // var e1 = document.getElementById("email").value.indexOf("@",0);
    // var e2 = document.getElementById("email").value.indexOf(".",0);
    reg=/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    var oemail=email.value;
    var flag=0;
    if (email.value == "")
    {
        check.innerHTML = "邮箱不能为空" ;
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    // else if((e1==-1 || e2==-1) || e2<e1 )
    else if(!reg.test(email.value))
    {
        check.innerHTML = "请输入正确邮箱";
        check.style.display = "block" ;
        check.style.color = "red" ;
        return false;
    }
    else
    {
        check.style.display = "none" ;
        return true;
    }
}
// function checkname() {
//     var name = document.getElementById("tname");
//     var check = document.getElementById("checkname");
//     reg=/^([\u4e00-\u9fa5]){2,7}$/;       //只能是中文，长度为2-7位
//     if (name.value == "")
//     {
//         check.innerHTML = "名字不能为空" ;
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else if(!reg.test(name.value))
//     {
//         check.innerHTML = "请输入正确名字" ;
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else
//     {
//         check.style.display = "none" ;
//         return true;
//     }
// }
// function checknum() {
//     var num = document.getElementById("age");
//     var check = document.getElementById("checknum");
//     if (num.value == "")
//     {
//         check.innerHTML = "请输入年龄" ;
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     if (num.value >=100 || num.value <=0)
//     {
//         check.innerHTML = "请输入正确年龄" ;
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else
//     {
//         check.style.display = "none" ;
//         return true;
//     }
// }
// function checkcom() {
//     var com = document.getElementById("company");
//     var check = document.getElementById("checkcom");
//     reg=/^([\u4e00-\u9fa5]){2,7}$/;
//     if (com.value == "")
//     {
//         check.innerHTML = "所属单位不能为空" ;
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else if (!reg.test(com.value))
//     {
//         check.innerHTML = "请输入正确所属单位" ;
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else
//     {
//         check.style.display = "none" ;
//         return true;
//     }
// }
// function checkph() {
//     var phone = document.getElementById("phone");
//     var check = document.getElementById("checkph");
//     if (phone.value == "")
//     {
//         check.innerHTML = "电话不能为空" ;
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else if (!(/^1[34578]\d{9}$/.test(phone.value)))
//     {
//         check.innerHTML = "请输入正确电话";
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else
//     {
//         check.style.display = "none" ;
//         return true;
//     }
// }
// function checkemail() {
//     var request = getRequest();
//     var id = request['id'];
//     var email = document.getElementById("email");
//     var check = document.getElementById("checkemail");
//     var e1 = document.getElementById("email").value.indexOf("@",0);
//     var e2 = document.getElementById("email").value.indexOf(".",0);
//     reg=/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
//     var oemail=email.value;
//     var flag=0;
//     if (email.value == "")
//     {
//         check.innerHTML = "邮箱不能为空" ;
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else if((e1==-1 || e2==-1) || e2<e1 )
//     {
//         check.innerHTML = "请输入正确邮箱";
//         check.style.display = "block" ;
//         check.style.color = "red" ;
//         return false;
//     }
//     else
//     {
//         // $.ajax({
//         //     url: ENV + '/activity/query',
//         //     type: "get",
//         //     data:{
//         //         id:id,
//         //         email:oemail
//         //     }
//         // })
//         //     .fail(function (jqXHR, textStatus) {
//         //         alertWarning('提交失败!');
//         //     })
//         //     .done(function (data) {
//         //         if(data.result==true){
//         //             alertWarning("该邮箱已被注册");
//         //             return false;
//         //             if(data.message.indexOf("参数不符合要求")!=-1)
//         //             {
//         //                 alertWarning("请输入一个合法的邮箱");
//         //                 // check.innerHTML = "请输入正确邮箱";
//         //                 // check.style.display = "block" ;
//         //                 // check.style.color = "red" ;
//         //                 return false;
//         //             }
//         //
//         //            else if(data.message.indexOf("该用户已报名")!=-1)
//         //                 {
//         //                     alertWarning("该邮箱已报名，请更换");
//         //                 return false;
//         //                 }
//         //
//         //         }
//         //     })
//         check.style.display = "none" ;
//         return true;
//     }
// }

// 检验邮箱
// ============================================================


// 协议页面
// ============================================================
function agreeON() {
    var oagree=$('input[name="agreebox"]').val();
    // if(oagree==true){
    //     $('#agreeBtn').attr('disable',false);
    var oCon1=document.getElementById("conVie1");
    var oCon2=document.getElementById("conVie2");
    oCon1.style.display="none";
    oCon2.style.display="block";
// }
    // else if(oagree=='2'){
    //     window.close();
    // }
}

function disagreeON(){
    window.close();
}

// 复选框事件
// ============================================================
function box(){
    var oagree=$('input[name="agreebox"]').is(':checked');
    if(oagree==true)
        $('#agreeBtn').removeAttr("disabled");
    else
        $('#agreeBtn').attr("disabled",'true');
}


// 返回协议页面
// ============================================================
function retPro() {
    var oCon1=document.getElementById('conVie1');
    var oCon2=document.getElementById('conVie2');
    oCon1.style.display='block';
    oCon2.style.display='none';
}