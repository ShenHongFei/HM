package hm

import grails.gorm.transactions.Transactional

import static hm.Application.activityNewsDir

@Transactional
class ActivityNewsController{
	static responseFormats = ['json']
    
    EditorService  editorService
    ContentService contentService
    
    //用户添加活动新闻时先初始化UE，创建某一id的活动新闻，设置状态为未保存， 文件存放在 news/id 中，并将该活动新闻放入session(不允许用户同时编辑两个活动新闻）
    def addUE(){
        //暂时不禁止有正在编辑活动新闻时再次初始化UE
/*        if(session.activityNewsInEdit){
            return render(view:'news-in-edit',model:[news:session.activityNewsInEdit])
        }*/
        def unsaved = new ActivityNews(title:'unsaved').save()
        session.activityNewsInEdit=unsaved
        render editorService.processUEAction(request,response,unsaved.dir)
    }
	
    // params title,content 
    // 活动新闻提交后需要重新初始化UE
    def addSubmit(){
        def activityNewsInEdit = session.activityNewsInEdit
        if(!activityNewsInEdit) return fail('提交失败，找不到正在编辑的活动新闻，可能是UE自上次提交后没有重新初始化')
        activityNewsInEdit.attach()
        def htmlContent = params.content
        def files=[] as Set<String>
        new File(activityNewsDir,"$activityNewsInEdit.id").listFiles().each{
            if(!htmlContent?.contains(it.name)) it.delete()
            else files<<it.name
        }
        activityNewsInEdit.with{
            title=params.title
            content=new Content(text:htmlContent,files:files)
        }
        if(!activityNewsInEdit.validate()) return fail(activityNewsInEdit,'添加失败')
        activityNewsInEdit.saved=true
        session.activityNewsInEdit=null
        render(view:'/success',model:[message:'添加成功',obj:activityNewsInEdit,objTemplate:'/activity-news/info'])
    }
    
    def addDiscard(){
        if(!session.activityNewsInEdit){
            return fail('没有正在编辑中的活动新闻')
        }
        session.activityNewsInEdit.with{
            it.dir.deleteDir()
            it.delete()
        }
        session.activityNewsInEdit=null
        return render(view:'/success',model:[message:'废弃成功'])
    }
    
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy      = sortParams[0]?:'modifiedAt'
        def order       = sortParams[1]?:'desc'
        def activityNewss = ActivityNews.findAll("from ActivityNews as activityNews where activityNews.saved=true order by activityNews.$sortBy $order".toString(),[max:size,offset:page*size])
        render view:'/my-page',model:[myPage:new MyPage(activityNewss,ActivityNews.countBySaved(true),size,page),template:'/activity-news/info']
    }
    
    //params id
    def delete(){
        //校验存在
        ActivityNews activityNews
        def id = params.int('id')
        if(id==null||!(activityNews=ActivityNews.get(id))||!activityNews.saved) return fail("id=$id 的活动新闻不存在或处于草稿状态，无法删除")
        activityNews.dir.deleteDir()
        activityNews.delete()
        render(view:'/success',model:[message:'删除成功'])
    }
    
    def updateUE(){
        //校验存在
        ActivityNews activityNews
        def id = params.int('id')
        if(id==null||!(activityNews=ActivityNews.get(id))||!activityNews.saved) return fail("id=$id 的活动新闻不存在")
        render editorService.processUEAction(request,response,activityNews.dir)
    }
    
    //params id,title,content
    def updateSubmit(){
        //校验存在
        ActivityNews activityNews
        def id = params.int('id')
        if(id==null||!(activityNews=ActivityNews.get(id))||!activityNews.saved) return fail("id=$id 的活动新闻不存在,可能是id错误或未成功提交")
        
        def htmlContent=params.content
        def fileNames=[] as Set<String>
        new File(activityNewsDir,"$activityNews.id").listFiles().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        activityNews.with{
            title=params.title
            content.text=htmlContent
            content.files=fileNames
        }
        if(!activityNews.validate()) return fail(activityNews,'修改失败')
        render view:'/success',model:[message:'修改成功',objTemplate:'/activity-news/info',obj:activityNews]
    }
    
    def addGet(){
        if(!session.activityNewsInEdit) return fail('没有正在编辑的活动新闻')
        return render(view: '/success',model:[obj:session.activityNewsInEdit,objTemplate:'/activity-news/details'])
    }
    
    def get(){
        //校验存在
        def id = params.int('id')
        def activityNews=ActivityNews.get(id)
        if(!activityNews) return fail("id=$id 的活动新闻不存在")
        if(!activityNews.saved) return fail("id=$id 的活动新闻未成功提交，处于草稿状态")
        return render(view: '/success',model:[obj:activityNews,objTemplate:'/activity-news/details'])
    }
    
    private def fail(def obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
}
