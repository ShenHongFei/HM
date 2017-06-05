package hm

import grails.gorm.transactions.Transactional

import static hm.Application.trainingDir

@Transactional
class TrainingController{
	static responseFormats = ['json']
    
    EditorService  editorService
    ContentService contentService
    
    //用户添加培训时先初始化UE，创建某一id的培训，设置状态为未保存， 文件存放在 training/id 中，并将该培训放入session(不允许用户同时编辑两个培训）
    def addUE(){
        //暂时不禁止有正在编辑培训时再次初始化UE
/*        if(session.trainingInEdit){
            return render(view:'training-in-edit',model:[training:session.trainingInEdit])
        }*/
        def unsaved = new Training(title:'unsaved').save()
        session.trainingInEdit=unsaved
        render editorService.processUEAction(request,response,unsaved.dir)
    }
	
    // params title,content **department**
    // 培训提交后需要重新初始化UE
    //需要多加一个部门参数
    def addSubmit(){
        def trainingInEdit = session.trainingInEdit
        if(!trainingInEdit) return fail('提交失败，找不到正在编辑的培训，可能是UE自上次提交后没有重新初始化')
        trainingInEdit.attach()
        def htmlContent = params.content
        if(!Department.values()*.toString().contains(params.department)) return fail("参数 department=$params.department 不正确")
        trainingInEdit.department=Department.valueOf(params.department)
        def files=[] as Set<String>
        new File(trainingDir,"$trainingInEdit.id").listFiles().each{
            if(!htmlContent?.contains(it.name)) it.delete()
            else files<<it.name
        }
        trainingInEdit.with{
            title=params.title
            content=new Content(text:htmlContent,files:files)
        }
        if(!trainingInEdit.validate()) return fail(trainingInEdit,'添加失败')
        trainingInEdit.saved=true
        session.trainingInEdit=null
        render(view:'/success',model:[message:'添加成功',obj:trainingInEdit,objTemplate:'/training/info'])
    }
    
    def addDiscard(){
        if(!session.trainingInEdit){
            return fail('没有正在编辑中的培训')
        }
        session.trainingInEdit.with{
            it.dir.deleteDir()
            it.delete()
        }
        session.trainingInEdit=null
        return render(view:'/success',model:[message:'废弃成功'])
    }
    
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy      = sortParams[0]?:'modifiedAt'
        def order       = sortParams[1]?:'desc'
        if(!params.type) return fail('参数 type 不能为空')
        if(!Department.values()*.toString().contains(params.type)) return fail("参数 type=$params.type 不正确")
        def trainings = Training.findAll("from Training as training where training.saved=true and training.department='${params.type}' order by training.$sortBy $order".toString(),[max:size,offset:page*size])
        render view:'/my-page',model:[myPage:new MyPage(trainings,Training.countBySaved(true),size,page),template:'/training/info']
    }
    
    //params id
    def delete(){
        //校验存在
        Training training
        def id = params.int('id')
        if(id==null||!(training=Training.get(id))||!training.saved) return fail("id=$id 的培训不存在或处于草稿状态，无法删除")
        training.dir.deleteDir()
        training.delete()
        render(view:'/success',model:[message:'删除成功'])
    }
    
    def updateUE(){
        //校验存在
        Training training
        def id = params.int('id')
        if(id==null||!(training=Training.get(id))||!training.saved) return fail("id=$id 的培训不存在")
        render editorService.processUEAction(request,response,training.dir)
    }
    
    //params id,title,content
    def updateSubmit(){
        //校验存在
        Training training
        def id = params.int('id')
        if(id==null||!(training=Training.get(id))||!training.saved) return fail("id=$id 的培训不存在,可能是id错误或未成功提交")
        
        def htmlContent=params.content
        def fileNames=[] as Set<String>
        new File(trainingDir,"$training.id").listFiles().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        training.with{
            title=params.title
            content.text=htmlContent
            content.files=fileNames
        }
        if(!training.validate()) return fail(training,'修改失败')
        render view:'/success',model:[message:'修改成功',objTemplate:'/training/info',obj:training]
    }
    
    def addGet(){
        if(!session.trainingInEdit) return fail('没有正在编辑的培训')
        return render(view: '/success',model:[obj:session.trainingInEdit,objTemplate:'/training/details'])
    }
    
    def get(){
        //校验存在
        def id = params.int('id')
        def training=Training.get(id)
        if(!training) return fail("id=$id 的培训不存在")
        if(!training.saved) return fail("id=$id 的培训未成功提交，处于草稿状态")
        return render(view: '/success',model:[obj:training,objTemplate:'/training/details'])
    }
    
    private def fail(def obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
}
