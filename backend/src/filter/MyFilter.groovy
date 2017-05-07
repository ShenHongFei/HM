package filter

import org.apache.commons.lang3.StringUtils

import javax.servlet.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class MyFilter implements javax.servlet.Filter{

    private static String[] FORBIDDEN_VISIT_SUFFIXES = { ".jsp" }
    private static String[] LOGIN_ONLY_PAGES         = []

    @Override
    void init( FilterConfig config ) throws ServletException{}

    @Override
    void doFilter( ServletRequest request,ServletResponse response,FilterChain chain ) throws IOException,ServletException{

        def req = request as HttpServletRequest
        def resp = response as HttpServletResponse
        def uri = req.getRequestURI() as String

        for(String suffix : FORBIDDEN_VISIT_SUFFIXES){
            if( StringUtils.endsWithIgnoreCase(uri,suffix) ){
                resp.sendError(403,"jsp页面不能直接访问")
                return
            }
        }
        Cookie[] cookies = req.getCookies()
        //todo:权限控制
        for(String s : LOGIN_ONLY_PAGES){
            if( StringUtils.startsWithIgnoreCase(uri,s) && req.getSession().getAttribute("user") == null ){
                if( cookies == null ){
                    // 1.可能用户清除过cookie 2.可能是由于用户禁用了cookie 此时都会跳转到登录界面
                    resp.setContentType("text/html; charset=UTF-8")
                    resp.setCharacterEncoding("UTF-8")
                    resp.getWriter().print("<script>alert('Cookie被清理或是已禁用，请尝试重新登录！');</script>")
                    return
                }
                resp.sendRedirect("/user/login")
                return
            }
        }
        if(req.getHeader('Origin')){
            resp.addHeader('Access-Control-Allow-Origin','null')
            resp.addHeader('Access-Control-Allow-Credentials','true')
        }
        chain.doFilter(request,response)
    }

    @Override
    void destroy(){}
}