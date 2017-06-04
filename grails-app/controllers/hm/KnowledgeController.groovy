package hm

import grails.gorm.transactions.Transactional

import static hm.Application.knowledgeDir

@Transactional
class KnowledgeController{
	static responseFormats = ['json']
    
    EditorService  editorService
    
    //用户添加知识时先初始化UE，创建某一id的知识，设置状态为未保存， 文件存放在 knowledge/id 中，并将该知识放入session(不允许用户同时编辑两个知识）
    def addUE(){
        //暂时不禁止有正在编辑知识时再次初始化UE
/*        if(session.knowledgeInEdit){
            return render(view:'knowledge-in-edit',model:[knowledge:session.knowledgeInEdit])
        }*/
        def unsaved = new Knowledge(title:'unsaved').save()
        def storeDir = new File(knowledgeDir,"$unsaved.id".toString())
        storeDir.mkdirs()
        session.knowledgeInEdit=unsaved
        render editorService.processUEAction(request,response,storeDir)
    }
	
    // params title,content **type**
    // 知识提交后需要重新初始化UE
    //需要多加一个类型参数
    def addSubmit(){
        def knowledgeInEdit = session.knowledgeInEdit
        if(!knowledgeInEdit) return fail('提交失败，找不到正在编辑的知识，可能是UE自上次提交后没有重新初始化')
        knowledgeInEdit.attach()
        def htmlContent = params.content
        if(!Knowledge.Type.values()*.toString().contains(params.type)) return fail("参数 type=$params.type 不正确")
        knowledgeInEdit.type=Knowledge.Type.valueOf(params.type)
        def files=[] as Set<String>
        new File(knowledgeDir,"$knowledgeInEdit.id").listFiles().each{
            if(!htmlContent?.contains(it.name)) it.delete()
            else files<<it.name
        }
        knowledgeInEdit.with{
            title=params.title
            content=new Content(text:htmlContent,files:files)
        }
        if(!knowledgeInEdit.validate()) return fail(knowledgeInEdit,'添加失败')
        knowledgeInEdit.saved=true
        session.knowledgeInEdit=null
        render(view:'/success',model:[message:'添加成功',obj:knowledgeInEdit,objTemplate:'/knowledge/info'])
    }
    
    def addDiscard(){
        if(!session.knowledgeInEdit){
            return fail('没有正在编辑中的知识')
        }
        session.knowledgeInEdit.with{
            it.dir.deleteDir()
            it.delete()
        }
        session.knowledgeInEdit=null
        return render(view:'/success',model:[message:'废弃成功'])
    }
    
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy      = sortParams[0]?:'modifiedAt'
        def order       = sortParams[1]?:'desc'
        if(!params.type) return fail('参数 type 不能为空')
        if(!Knowledge.Type.values()*.toString().contains(params.type)) return fail("参数 type=$params.type 不正确")
        def knowledges = Knowledge.findAll("from Knowledge as knowledge where knowledge.saved=true and knowledge.type='${params.type}' order by knowledge.$sortBy $order".toString(),[max:size,offset:page*size])
        render view:'/my-page',model:[myPage:new MyPage(knowledges,Knowledge.countBySaved(true),size,page),template:'/knowledge/info']
    }
    
    //params id
    def delete(){
        //校验存在
        Knowledge knowledge
        def id = params.int('id')
        if(id==null||!(knowledge=Knowledge.get(id))) return fail("id=$id 的知识不存在")
        knowledge.dir.deleteDir()
        knowledge.delete()
        render(view:'/success',model:[message:'删除成功'])
    }
    
    def updateUE(){
        //校验存在
        Knowledge knowledge
        def id = params.int('id')
        if(id==null||!(knowledge=Knowledge.get(id))) return fail("id=$id 的知识不存在")
        render editorService.processUEAction(request,response,knowledge.dir)
    }
    
    //params id,title,content
    def updateSubmit(){
        //校验存在
        Knowledge knowledge
        def id = params.int('id')
        if(id==null||!(knowledge=Knowledge.get(id))||!knowledge.saved) return fail("id=$id 的知识不存在,可能是id错误或未成功提交")
        
        def htmlContent=params.content
        def fileNames=[] as Set<String>
        new File(knowledgeDir,"$knowledge.id").listFiles().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        knowledge.with{
            title=params.title
            content.text=htmlContent
            content.files=fileNames
        }
        if(!knowledge.validate()) return fail(knowledge,'修改失败')
        render view:'/success',model:[message:'修改成功',objTemplate:'/knowledge/info',obj:knowledge]
    }
    
    def addGet(){
        if(!session.knowledgeInEdit) return fail('没有正在编辑的知识')
        return render(view: '/success',model:[obj:session.knowledgeInEdit,objTemplate:'/knowledge/details'])
    }
    
    def get(){
        //校验存在
        def id = params.int('id')
        def knowledge=Knowledge.get(id)
        if(!knowledge) return fail("id=$id 的知识不存在")
        if(!knowledge.saved) return fail("id=$id 的知识未成功提交，处于草稿状态")
        return render(view: '/success',model:[obj:knowledge,objTemplate:'/knowledge/details'])
    }
    
    private def fail(def obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
}
