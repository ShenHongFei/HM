package system

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.sun.istack.internal.Nullable
import config.WebAppConfig
import groovy.text.SimpleTemplateEngine
import model.Model
import model.User
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.annotation.SessionScope
import repo.UserRepo

import javax.imageio.ImageIO
import javax.mail.MessagingException
import javax.mail.internet.MimeMessage
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static model.User.GUSET_USER
import static model.User.Role.*

@Controller
@SessionScope
@Transactional(rollbackFor = Exception)
class UserSystem{

    User     user = GUSET_USER
    Model    m    = new Model()
    @Autowired
    UserRepo repo
    @Autowired
    DefaultKaptcha verificationCodeGenerator
    @Autowired
    JavaMailSenderImpl mailSender
    Map verificationCodes = [:]
    //todo:验证码刷新逻辑
//todo:所有的时间格式

    //todo:密码存储加密
    //todo:用户头像
    
    @GetMapping('/user')
    getUser(){
        m<<user
    }
    
    @GetMapping('/user/verificationCode.jpg')
    void getCode(@RequestParam('category') String category,HttpServletResponse resp){
        def vcode = verificationCodeGenerator.createText()
        verificationCodes[category]=vcode
        ImageIO.write(verificationCodeGenerator.createImage(vcode),'jpg',resp.outputStream)
    }


    @GetMapping('/user/manage')
    Page<User> manageListAll( @PageableDefault(value = 3, sort = 'id', direction = Sort.Direction.DESC) Pageable pageable ){ repo.findAll(pageable) }
    @GetMapping('/user/manage/get')
    manageGetOne(@RequestParam('id')Long id){
        User e=repo.getOne(id)
        if(!e) return -m<<'用户不存在'
        m << e
    }
    @PostMapping('/user/manage/update')
    manageUpdate(@RequestParam('id')Long id,@Validated User t){
        def e=repo.getOne(id)
        if(!e) return -m<<'用户不存在'
        if(t.username!=e.username&&repo.findByUsername(t.username)) return -m<<'用户名已存在'
        e.managerUpdate(t)
        m<<e
    }
    @PostMapping('/user/manage/add')
    manageAdd(@Validated User t){
        User e = repo.findByUsername(t.username)
        if( e ) return -m << '用户名已存在'
        if(!StringUtils.trimToNull(t.password)) return -m<<'密码不能为空'
        if(![USER,VIP,MANAGER,BOSS].contains(t.role)) return -m<<'未设置权限或设置不正确'
        t.registerTime = new Date()
        repo.save(t)
        m<<'添加成功'
    }
    @PostMapping('/user/manage/delete')
    manageDelete(@RequestParam('id')Long id){ repo.delete(id);m<<'删除成功'}
    //todo:筛选用户

    //--------------------------------------------------------------------------------

    def autologin(HttpServletRequest req,HttpServletResponse resp){
        if( user==GUSET_USER ){
            Cookie[] cookies = req.cookies
            Cookie autologinCookie = cookies.find{ it.name == 'autologin' }
            if(autologinCookie){
                def cookieId = autologinCookie.value
                User e=repo.findByCookieId(cookieId)
                if(e&&req.remoteAddr==e.lastIp&&cookieId==e.cookieId){
                    user=e
                    saveUserInfoToCurrentSession(req,resp)
                }
            }
        }
        m << user
    }

    @PostMapping( '/user/login' )
    login( @Validated User t,@RequestParam( name = 'verificationCode' )String code,HttpServletRequest req,HttpServletResponse resp ){
        
//        if( code!=verificationCodes.login ) return -m<<'验证码错误'
        
        User e = repo.findByEmail(t.email)

        if( !e ) return -m << '用户不存在'
        if( e.password != t.password ) return -m << '密码错误'

        user=e
        String addr = req.remoteAddr
        user.lastIp = addr
        //自动登录Cookie设置及清空
        if(t.autologin){
            def cookieId = UUID.randomUUID() as String
            user.cookieId = cookieId
            user.autologin=true
            setCookie(resp,'autologin',cookieId,Integer.MAX_VALUE)
        }else{
            Cookie autologinCookie = req.cookies.find{ it.name == 'autologin' }
            if(autologinCookie) setCookie(resp,'autologin',null,0)
            user.autologin=false
        }
        saveUserInfoToCurrentSession(req,resp)
        m<<'登录成功'
        //todo:登录成功后更换验证码
    }


