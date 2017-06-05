package hm

import grails.gorm.transactions.Transactional

import static hm.Application.noticeDir

@Transactional
class NoticeController{
	static responseFormats = ['json']
    
    EditorService  editorService
    ContentService contentService
    
    //用户添加公示时先初始化UE，创建某一id的公示，设置状态为未保存， 文件存放在 notice/id 中，并将该公示放入session(不允许用户同时编辑两个公示）
    def addUE(){
        //暂时不禁止有正在编辑公示时再次初始化UE
/*        if(session.noticeInEdit){
            return render(view:'notice-in-edit',model:[notice:session.noticeInEdit])
        }*/
        def unsaved = new Notice(title:'unsaved').save()
        session.noticeInEdit=unsaved
        render editorService.processUEAction(request,response,unsaved.dir)
    }
	
    // params title,content **type**
    // 公示提交后需要重新初始化UE
    //需要多加一个部门参数
    def addSubmit(){
        def noticeInEdit = session.noticeInEdit
        if(!noticeInEdit) return fail('提交失败，找不到正在编辑的公示，可能是UE自上次提交后没有重新初始化')
        noticeInEdit.attach()
        def htmlContent = params.content
        if(!Notice.Type.values()*.toString().contains(params.type)) return fail("参数 department=$params.department 不正确")
        noticeInEdit.type=Notice.Type.valueOf(params.type)
        def files=[] as Set<String>
        new File(noticeDir,"$noticeInEdit.id").listFiles().each{
            if(!htmlContent?.contains(it.name)) it.delete()
            else files<<it.name
        }
        noticeInEdit.with{
            title=params.title
            content=new Content(text:htmlContent,files:files)
        }
        if(!noticeInEdit.validate()) return fail(noticeInEdit,'添加失败')
        noticeInEdit.saved=true
        session.noticeInEdit=null
        render(view:'/success',model:[message:'添加成功',obj:noticeInEdit,objTemplate:'/notice/info'])
    }
    
    def addDiscard(){
        if(!session.noticeInEdit){
            return fail('没有正在编辑中的公示')
        }
        session.noticeInEdit.with{
            it.dir.deleteDir()
            it.delete()
        }
        session.noticeInEdit=null
        return render(view:'/success',model:[message:'废弃成功'])
    }
    
    //params type
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy      = sortParams[0]?:'modifiedAt'
        def order       = sortParams[1]?:'desc'
        if(!params.type) return fail('参数 type 不能为空')
        if(!Notice.Type.values()*.toString().contains(params.type)) return fail("参数 type=$params.type 不正确")
        def notices = Notice.findAll(
        "from Notice as notice where notice.saved=true and notice.type='${params.type}' order by notice.$sortBy $order".toString(),
        [max:size,offset:page*size])
        render view:'/my-page',model:[myPage:new MyPage(notices,Notice.countBySaved(true),size,page),template:'/notice/info'] 
    }
    
    //params id
    def delete(){
        //校验存在
        Notice notice
        def id = params.int('id')
        if(id==null||!(notice=Notice.get(id))||!notice.saved) return fail("id=$id 的公示不存在或处于草稿状态，无法删除")
        notice.dir.deleteDir()
        notice.delete()
        render(view:'/success',model:[message:'删除成功'])
    }
    
    def updateUE(){
        //校验存在
        Notice notice
        def id = params.int('id')
        if(id==null||!(notice=Notice.get(id))||!notice.saved) return fail("id=$id 的公示不存在")
        render editorService.processUEAction(request,response,notice.dir)
    }
    
    //params id,title,content
    def updateSubmit(){
        //校验存在
        Notice notice
        def id = params.int('id')
        if(id==null||!(notice=Notice.get(id))||!notice.saved) return fail("id=$id 的公示不存在,可能是id错误或未成功提交")
        
        def htmlContent=params.content
        def fileNames=[] as Set<String>
        new File(noticeDir,"$notice.id").listFiles().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        notice.with{
            title=params.title
            content.text=htmlContent
            content.files=fileNames
        }
        if(!notice.validate()) return fail(notice,'修改失败')
        render view:'/success',model:[message:'修改成功',objTemplate:'/notice/info',obj:notice]
    }
    
    def addGet(){
        if(!session.noticeInEdit) return fail('没有正在编辑的公示')
        return render(view: '/success',model:[obj:session.noticeInEdit,objTemplate:'/notice/details'])
    }
    
    def get(){
        //校验存在
        def id = params.int('id')
        def notice=Notice.get(id)
        if(!notice) return fail("id=$id 的公示不存在")
        if(!notice.saved) return fail("id=$id 的公示未成功提交，处于草稿状态")
        return render(view: '/success',model:[obj:notice,objTemplate:'/notice/details'])
    }
    
    private def fail(def obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
}
