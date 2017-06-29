/**
 * Created by 王子恒 on 2017/6/29.
 */
$(document).ready(function() {
    $.ajax({
        url:'/user/manage/invitation-code/get',
        type: "post"
    })
        .fail(function (jqXHR, textStatus) {
            alertWarning('提交失败!');
        })
        .done(function (data) {
            $('#tinvite').html(data.code.value);
            // a.innerHTML();
            var time=new Date(data.code.expirationTime);
            var time1=data.code.expirationTime.split("T");
            $('#odeadline').html(time1[0]);
        })
})