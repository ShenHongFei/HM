package system

import config.WebAppConfig
import model.Model
import model.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.annotation.SessionScope
import org.springframework.web.multipart.MultipartFile
import repo.DepartmentRepo
import repo.ProjectRepo
import service.ContentService

@Controller
@SessionScope
@Transactional(rollbackFor = Exception)
class ProjectSystem{


    Model m=new Model()
    @Autowired
    ProjectRepo repo
    @Autowired
    DepartmentRepo dpRepo
    @Autowired
    ContentService cs

    @GetMapping('/project')
    list(){
        def l=repo.findAll()
        def pl=[]
        l.each{ pl << it.info }
        m.list=pl
        m
    }

    @PostMapping('/project/add')
    add(@RequestParam(name = 'file') MultipartFile[] files,
        @RequestParam(name='contentText',required=false)String contentText,
        @Validated Project pj){
        if(repo.findByName(pj.name)) return -m<<'项目已存在'
        pj=repo.save(pj)
        m.content=pj.content=cs.createFromMultipartFile(files,contentText,pj.contextPath)
        m<<'添加成功'
    }

    @GetMapping('/project/{id}')
    getContent(@PathVariable(name='id') Long id){
        if(!repo.exists(id)) return -m<<'项目不存在'
        m.content=repo.getOne(id).content
        m
    }

    @RequestMapping('/project/delete')
    delete(@RequestParam('id')Long id){
        if(!repo.exists(id)) return -m<<'项目不存在'
        Project pj = repo.getOne(id)
        new File(WebAppConfig.FILE_DIR,pj.contextPath).deleteDir()
        repo.delete(id)
        m<<'删除成功'
    }
}
