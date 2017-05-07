package service

import config.WebAppConfig
import model.Content
import model.Model
import model.ResourceFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import repo.ContentRepo
import ueditor.ActionEnter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static config.WebAppConfig.getINTRODUCTION_DIR

@Service
class ContentService{

    @Autowired
    ContentRepo repo

    Content createFromMultipartFile(MultipartFile[] files,String contentText,String contextPath ){
        def resFiles=[]
        def fileDir=new File(WebAppConfig.FILE_DIR,contextPath)
        fileDir.mkdirs()
        files.each{
            def f=new File(fileDir,it.originalFilename.replace(' ','-'))
            if(f.name.trim()&&!f.exists()){
                it.transferTo(f)
                resFiles<<new ResourceFile(f)
            }
        }
        repo.save(new Content(text: contentText,files: resFiles))
    }
    

    void delete(Content ct){

    }

    static void processUEAction(HttpServletRequest req,HttpServletResponse resp,File storeDir){
        resp.setHeader('Content-Type','text/html')
        resp.outputStream.println(new ActionEnter(req,storeDir).exec())
        resp.flushBuffer()
    }

    static Model getContent(File storeDir){
        Model m=new Model()
        File contentFile = new File(storeDir,'content.html')
        if(!contentFile.exists()){
            return -m<<'无介绍'
        }
        m.content=contentFile.text
        m
    }
    
    static Model setContent(String content,File storeDir){
        Model m=new Model()
        storeDir.eachFile{
            if(!content.contains(it.name)){
                it.delete()
            }
        }
        new File(storeDir,'content.html').write(content,'UTF-8')
        m.content=content
        m
    }
    
    
}
