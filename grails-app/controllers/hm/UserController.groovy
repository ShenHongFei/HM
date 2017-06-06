package hm

import grails.gorm.transactions.Transactional
import groovy.text.SimpleTemplateEngine
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper

import javax.mail.internet.MimeMessage
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

import static hm.User.*

@Transactional
class UserController {
    
    static responseFormats = ['json']
    
    def mailSender=new JavaMailSenderImpl().with{
        host            =   'smtp.qq.com'
        port            =   465             //端口号，QQ邮箱需要使用SSL，端口号465或587
        username        =   '350986489'
        password        =   'pniljmfgcgzobiij'
        defaultEncoding =   'UTF-8'
        javaMailProperties=[
                'mail.smtp.timeout'               :25000,
                'mail.smtp.auth'                  :true,
                'mail.smtp.starttls.enable'       :true,//STARTTLS是对纯文本通信协议的扩展。它提供一种方式将纯文本连接升级为加密连接（TLS或SSL）
                'mail.smtp.socketFactory.port'    :465,
                'mail.smtp.socketFactory.class'   :'javax.net.ssl.SSLSocketFactory',
                'mail.smtp.socketFactory.fallback':false,
        ] as Properties
        it
    }
    
    def templateEngine=new SimpleTemplateEngine()
    
    @SuppressWarnings("UnnecessaryQualifiedReference")
    def register(){
        User user=new User(params)
        if(!user.validate()) return fail(user,'注册失败')
        user.with{
            registerTime=new Date()
            role=User.Role.USER
            uuid=UUID.randomUUID().toString()
            lastIp=request.remoteAddr
            save()
        }
        session.user=user
        setUserCookies(response,user)
        success '注册成功'
    }
    
    def logout(){
        def user = session.user
        if(user) user.autologin=false
        session.user=null
        if(request.cookies.find{it.name=='autologin'}) clearCookie(response,'autologin')
        setUserCookies(response,GUEST)
        success '注销成功'
    }
    
    //params email,password,autologin
    def login(){
        def temp = new User(params)
        if(temp.hasErrors()) return fail(temp,'登录失败，参数错误')
        def user = User.find{email==temp.email}
        if(!user) return fail('登录失败，无此用户')
        if(temp.password!=user.password) return fail('登录失败，密码错误')
        user.lastIp=request.remoteAddr
        session.user=user
        setUserCookies(response,user)
        // 自动登录cookie处理
        user.autologin=params.boolean('autologin',false)
        if(user.autologin){
            def cookieId = UUID.randomUUID().toString()
            user.cookieId=cookieId
            response.addCookie(new Cookie('autologin',cookieId).with{
                maxAge=Integer.MAX_VALUE
                path='/'
                httpOnly=true
                it
            })
        }else{
            user.cookieId=UUID.randomUUID().toString()
            if(request.cookies.find{it.name=='autologin'}) clearCookie(response,'autologin')
        }
        user.save()
        success '登录成功'
    }
    
    //当前用户的信息
    def info(){
         render view:'info',model:[user:session.user] 
    }
    
    //params oldPassword [newPassword] [newUsername]
    def updateInfo(){
        def user=session.user
        if(user==GUEST) return fail(user,'当前无已登录的用户')
        if(params.oldPassword!=user.password) return fail(user,'原密码错误')
        //修改用户名
        def newUsername = params.newUsername
        if(newUsername&&newUsername!=user.username){
            if(User.find{username==newUsername}){
                return fail("用户名${newUsername}已存在")
            }
            user.username=newUsername
        }
        //按需修改密码
        if(params.newPassword)
        user.password=params.password
        if(!user.validate()) return fail(user,'更新失败')
        user.save()
        success '更新成功'
    }
    
    //params email
    def sendResetEmail(){
        def user = User.find{email==params.email}
        if(!user) return fail('该邮箱未注册')
        MimeMessage message = mailSender.createMimeMessage()
        //使用MimeMessageHelper构建Mime类型邮件,第二个参数true表明信息类型是multipart类型
        MimeMessageHelper helper = new MimeMessageHelper(message,true,'UTF-8')
        helper.setFrom('350986489@qq.com')
        helper.setTo(params.email as String)
        message.setSubject("大连高校环境联盟 重置密码")
        helper.setText(templateEngine.createTemplate(Application.resetEmailTemplateInputStream.newReader('UTF-8')).make([username:user.username,id:user.id,uuid:user.uuid]).toString(),true)
        try{
            mailSender.send(message)
        }catch(Exception e){
            e.printStackTrace()
            return fail(e.localizedMessage)
        }
        success '邮件发送成功'
    }
    
    //params id,uuid
    def resetPassword(){
        def user = User.find{id==params.id&&uuid==params.uuid}
        if(!user) return 
        user.password='123456'
        success('重置成功，新密码为123456，请及时更改。')
    }
    
    //工具方法
    private def success(String message){
        render view:'/success',model:[message:message]
    }
    private def fail(User user,String failureMessage){
        render view:'/failure',model:[errors:user?.errors,message:failureMessage]
    }
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
    static void setCookie(HttpServletResponse response,String name,String value,Integer maxAge ){
        response.addCookie(new Cookie(name,value).with{
            it.maxAge=maxAge
            path='/'
            httpOnly=false
            it
        })
    }
    static void clearCookie(HttpServletResponse response,String cookieName){
        setCookie(response,cookieName,null,0)
    }
    static void setUserCookies(HttpServletResponse response,User user){
        setCookie(response,'userId',user.id as String,-1)
        setCookie(response,'username',user.username,-1)
        setCookie(response,'role',user.role as String,-1)
    }
}
