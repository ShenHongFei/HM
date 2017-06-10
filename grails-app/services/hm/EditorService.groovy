package hm

import grails.gorm.transactions.Transactional
import ueditor.ActionEnter

import static hm.Application.introductionDir

@Transactional
class EditorService{

    //从文件读取配置、保存上传图片、返回结果json String
    String processUEAction(request,response,storeDir) {
        println "UEditor操作：$request.requestURI"
        def ueAction=request.getParameter('action')
        switch(ueAction){
            case 'config':
                response.contentType='application/json';break
            default:null
        }
        new ActionEnter(request,storeDir).exec()
    }
}
