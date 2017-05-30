package hm

class UrlMappings {
    
    static mappings = {
        "/$controller/$action?"{
            /*constraints {
                
            }*/
        }
        "/"                             controller:'resource',      action:'listDir'
        "/file/**.$suffix"              controller:'resource',      action:'file'
        "/**"                           controller:'resource',      action:'listDir'
        "/**.$suffix"                   controller:'resource',      action:'web'
        "/user/manage/$action"          controller:'userManage'
        "/news/add/ue"                  controller:'news',          action:'addUE'
        "/news/add/submit"              controller:'news',          action:'addSubmit'
        "/news/update/ue"               controller:'news',          action:'updateUE'
        "/news/update/submit"           controller:'news',          action:'updateSubmit'
        "500"(view: '/error')
        "404"(view: '/notFound')
        
        delete "/$controller/$id(.$format)?"(action:"delete")
        get "/$controller(.$format)?"(action:"index")
        get "/$controller/$id(.$format)?"(action:"show")
        post "/$controller(.$format)?"(action:"save")
        put "/$controller/$id(.$format)?"(action:"update")
        patch "/$controller/$id(.$format)?"(action:"patch")
     
        /* 动态匹配action
        "/user/manage/$manageAction"{
            controller='user'
            action={"manage$params.manageAction"}
        }*/
    }
}
