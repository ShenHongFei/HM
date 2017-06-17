package hm

class User {
    static enum Role {// manager 管理员
        GUEST,USER,VIP,MANAGER,BOSS,ROOT
    }
    public static User GUEST = new User(id:-1,email:'guest@hm.com',username: 'Guest',role: Role.GUEST)
    
    String  email
    String  username
    Role    role=Role.GUEST
    String  password //**
    
    
    String  realname
    static enum Gender{
        MAN,WOMAN
    }
    Gender  gender
    Date    registerTime
    String  phone
    Integer age
    String company
    String address
    
    
    String  cookieId //**
    String  lastIp //*
    Boolean autologin = true //*
    String  uuid //**

    
    String toString(){ "{id: $id, username:$username, password:$password, role:$role}" }
    
    static constraints = {
        //用户名可以重复
        password size:6..20
        username matches:/[0-9a-zA-Z\u4e00-\u9fa5_-]{1,20}/
        email email:true,nullable:false,unique:true
//        phone matches:/(^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$)|(^$)/
    }
    
    boolean equals(Object obj){
        return id==obj?.id
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