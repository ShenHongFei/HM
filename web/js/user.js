window.onload=userloading();

//  用户加载
// ============================================================
function userloading(){
    var user= $.cookie("role");
    // var role=0;
    switch(user){
        case('MANAGER'):
            $('#nav  li').removeClass('manager');
        case('VIP'):
        case('USER'):
        {
            $('#nav  li').removeClass('user');
        }
        case('GUEST'):
            $('#nav  li').removeClass('guest');
        default:
            $('#nav  li').removeClass('guest');
    }
    // if(user=='VIP'||user=='MANAGER')
    //     role=1;
    // if (role == 0) {
    //     $('#edit-area').hide();
    // } else {
    //     $('#edit-area').show();
    // }
}