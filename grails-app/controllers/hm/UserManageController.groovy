package hm

import java.util.jar.JarEntry

class UserManageController {
	static responseFormats = ['json']
	
    def getUser(){
        def id = params.int'id'
        def user
        if(id==null||!(user=User.get(id))) return fail('查询id不正确或用户不存在')
        render view:'/user/details',model:[user:user]
    }
    
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:5) as Integer
        def sortParams  = (params.sort as String)?.split(',') as List?:[]
        def sortBy      = sortParams[0]?:'id'
        def order       = sortParams[1]?:'desc'
        render view:'/mypage',
               model:[myPage:new MyPage(
                                    User.findAll("from User as u order by u.$sortBy $order".toString(),[max:size,offset:page*size]),
                                    User.count,
                                    size,
                                    page),
                      template:'/user/details']
    }
    
    //todo:用户管理方法
    def update(){
        
    }
    
    def add(){
        
    }
    
    def delete(){
        
    }
    
    //工具方法
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
}
