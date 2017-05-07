package system

import config.WebAppConfig
import model.Activity
import model.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.annotation.SessionScope
import org.springframework.web.multipart.MultipartFile
import repo.ActivityRepo
import repo.DepartmentRepo
import service.ContentService

@Controller
@SessionScope
@Transactional(rollbackFor = Exception)
class ActivitySystem{

    Model          m =new Model()
    @Autowired
    ActivityRepo   repo
    @Autowired
    DepartmentRepo dpRepo
    @Autowired
    ContentService cs

    @GetMapping('/activity')
    list(){
        def l=repo.findAll()
        def acl=[]
        l.each{ acl << it.info }
        m.list=acl
        m
    }

    @PostMapping('/activity/add')
    add(@RequestParam(name = 'file') MultipartFile[] files,
        @RequestParam(name='contentText',required=false)String contentText,
        @RequestParam(name='departmentId') Long dpId,
        Activity ac){
        if(!dpRepo.exists(dpId)) return -m<<'部门不存在'
        if(repo.findByName(ac.name)) return -m<<'活动已存在'
        ac=repo.save(ac)
        ac.department=dpRepo.getOne(dpId)
        m.content=ac.content=cs.createFromMultipartFile(files,contentText,ac.contextPath)
        m<<'添加成功'
    }

    @GetMapping('/activity/{id}')
    getContent(@PathVariable(name='id') Long id){
        if(!repo.exists(id)) return -m<<'活动不存在'
        m.content=repo.getOne(id).content
        m
    }

    @RequestMapping('/activity/delete')
    delete(@RequestParam('id')Long id){
        if(!repo.exists(id)) return -m<<'活动不存在'
        Activity ac = repo.getOne(id)
        new File(WebAppConfig.FILE_DIR,ac.contextPath).deleteDir()
        repo.delete(id)
        m<<'删除成功'
    }
}
