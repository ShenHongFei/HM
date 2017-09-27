package hm

import grails.gorm.transactions.Transactional
import hm.User.Role

@Transactional
class UserManageController {
    
	static responseFormats = ['json']
    static invitationCode
    
    
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
        def sortBy='id'
        if(sortParams[0]&&User.hasProperty(sortParams[0])) sortBy=sortParams[0]
        def order='desc'
        if(sortParams[1]&&['asc','desc'].contains(sortParams[1])) order=sortParams[1]
        Role role=params.role==Role.VIP.toString()?Role.VIP:Role.USER
        def users = User.findAll("from User as user where user.role='${role}' order by user.$sortBy $order".toString(),[max:size,offset:page*size])
        return render(view:'/my-page',model:[myPage:new MyPage(users,User.countByRole(role),size,page),template:'/user/details'])
    }
    
    def delete(){
        def paramsId=params.int('id')
        //单个
        if(paramsId){
            def user=User.get(paramsId)
            if(!user) return fail("参数 id=${params.id} 不正确或用户不存在")
            log("管理员删除了用户 $user.username")
            user.delete()
            return success('删除成功')
        }
        List<Number> ids= params.list('ids[]')?.collect{it as Integer}
        if(!ids) return fail("参数 ids=${params.ids} 不正确")
        List<User> users=User.getAll(ids).findAll{it!=null}
        User.deleteAll(users)
        log("管理员删除了${users.size()}个用户")
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
        log("管理员修改了${users.size()}个用户的权限")
        return batchSuccess('设置权限成功',ids,users)
    }
    
    def invitationCodeSet(){
        if(!params.value) return fail('无邀请码')
        if(!params.expirationTime) return fail('无过期时间')
        Date etime
        try{
            etime=new Date(params.long('expirationTime'))
        }catch(any){
            return fail('过期时间格式不对')
        }
        invitationCode=new InvitationCode(value:params.value,expirationTime:etime).with{code->
            new File(Application.dataDir,'invitation-code.ser').withObjectOutputStream{
                it<<code
            }
            code
        }
        log("管理员设置邀请码为$invitationCode.value 过期时间为$invitationCode.expirationTime")
        return success('邀请码设置成功')
    }
    
    def invitationCodeGet(){
        def icodeFile=new File(Application.dataDir,'invitation-code.ser')
        if(!icodeFile.exists()) return fail('无邀请码')
        if(!invitationCode) {
            icodeFile.withObjectInputStream{
                invitationCode=it.readObject()
            }
        }
        render view:'/user/invitation-code',model:[invitationCode:invitationCode]
    }
    
    static void log(String msg){
        Logger.log('用户管理',msg)
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