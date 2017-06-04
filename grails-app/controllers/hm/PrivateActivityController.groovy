package hm

import grails.gorm.transactions.Transactional

import static hm.Application.privateActivityDir

@Transactional
class PrivateActivityController{
	static responseFormats = ['json']
    
    EditorService  editorService
    
    //用户添加内部活动时先初始化UE，创建某一id的内部活动，设置状态为未保存， 文件存放在 privateActivity/id 中，并将该内部活动放入session(不允许用户同时编辑两个内部活动）
    def addUE(){
        //暂时不禁止有正在编辑内部活动时再次初始化UE
/*        if(session.privateActivityInEdit){
            return render(view:'private-activity-in-edit',model:[privateActivity:session.privateActivityInEdit])
        }*/
        def unsaved = new PrivateActivity(title:'unsaved').save()
        def storeDir = new File(privateActivityDir,"$unsaved.id".toString())
        storeDir.mkdirs()
        session.privateActivityInEdit=unsaved
        render editorService.processUEAction(request,response,storeDir)
    }
	
    // params title,content **department**
    // 内部活动提交后需要重新初始化UE
    //需要多加一个部门参数
    def addSubmit(){
        def privateActivityInEdit = session.privateActivityInEdit
        if(!privateActivityInEdit) return fail('提交失败，找不到正在编辑的内部活动，可能是UE自上次提交后没有重新初始化')
        privateActivityInEdit.attach()
        def htmlContent = params.content
        if(!Department.values()*.toString().contains(params.department)) return fail("参数 department=$params.department 不正确")
        privateActivityInEdit.department=Department.valueOf(params.department)
        def files=[] as Set<String>
        new File(privateActivityDir,"$privateActivityInEdit.id").listFiles().each{
            if(!htmlContent?.contains(it.name)) it.delete()
            else files<<it.name
        }
        privateActivityInEdit.with{
            title=params.title
            content=new Content(text:htmlContent,files:files)
        }
        if(!privateActivityInEdit.validate()) return fail(privateActivityInEdit,'添加失败')
        privateActivityInEdit.saved=true
        session.privateActivityInEdit=null
        render(view:'/success',model:[message:'添加成功',obj:privateActivityInEdit,objTemplate:'/private-activity/info'])
    }
    
    def addDiscard(){
        if(!session.privateActivityInEdit){
            return fail('没有正在编辑中的内部活动')
        }
        session.privateActivityInEdit.with{
            it.dir.deleteDir()
            it.delete()
        }
        session.privateActivityInEdit=null
        return render(view:'/success',model:[message:'废弃成功'])
    }
    
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy      = sortParams[0]?:'modifiedAt'
        def order       = sortParams[1]?:'desc'
        def privateActivitys = PrivateActivity.findAll("from PrivateActivity as privateActivity where privateActivity.saved=true order by privateActivity.$sortBy $order".toString(),[max:size,offset:page*size])
        render view:'/my-page',model:[myPage:new MyPage(privateActivitys,PrivateActivity.countBySaved(true),size,page),template:'/private-activity/info']
    }
    
    //params id
    def delete(){
        //校验存在
        PrivateActivity privateActivity
        def id = params.int('id')
        if(id==null||!(privateActivity=PrivateActivity.get(id))) return fail("id=$id 的内部活动不存在")
        privateActivity.dir.deleteDir()
        privateActivity.delete()
        render(view:'/success',model:[message:'删除成功'])
    }
    
    def updateUE(){
        //校验存在
        PrivateActivity privateActivity
        def id = params.int('id')
        if(id==null||!(privateActivity=PrivateActivity.get(id))) return fail("id=$id 的内部活动不存在")
        render editorService.processUEAction(request,response,privateActivity.dir)
    }
    
    //params id,title,content
    def updateSubmit(){
        //校验存在
        PrivateActivity privateActivity
        def id = params.int('id')
        if(id==null||!(privateActivity=PrivateActivity.get(id))||!privateActivity.saved) return fail("id=$id 的内部活动不存在,可能是id错误或未成功提交")
        
        def htmlContent=params.content
        def fileNames=[] as Set<String>
        new File(privateActivityDir,"$privateActivity.id").listFiles().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        privateActivity.with{
            title=params.title
            content.text=htmlContent
            content.files=fileNames
        }
        if(!privateActivity.validate()) return fail(privateActivity,'修改失败')
        render view:'/success',model:[message:'修改成功',objTemplate:'/private-activity/info',obj:privateActivity]
    }
    
    def addGet(){
        if(!session.privateActivityInEdit) return fail('没有正在编辑的内部活动')
        return render(view: '/success',model:[obj:session.privateActivityInEdit,objTemplate:'/private-activity/details'])
    }
    
    def get(){
        //校验存在
        def id = params.int('id')
        def privateActivity=PrivateActivity.get(id)
        if(!privateActivity) return fail("id=$id 的内部活动不存在")
        if(!privateActivity.saved) return fail("id=$id 的内部活动未成功提交，处于草稿状态")
        return render(view: '/success',model:[obj:privateActivity,objTemplate:'/private-activity/details'])
    }
    
    private def fail(def obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
}
