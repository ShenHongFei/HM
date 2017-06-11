window.ready = viewContent();


var initURL ;
var getURL ;
var postURL ;
var role;

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
    $('#edit').attr('display','none');

} else {
    $('#edit').attr('display','block');
}

//  查看内容
// ============================================================
function searchURL() {
    var value;
    var str = window.location.href;
    var num = str.indexOf("?");
    str = str.substr(num+1);
    num = str.indexOf("=");
    if (num > 0)
    {
        value = str.substr(num + 1);
    }
    return value;
}

function viewContent() {
    var request;
    request = searchURL() ;
    initURL = "/about/introduction/ue" ;
    getURL = "/about/introduction/get" ;
    postURL ="/about/introduction/set" ;

    if (request == 0){
        document.getElementById("department").innerHTML = "槐盟介绍";
        document.getElementById("depart").innerHTML = "了解我们 - 槐盟介绍";
        initURL = "/about/introduction/ue";
        getURL="/about/introduction/get";
        postURL="/about/introduction/set";
    }
    else if (request == 1){
        document.getElementById("department").innerHTML = "联系我们";
        document.getElementById("depart").innerHTML = "了解我们 - 联系我们";
        initURL = "/about/contact/ue";
        getURL="/about/contact/get";
        postURL="/about/contact/set";
    }
    else if (request == 2){
        document.getElementById("department").innerHTML = "活动图片";
        document.getElementById("depart").innerHTML = "了解我们 - 活动图片";
        initURL = "/about/gallery/ue";
        getURL="/about/gallery/get";
        postURL="/about/gallery/set";
    }
}
$(function() {
    editor = UE.getEditor('editor', {
        serverUrl: initURL,
        zIndex: 1,
        elementPathEnabled: false,
        wordCount: false,
        isShow : false,
    })


    $.ajax({

        url:getURL,
        method:'GET'
    }).fail(function(jqXHR,textStatus){
        alertWarning('ajax GET request failed\n'+textStatus);
    }).done(function(model){
        if(!model||!model.result){
            alertWarning(model?model.message:'unknown error');
            return;
        }
        $('#Introduction').html(model.content);
    });


    $("#edit").click(function(){
        $("#Introduction").css("display","none");
        $("#edit").css("display","none");
        $("#cancel").css("display","block");
        $("#add").css("display","block");
        //$("#hide_edit")
        $("#editor").css("display","block");
        editor.setShow();
        editor.setContent($('#Introduction').html());
    });
    $("#cancel").click(cancelEdit());



})


function cancelEdit(){
    $("#Introduction").css("display","block");
    if(role!=0)
    {$("#edit").css("display","block");}
    $("#cancel").css("display","none");
    $("#add").css("display","none");
    UE.getEditor('editor').setHide();
}


function submitIntroduction(){
    var content=UE.getEditor('editor').getContent();
    if(content=='')alertWarning("您未输入内容")
    else{$.ajax({
        method:'POST',
        url:postURL,
        data:{content:content}
    }).fail(function(jqXHR, textStatus){
        alertWarning('ajax post request failed\n'+textStatus);
    }).done(function(model){
        if(!model|| !model.result){
            alert(model?model.message:'unknown error');
            return;
        }
        $('#Introduction').html(content);
        alertInfo('上传成功');
        cancelEdit();
    });}
}