package hm

class UrlMappings {
    
    static mappings = {
        "/**"                           controller:'resource',      action:'resource'
        "/$controller/$action?"{
            /*constraints {
                
            }*/
        }
        
        "/user/manage/$action"          controller:'userManage'
        '/user/manage/invitation-code/get'          controller:'userManage',action:'invitationCodeGet'
        '/user/manage/invitation-code/set'          controller:'userManage',action:'invitationCodeSet'
        
        "/$controller/add/get"                 action:'addGet'
        "/$controller/add/ue"                  action:'addUE'
        "/$controller/add/submit"              action:'addSubmit'
        "/$controller/add/discard"             action:'addDiscard'
        "/$controller/update/ue"               action:'updateUE'
        "/$controller/update/submit"           action:'updateSubmit'
        "/$controller/list"                    action:'list'
        
        "/about/$type/$action"          controller:'about'
        
        "/activity/law/get"              controller:'activity',  action:'lawGet'
        
        
        "500"(view: '/error')
        "404"(view: '/notFound')
     
        /* 动态匹配action
        "/user/manage/$manageAction"{
            controller='user'
            action={"manage$params.manageAction"}
        }*/
    }
}
