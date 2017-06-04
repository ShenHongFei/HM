package hm

class UrlMappings {
    
    static mappings = {
        "/**"                           controller:'resource',      action:'resource'
        "/$controller/$action?"{
            /*constraints {
                
            }*/
        }
        "/user/manage/$action"          controller:'userManage'
        "/news/add/ue"                  controller:'news',          action:'addUE'
        "/news/add/submit"              controller:'news',          action:'addSubmit'
        "/news/add/discard"              controller:'news',          action:'addDiscard'
        "/news/update/ue"               controller:'news',          action:'updateUE'
        "/news/update/submit"           controller:'news',          action:'updateSubmit'
        "500"(view: '/error')
        "404"(view: '/notFound')
     
        /* 动态匹配action
        "/user/manage/$manageAction"{
            controller='user'
            action={"manage$params.manageAction"}
        }*/
    }
}
