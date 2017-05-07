package system

import model.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.annotation.SessionScope
import service.ContentService

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static config.WebAppConfig.INTRODUCTION_DIR

@Controller
@SessionScope
class BoardingSystem{

    Model m=new Model()
    @Autowired
    ContentService cs
    @Autowired
    UserSystem us
    
    @RequestMapping('/introduction/ue')
    void UE(HttpServletRequest req,HttpServletResponse resp){
        cs.processUEAction(req,resp,INTRODUCTION_DIR)
    }
    @PostMapping('/introduction')
    setIntroduction(@RequestParam('content')String content){
        cs.setContent(content,INTRODUCTION_DIR)
    }
    @GetMapping('/introduction')
    getIntroduction(){
        cs.getContent(INTRODUCTION_DIR)
    }

}
