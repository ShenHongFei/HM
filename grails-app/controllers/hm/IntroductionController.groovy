package hm

import static hm.Application.introductionDir

class IntroductionController {
	static responseFormats = ['json']
    
    EditorService editorService
    
    // /introduction/ue?action=config&noCache=1494122902762
    def ue(){
        render editorService.processUEAction(request,response,introductionDir)
    }
    
    // params content
    def set(){
        String content = params.content
        introductionDir.eachFile{if(!content.contains(it.name)) it.delete()}
        new File(introductionDir,'content.html').write(content,'UTF-8')
        render view:'/success',model:[message:'设置成功']
    }
    
    def get(){
        def contentFile = new File(introductionDir,'content.html')
        if(!contentFile.exists()) return render(view:'/failure',model:[message:'无介绍'])
        render view:'get',model:[content:contentFile.text]
    }
    
}