    @PostMapping('/user/register')
    register(@Validated User t,@RequestParam( name = 'verificationCode' )String code,HttpServletRequest req,HttpServletResponse resp ){
        
//        if( code!=verificationCodes.register ) return -m<<'验证码错误'
        
        User e = repo.findByEmail(t.email)

        if( e ) return -m << '用户已存在'
        if(!StringUtils.trimToNull(t.password)) return -m<<'密码不能为空'

        t.registerTime = new Date()
        t.role=USER
        t.resetKey=UUID.randomUUID().toString()

        repo.save(t)
        user = t
        saveUserInfoToCurrentSession(req,resp)
        //todo:发送注册成功邮件
        m<<'注册成功'
    }

    @RequestMapping('/user/logout')
    logout(HttpServletRequest req,HttpServletResponse resp ){
        user   = GUSET_USER
        user.cookieId=null
        setCookie(resp,'autologin',null,0)
        saveUserInfoToCurrentSession(req,resp)
        'redirect:/'
    }
//todo:api test refresh

    @PostMapping('/user/update')
    update(@Validated User t,@RequestParam('oldPassword') String op,@RequestParam( name = 'verificationCode' )String code,HttpServletResponse resp ){

//        if( code!=verificationCodes.update ) return -m<<'验证码错误'
        
        if(t.username!=user.username&&repo.findByUsername(t.username)) return -m<<'用户名已存在'
        if(op!=user.password) return -m<<'原密码错误'
        user.update t
        saveUserInfoToCurrentSession(null,resp)
        m<<'更新成功'
    }

    @PostMapping('/user/reset-email')
    sendResetEmail(@RequestParam String email){
        User u = repo.findByEmail(email)
        if(!u) return -m<<'该邮箱未被注册'
        //todo:创建线程执行发送操作
        MimeMessage message = mailSender.createMimeMessage()
        //使用MimeMessageHelper构建Mime类型邮件,第二个参数true表明信息类型是multipart类型
        MimeMessageHelper helper = new MimeMessageHelper(message,true,'UTF-8')
        helper.setFrom('350986489@qq.com')
        helper.setTo(u.email)
        message.setSubject("大连高校环境联盟 重置密码")
        helper.setText(new SimpleTemplateEngine().createTemplate(WebAppConfig.RESET_EMAIL_TEMPLATE).make([username:u.username,id:u.id,resetKey:u.resetKey]).toString(),true)
        mailSender.send(message)
        m<<'邮件已发送'
    }
    
    @GetMapping('/user/reset')
    resetPassword(@RequestParam String resetKey,@RequestParam Long id){
        User u = repo.findById(id)
        if(!u||resetKey!=u.resetKey) return -m<<'重置失败'
        u.password='123456'
        m<<'重置成功，新密码为123456'
    }

//--------------------------------------------------------------------------------
    
    File getUserTmpDir(){
        return new File(WebAppConfig.TMP_DIR,user.id.toString())
    }

    private static void setCookie( HttpServletResponse resp,String name,String value,Integer maxAge ){
        Cookie cookie = new Cookie(name,value)
        cookie.maxAge = maxAge
        cookie.path = '/'
        cookie.httpOnly = false
        resp.addCookie(cookie)
    }
    private void saveUserInfoToCurrentSession( @Nullable HttpServletRequest req,HttpServletResponse resp ){
        setCookie(resp,'id',user.id as String,-1)
        setCookie(resp,'username',user.username,-1)
        setCookie(resp,'role',user.role as String,-1)
        if(req) req.session.setAttribute('user',user)
    }
    
    

}
