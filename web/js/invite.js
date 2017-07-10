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
            // var temp_second=time.getTime()+14400000;
            // var temp_time=new Date(temp_second);
            // alert(temp_time);
            // var time1=data.code.expirationTime.split("T");
            // alert(time1);
            // $('#odeadline').html(temp_time.getDay());
            var month=time.getMonth()+1;
            $('#odeadline').html(time.getFullYear()+'-'+month+'-'+time.getDate());
            // $('#odeadline').html(time1[0]);
        })
})