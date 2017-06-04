package hm

class UrlMappings {
    
    static mappings = {
        "/**"                           controller:'resource',      action:'resource'
        "/$controller/$action?"{
            /*constraints {
                
            }*/
        }
        "/user/manage/$action"          controller:'userManage'
        
        "/$controller/add/get"                 action:'addGet'
        "/$controller/add/ue"                  action:'addUE'
        "/$controller/add/submit"              action:'addSubmit'
        "/$controller/add/discard"             action:'addDiscard'
        "/$controller/update/ue"               action:'updateUE'
        "/$controller/update/submit"           action:'updateSubmit'
        
        
        
        
        "500"(view: '/error')
        "404"(view: '/notFound')
     
        /* 动态匹配action
        "/user/manage/$manageAction"{
            controller='user'
            action={"manage$params.manageAction"}
        }*/
    }
}
