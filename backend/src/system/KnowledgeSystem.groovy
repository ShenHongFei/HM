package system

import config.WebAppConfig
import model.Model
import model.Knowledge
import model.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.annotation.SessionScope
import org.springframework.web.multipart.MultipartFile
import repo.KnowledgeRepo
import repo.ProjectRepo
import service.ContentService

import static model.Knowledge.Category.PROJECT

@Controller
@SessionScope
@Transactional(rollbackFor = Exception)
class KnowledgeSystem{


    Model m=new Model()
    @Autowired
    KnowledgeRepo repo
    @Autowired
    ProjectRepo pjRepo
    @Autowired
    ContentService cs

    @GetMapping('/knowledge')
    list(){
        def l=repo.findAll()
        def tl=[]
        l.each{ tl << it.info }
        m.list=tl
        m
    }

    @PostMapping('/knowledge/add')
//    kng中注入的属性为name,category
    add(@RequestParam(name = 'file') MultipartFile[] files,
        @RequestParam(name='contentText',required=false)String contentText,
        @RequestParam(name='projectId',required = false)Long pjId,
        @Validated Knowledge kng){
        Project pj=null
        if(kng.category==PROJECT){
            if(!pjId) return -m<<'项目不能为空'
            pj=pjRepo.findById(pjId)
            if(!pj) return -m<<'不存在该id的项目'
        }
        kng=repo.save(kng)
        kng.project=pj
        m.content=kng.content=cs.createFromMultipartFile(files,contentText,kng.contextPath)
        m<<'添加成功'
    }

    @GetMapping('/knowledge/{id}')
    getContent(@PathVariable(name='id') Long id){
        if(!repo.exists(id)) return -m<<'知识不存在'
        m.content=repo.getOne(id).content
        m
    }

    @RequestMapping('/knowledge/delete')
    delete(@RequestParam('id')Long id){
        if(!repo.exists(id)) return -m<<'知识不存在'
        Knowledge tn = repo.getOne(id)
        new File(WebAppConfig.FILE_DIR,tn.contextPath).deleteDir()
        repo.delete(id)
        m<<'删除成功'
    }
}
