package hm


import grails.rest.*
import grails.converters.*
import org.hibernate.SessionFactory

import java.util.concurrent.atomic.AtomicInteger

import static hm.Application.dataDir
import static hm.Application.newsDir

class NewsController {
	static responseFormats = ['json']
    
    def            UEService
    SessionFactory sessionFactory
    
    //初始化UE时，创建某一id的新闻，设置状态为未保存， 文件存放在 news/id 中，并将该新闻放入session保存(不允许用户同时编辑两个新闻）
    def addUE(){
        def unsaved = new News(title:'unsaved').save()    
        def storeDir = new File(newsDir,"$unsaved.id")
        storeDir.mkdirs()
        session.newsInEdit=unsaved
        render UEService.processUEAction(request,response,storeDir)
    }
	
    // params title,content 
    // 新闻提交后需要重新初始化UE
    def addSubmit(){
        def newsInEdit = session.newsInEdit
        if(!newsInEdit) return toFailure('提交失败，找不到正在编辑的新闻，可能是UE没有初始化')
        def htmlContent = params.content
        def fileNames=[] as Set<String>
        new File(newsDir,"$newsInEdit.id").listFiles().toList().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        newsInEdit.with{
            title=params.title
            content=new Content(text:htmlContent,files:fileNames)
            publishedAt=new Date()
        }
        if(!newsInEdit.validate()) return toFailure(newsInEdit,'添加失败')
        newsInEdit.saved=true
        newsInEdit.save()
        render view:'addSuccess',model:[news:newsInEdit,message:'添加成功']
    } 
    
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:5) as Integer
        def sortParams  = (params.sort as String).split(',') as List
        def sortBy      = sortParams[0]?:'id'
        def order       = sortParams[1]?:'desc'
        render view:'/mypage',
               model:[myPage:new MyPage(
                       News.findAll("from News as news where news.saved=true order by news.$sortBy $order".toString(),[max:size,offset:page*size]),
                       News.countBySaved(true),
                       size,
                       page),
                      template:'/news/details']
    }
    
    //params newsId
    def delete(){
        //校验存在
        News news
        def newsId = params.int('newsId')
        if(newsId==null||!(news=News.get(newsId))) return toFailure("id=$newsId 的新闻不存在")
        
        new File(newsDir,"$news.id").deleteDir()
        news.delete()
        toSuccess('删除成功')
    }
    
    def updateUE(){
        //校验存在
        News news
        def newsId = params.int('newsId')
        if(newsId==null||!(news=News.get(newsId))) return toFailure("id=$newsId 的新闻不存在")
        
        render UEService.processUEAction(request,response,new File(newsDir,"$news.id"))
    }
    
    //params newsId,title,content
    def updateSubmit(){
        //校验存在
        News news
        def newsId = params.int('newsId')
        if(newsId==null||!(news=News.get(newsId))) return toFailure("id=$newsId 的新闻不存在")
        
        def htmlContent=params.content
        def fileNames=[] as Set<String>
        new File(newsDir,"$news.id").listFiles().toList().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        news.with{
            title=params.title
            content.text=htmlContent
            content.files=fileNames
        }
        if(!news.validate()) return toFailure(news,'修改失败')
        news.save()
        render view:'addSuccess',model:[news:news,message:'修改成功']
    }
    
    def get(){
        //校验存在
        News news
        def newsId = params.int('newsId')
        if(newsId==null||!(news=News.get(newsId))) return toFailure("id=$newsId 的新闻不存在")
        if(!news.saved) return toFailure("id=$newsId 的新闻未保存，可能是正在编辑中还未提交")
        render(view: 'getSuccess',model:[news:news])
    }
    
    private def toFailure(def obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    private def toFailure(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
    private def toSuccess(String message){
        render view:'/success',model:[message:message]
    }
}
