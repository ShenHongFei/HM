package hm

class User {
    static enum Role {
        GUEST,USER,VIP,MANAGER,BOSS,ROOT
    }
    public static User GUEST = new User(id:-1,email:'guest@hm.com',username: 'Guest',role: Role.GUEST)
    
    String  email
    String  username
    Role    role
    String  password //**
    String  realname
    String  gender
    Date    registerTime
    String  phone
    String  cookieId //**
    String  lastIp //*
    Boolean autologin = false //*
    String  uuid //**
//    Date birthday
//    String address
    
    @Override
    String toString(){ "{id: $id, username:$username, password:$password, role:$role}" }
    
    static constraints = {
        password size:1..20
        username matches:/[0-9a-zA-Z\u4e00-\u9fa5_-~`·]{1,20}/,unique:true
        email email:true,nullable:false,unique:true
        phoneNumber matches:/(^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$)|(^$)/
    }
    
}
/*    void update( User u ){
        username = u.username
        realname = u.realname
        password = u.password
        gender = u.gender
        birthday = u.birthday
        email = u.email
        address = u.address
        phoneNumber = u.phoneNumber
    }
    void managerUpdate( User u ){
        update(u)
        role = u.role
        autologin = u.autologin
    }*/