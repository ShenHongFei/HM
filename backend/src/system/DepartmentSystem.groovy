package system

import config.WebAppConfig
import model.Department
import model.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.annotation.SessionScope
import org.springframework.web.multipart.MultipartFile
import repo.DepartmentRepo
import service.ContentService

@Controller
@SessionScope
@Transactional(rollbackFor = Exception)
class DepartmentSystem{


    Model m=new Model()
    @Autowired
    DepartmentRepo repo
    @Autowired
    ContentService cs

    @GetMapping('/department')
    list(){
        def l=repo.findAll()
        def dl=[]
        l.each{ dl << it.info }
        m.list=dl
        m
    }

    @PostMapping('/department/add')
    add(@RequestParam(name = 'file') MultipartFile[] files,
        @RequestParam(name='contentText',required=false)String contentText,
        @Validated Department dp){
        if(repo.findByName(dp.name)) return -m<<'部门已存在'
        dp=repo.save(dp)
        m.content=dp.content=cs.createFromMultipartFile(files,contentText,dp.contextPath)
        m<<'添加成功'
    }

    @GetMapping('/department/{id}')
    getContent(@PathVariable(name='id') Long id){
        if(!repo.exists(id)) return -m<<'部门不存在'
        m.content=repo.getOne(id).content
        m
    }

    @RequestMapping('/department/delete')
    delete(@RequestParam('id')Long id){
        if(!repo.exists(id)) return -m<<'部门不存在'
        Department dp = repo.getOne(id)
        new File(WebAppConfig.FILE_DIR,dp.contextPath).deleteDir()
        repo.delete(id)
        m<<'删除成功'
    }
}
