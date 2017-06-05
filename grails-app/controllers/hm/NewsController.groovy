package hm

import grails.gorm.transactions.Transactional

import static hm.Application.newsDir

@Transactional
class NewsController {
	static responseFormats = ['json']
    
    EditorService  editorService
    ContentService contentService
    
    //用户添加新闻时先初始化UE，创建某一id的新闻，设置状态为未保存， 文件存放在 news/id 中，并将该新闻放入session(不允许用户同时编辑两个新闻）
    def addUE(){
        //暂时不禁止有正在编辑新闻时再次初始化UE
/*        if(session.newsInEdit){
            return render(view:'news-in-edit',model:[news:session.newsInEdit])
        }*/
        def unsaved = new News(title:'unsaved').save()
        session.newsInEdit=unsaved
        render editorService.processUEAction(request,response,unsaved.dir)
    }
	
    // params title,content 
    // 新闻提交后需要重新初始化UE
    def addSubmit(){
        def newsInEdit = session.newsInEdit
        if(!newsInEdit) return fail('提交失败，找不到正在编辑的新闻，可能是UE自上次提交后没有重新初始化')
        newsInEdit.attach()
        def htmlContent = params.content
        def files=[] as Set<String>
        new File(newsDir,"$newsInEdit.id").listFiles().each{
            if(!htmlContent?.contains(it.name)) it.delete()
            else files<<it.name
        }
        newsInEdit.with{
            title=params.title
            content=new Content(text:htmlContent,files:files)
        }
        if(!newsInEdit.validate()) return fail(newsInEdit,'添加失败')
        newsInEdit.saved=true
        session.newsInEdit=null
        render(view:'/success',model:[message:'添加成功',obj:newsInEdit,objTemplate:'/news/info'])
    }
    
    def addDiscard(){
        if(!session.newsInEdit){
            return fail('没有正在编辑中的新闻')
        }
        session.newsInEdit.with{
            it.dir.deleteDir()
            it.delete()
        }
        session.newsInEdit=null
        return render(view:'/success',model:[message:'废弃成功'])
    }
    
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy      = sortParams[0]?:'modifiedAt'
        def order       = sortParams[1]?:'desc'
        def newss = News.findAll("from News as news where news.saved=true order by news.$sortBy $order".toString(),[max:size,offset:page*size])
        render view:'/my-page',model:[myPage:new MyPage(newss,News.countBySaved(true),size,page),template:'/news/info']
    }
    
    //params id
    def delete(){
        //校验存在
        News news
        def id = params.int('id')
        if(id==null||!(news=News.get(id))||!news.saved) return fail("id=$id 的新闻不存在或处于草稿状态，无法删除")
        news.dir.deleteDir()
        news.delete()
        render(view:'/success',model:[message:'删除成功'])
    }
    
    def updateUE(){
        //校验存在
        News news
        def id = params.int('id')
        if(id==null||!(news=News.get(id))||!news.saved) return fail("id=$id 的新闻不存在")
        render editorService.processUEAction(request,response,news.dir)
    }
    
    //params id,title,content
    def updateSubmit(){
        //校验存在
        News news
        def id = params.int('id')
        if(id==null||!(news=News.get(id))||!news.saved) return fail("id=$id 的新闻不存在,可能是id错误或未成功提交")
        
        def htmlContent=params.content
        def fileNames=[] as Set<String>
        new File(newsDir,"$news.id").listFiles().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        news.with{
            title=params.title
            content.text=htmlContent
            content.files=fileNames
        }
        if(!news.validate()) return fail(news,'修改失败')
        render view:'/success',model:[message:'修改成功',objTemplate:'/news/info',obj:news]
    }
    
    def addGet(){
        if(!session.newsInEdit) return fail('没有正在编辑的新闻')
        return render(view: '/success',model:[obj:session.newsInEdit,objTemplate:'/news/details'])
    }
    
    def get(){
        //校验存在
        def id = params.int('id')
        def news=News.get(id)
        if(!news) return fail("id=$id 的新闻不存在")
        if(!news.saved) return fail("id=$id 的新闻未成功提交，处于草稿状态")
        return render(view: '/success',model:[obj:news,objTemplate:'/news/details'])
    }
    
    private def fail(def obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
}
