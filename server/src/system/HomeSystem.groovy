package system

import model.Model
import model.ModelAndView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.context.annotation.SessionScope

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@SessionScope
class HomeSystem{

    @Autowired
    UserSystem us
    Model      m=new Model()

    @GetMapping(['','/'])
    ModelAndView index(HttpServletRequest req,HttpServletResponse resp ){
        //todo:首页cookie
        ModelAndView mav=us.autologin(req,resp)
        mav.view='/api.html'
        mav
    }

}
