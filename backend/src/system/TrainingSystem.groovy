package system

import config.WebAppConfig
import model.Department
import model.Training
import model.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.annotation.SessionScope
import org.springframework.web.multipart.MultipartFile
import repo.DepartmentRepo
import repo.TrainingRepo
import service.ContentService

@Controller
@SessionScope
@Transactional(rollbackFor = Exception)
class TrainingSystem{


    Model m=new Model()
    @Autowired
    TrainingRepo repo
    @Autowired
    DepartmentRepo dpRepo
    @Autowired
    ContentService cs

    @GetMapping('/training')
    list(){
        def l=repo.findAll()
        def tl=[]
        l.each{ tl << it.info }
        m.list=tl
        m
    }

    @PostMapping('/training/add')
//    tn中注入的属性为name
    add(@RequestParam(name = 'file') MultipartFile[] files,
        @RequestParam(name='contentText',required=false)String contentText,
        @RequestParam(name='departmentId')Long dpId,
        @Validated Training tn){
        Department dp = dpRepo.findById(dpId)
        if(!dp) return -m<<'部门不存在'
        if(repo.findByNameAndDepartment(tn.name,dp)) return -m<<'培训已存在'
        tn=repo.save(tn)
        tn.department=dp
        m.content=tn.content=cs.createFromMultipartFile(files,contentText,tn.contextPath)
        m<<'添加成功'
    }

    @GetMapping('/training/{id}')
    getContent(@PathVariable(name='id') Long id){
        if(!repo.exists(id)) return -m<<'培训不存在'
        m.content=repo.getOne(id).content
        m
    }

    @RequestMapping('/training/delete')
    delete(@RequestParam('id')Long id){
        if(!repo.exists(id)) return -m<<'培训不存在'
        Training tn = repo.getOne(id)
        new File(WebAppConfig.FILE_DIR,tn.contextPath).deleteDir()
        repo.delete(id)
        m<<'删除成功'
    }
}
