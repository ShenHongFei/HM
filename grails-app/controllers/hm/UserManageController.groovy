package hm

import grails.gorm.transactions.Transactional
import hm.User.Role

@Transactional
class UserManageController {
    
	static responseFormats = ['json']
    
    
    def get(){
        def id = params.int('id')
        def user
        if(id==null||!(user=User.get(id))) return fail('查询id不正确或用户不存在')
        render view:'/user/details',model:[user:user]
    }
    
    //高级用户参数只有role=VIP
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy      = sortParams[0]?:'id'
        def order       = sortParams[1]?:'desc'
        def users = User.findAll("from User as user where user.role='${params.role==Role.VIP.toString()?Role.VIP:Role.USER}' order by user.$sortBy $order".toString(),[max:size,offset:page*size])
        return render(view:'/my-page',model:[myPage:new MyPage(users,users.size(),size,page),template:'/user/details'])
    }
    
    def delete(){
        def paramsId=params.int('id')
        //单个
        if(paramsId){
            def user=User.get(paramsId)
            if(!user) return fail("参数 id=${params.id} 不正确或用户不存在")
            user.delete()
            return success('删除成功')
        }
        List<Number> ids= params.list('ids[]')?.collect{it as Integer}
        if(!ids) return fail("参数 ids=${params.ids} 不正确")
        List<User> users=User.getAll(ids).findAll{it!=null}
        User.deleteAll(users)
        return batchSuccess('删除成功',ids,users)
    }
    
    //批量设置权限 params ids,role 若ids中某id对应的用户不存在则忽略
    def setPermission(){
        if(![Role.USER,Role.VIP]*.toString().contains(params.role)) return fail("参数 role=$params.role 不正确")
        def role=Role.valueOf(params.role)
        //单个用户权限设置
        def id=params.int('id')
        if(id){
            def user=User.get(id)
            if(!user) return fail("参数 id=${params.id} 不正确或用户不存在")
            user.role=role
            return success('设置权限成功')
        }
        List<Number> ids= params.list('ids[]')?.collect{it as Integer}
        if(!ids) return fail("参数 ids=${params.ids} 不正确")
        List<User> users=User.getAll(ids).findAll{it!=null}
        users.each{it.role=role}
        return batchSuccess('设置权限成功',ids,users)
    }
    
    //工具方法
    private def fail(String message){
        render view:'/failure',model:[message:message]
    }
    private def success(String message){
        render view:'/success',model:[message:message]
    }
    private def batchSuccess(String message,List ids,List effectives){
        render(view:'/batch',model:[message:message,ids:ids,effectives:effectives])
    }
}