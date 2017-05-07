package system

import config.WebAppConfig
import model.Content
import model.Model
import model.News
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.annotation.SessionScope
import repo.NewsRepo
import service.ContentService

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@SessionScope
class NewsSystem{
    
    Model m=new Model()
    @Autowired
    ContentService cs
    @Autowired
    UserSystem us
    @Autowired
    NewsRepo repo
    
    
    @RequestMapping('/news/ue')
    void UEAction(HttpServletRequest req,HttpServletResponse resp){
        cs.processUEAction(req,resp,News.getDir(nextId))
    }
    
    @PostMapping('/news')
    add(@RequestParam('title') title,@RequestParam('content')String content){
        if(repo.findByTitle(title)) return -m<<'新闻已存在'
        News news = new News(title:title,content:new Content(content,News.getDir(nextId)),publishedAt:new Date())
        repo.save(news)
        m.id=news.id
        m<<'新增成功'
    }
    
    @GetMapping('/news/{id}')
    getById(@PathVariable('id')long id){
        News news=repo.findById(id)
        if(!news) return -m<<'新闻不存在'
        List attachments=[]
        news.content.files.each{
            attachments<<"$WebAppConfig.FILE_DIR.name/$News.DIR.name/$news.dir.name/$it.name".toString()
        }
        m.news=[id:news.id,
                title:news.title,
                publishedAt:news.publishedAt,
                content:news.content.text,
                attachments:attachments]
    }
    
    @GetMapping('/news')
    Page<Map> list(@PageableDefault(value = 3, sort = 'id', direction = Sort.Direction.DESC) Pageable pageable ){
        Page<News> page = repo.findAll(pageable)
        page.map({[
                id:it.id,
                title:it.title,
                publishedAt:it.publishedAt]})
    }
    
    @PostMapping('/news/delete')
    delete(@RequestParam('id')long id){
        News news=repo.findById(id)
        if(!news) return -m<<'新闻不存在'
        news.dir.delete()
        repo.delete(news)
        m<<'删除成功'
    }

    private long getNextId(){
        repo.findFirstByOrderByIdDesc()?.id?:0+1
    }
}
