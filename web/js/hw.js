$(document).ready(function() {

    $('#login_button').click(function() {
        var reEmail=/^\w+((-\w+)丨(\.\w+))*\@[A-Za-z0-9]+((\.丨-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
        if(!reEmail.test($('#email').val())){
            if($('#email').val().length==0){
                alert("用户名不能为空")
            }
            else{
                alert("用户名格式有误，请输入正确邮箱地址")
            }
            return;
        }
        var rePwd = /[A-Za-z0-9\.\_]{6,12}/;
        if (!rePwd.test($('#pwd1').val())) {
            if ($('#pwd1').val().length == 0) {
                alert("密码不能为空")
            } else {
                alert("密码格式有误")
            }
            return;
        }

        $.ajax({
            type: 'post',
            url: 'user/login',
            data: {
                "email": $('#email').val(),
                "password": $('#pwd1').val(),
                "autologin": false
            },
            success: function(result) {
                alert(result.message);
            }
        });

    });
})
