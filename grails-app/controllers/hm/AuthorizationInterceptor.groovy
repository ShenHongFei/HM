package hm

import static hm.User.GUEST
import static hm.User.Role.MANAGER
import static hm.User.Role.USER
import static hm.User.Role.VIP

class AuthorizationInterceptor {
    
    static userLevel=[~'.*/(private-activity|training|notice)/.*']
    static vipLevel=[~'.*/(add|update)/.*',~'.*/delete$',~'/about/.*/(ue|set)']
    static managerLevel=[~'.*/user/manage/.*']
    
    AuthorizationInterceptor(){
        matchAll()
    }

    boolean before() {
        def uri = URLDecoder.decode(request.requestURI-request.contextPath,'UTF-8')
        try{
            if(!uri.split('/')[-1].contains('.')){
                println "API=\t$uri"
                println "参数=\t$request.parameterMap"
                println "Role=\t$session.user.role"
            }
        }catch(any){}
        def refreshCookie=false
        //首次访问网站
        if(!session.user){
            session.user=GUEST
            refreshCookie=true
        }
        //若当前用户为访客，根据有无autologinCookie尝试自动登录
        def autologinCookie = request.cookies.find{it.name=='autologin'}
        if(session.user==GUEST&&autologinCookie){
            def user = User.find{lastIp==request.remoteAddr&&cookieId==autologinCookie.value&&autologin}
            if(user){
                session.user=user
                refreshCookie=true
            }else{
                UserController.clearCookie(response,'autologin')
            }
        }
        //设置/刷新 前端Cookie
        if(refreshCookie||!request.cookies.find{it.name=='userId'}){
            UserController.setUserCookies(response,session.user)
        }
        
        //权限控制

        if(userLevel.any{uri ==~ it}&&session.user.role<USER||
           vipLevel.any{uri ==~ it}&&session.user.role<VIP||
           managerLevel.any{uri ==~ it}&&session.user.role<MANAGER){
            response.status=403
            render('权限不足')
            return false
        }
        true
    }
    
    boolean after() {
        true
    }

    void afterView() {
        if(request.getHeader('Origin')){
            response.addHeader('Access-Control-Allow-Origin','shenhongfei.site')
            response.addHeader('Access-Control-Allow-Credentials','true')
        }
    }
}
