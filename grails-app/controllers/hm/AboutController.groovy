package hm

import grails.gorm.transactions.Transactional


@Transactional
class AboutController{
	static responseFormats = ['json']
    
    EditorService editorService
    
    static Map dirMap=[contact:Application.contactDir,introduction:Application.introductionDir,gallery:Application.galleryDir]
    
    def ue(){
        render editorService.processUEAction(request,response,dirMap[params.type])
    }
    
    // params content
    def set(){
        String content = params.content
        dirMap[params.type].eachFile{if(!content.contains(it.name)) it.delete()}
        new File(dirMap[params.type] as File,'content.html').write(content,'UTF-8')
        render view:'/success',model:[message:'设置成功']
    }
    
    def get(){
        def contentFile = new File(dirMap[params.type] as File,'content.html')
        if(!contentFile.exists()) return render(view:'/failure',model:[message:'暂时无内容'])
        render view:'content',model:[content:contentFile.text]
    }
}
