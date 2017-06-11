package hm

class ActivityController extends ItemController<Activity>{
	
    def lawGet(){
        render(Application.lawTemplateInputStream.text,contentType:'text/html')
    }
    
    def signup(){
        def errors=[]
        ['email','phone','realname','gender','age','company','id'].each{
            if(!params[it]) errors<<"$it 不能为空"
        }
        if(errors) return fail(errors.toString())
        def activity=Activity.get(params.int('id'))
        if(!activity) return fail("活动不存在")
        if(!User.Gender.values()*.toString().contains(params.gender)) return fail("参数 gender=$params.gender 不正确")
        def user=User.findByEmail(params.email)
        if(user&&activity.members.contains(user)) return fail("您已用该邮箱报名")
        if(!user) user=new User(email:params.email)
        user.with{
            phone=params.phone
            realname=params.realname
            //todo:校验
            gender=User.Gender.valueOf(params.gender)
            age=params.int('age')
            company=params.company
            address=params.address
        }
        if(!user.validate()) return fail(user,'请检查邮箱格式')
        activity.members<<user
        user.save()
        return render(view:'/success',model:[message:'报名成功',obj:user,objTemplate:'/user/info'])
    }
    
    
    def listMembers(){
        def activity=Activity.get(params.int('id'))
        if(!activity) return fail("id=$params.id 的活动不存在")
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        //todo:为什么渲染出来的content中所有的用户都一样
        def members=activity.members.collect{new User(id:it.id,phone:it.phone,realname:it.realname,gender:it.gender,address:it.address,company:it.company,age:it.age,email:it.email,role:it.role)}
        if(!members) return render(view:'/my-page',model:[myPage:new MyPage([],0,size,page),template:'/user/info'])
        def offset=0<=page*size&&page*size<=members.size()-1?page*size:0
        def upperBound=offset+size<=members.size()-1?offset+size:members.size()-1
        render(view:'/my-page',model:[myPage:new MyPage(members[offset..upperBound],members.size(),size,page),template:'/user/info'])
    }
    
    def query(){
        ['email','id'].each{
            if(!params[it]) return fail("$it 不能为空")
        }
        def activity=Activity.get(params.int('id'))
        if(!activity) return fail("id=$params.id 的活动不存在")
        def user = User.findByEmail(params.email)
        if(!user) return fail('找不到符合条件的用户')
        if(!activity.members.contains(user)) return fail("该用户未报名 id=$activity.id 的活动")
        success message:"用户已报名 id=$activity.id 的活动",obj:activity
    }
    
    //params id
    def delete(){
        //校验存在
        Activity activity
        def id = params.int('id')
        if(id==null||!(activity=Activity.get(id))||!activity.saved) return fail("id=$id 的活动不存在")
        activity.dir.deleteDir()
        activity.delete()
        success message:'删除成功'
    }
    
}
