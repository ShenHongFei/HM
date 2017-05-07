package model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.Length
import org.springframework.format.annotation.DateTimeFormat

import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Past
import javax.validation.constraints.Pattern

@Entity
@JsonIgnoreProperties( ['hibernateLazyInitializer','handler'] )
class User{

    public static User   GUSET_USER = new User(id:0,username: 'Guest',role: Role.GUEST)


    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    Long   id

    @Column(length = 20 )
    @Pattern( regexp = /[0-9a-zA-Z\u4e00-\u9fa5_-~`·]{1,20}/,message = '用户名含有非法字符或长度不正确' )
    String username

    @Column( length = 20 )
    String realname

    @Column( length = 20 )
    @Length( min = 1,max = 20,message = '密码长度不正确' )
    String password

    static enum Role {
        GUEST,USER,VIP,MANAGER,BOSS,ROOT
    }
    @Enumerated(EnumType.STRING)
    Role    role

    @Pattern( regexp = /[MF]{1}/,message = '性别格式不正确，应为"M"或"F"' )
    String  gender

    @Past( message = '生日应为过去日期' )
    @DateTimeFormat( pattern = 'yyyy-MM-dd' )
    Date    birthday

    Date    registerTime

    @Column( length = 30 ,unique = true)
    @Email
    String  email

    @Column( length = 100 )
    String  address

    @Column( length = 15 )
    @Pattern( regexp = /(^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$)|(^$)/,message = '手机号格式错误' )
    String  phoneNumber


    @Column( length = 40 )
    @JsonIgnore
    String cookieId

    @Column( length = 30 )
    String  lastIp

    Boolean autologin = false
    
    @Column(length=40)
    String resetKey


    @Override
    String toString(){ "{id: $id, username:$username, password:$password, role:$role}" }

    void update( User u ){
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
    }
    
}

/*    @OneToMany( mappedBy = 'author' )   @JsonIgnore      @OrderBy( 'modTime desc' )
    List<Thread> threads   = new ArrayList<>()*/

/*    @OneToMany( mappedBy = 'reviewer' ) @JsonIgnore     @OrderBy( 'time desc' )
    List<Reply>  replies   = new ArrayList<>()*/