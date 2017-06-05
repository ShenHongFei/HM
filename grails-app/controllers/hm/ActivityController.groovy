package hm

import grails.gorm.transactions.Transactional
import org.h2.store.Page

import static Application.activityDir


@Transactional
class ActivityController {
    
	static responseFormats = ['json']
    
    EditorService  editorService
    ContentService contentService
	
    def lawGet(){
        return render(Application.lawTemplateInputStream.text,contentType:'text/html')
    }
    
    def signup(){
        def errors=[]
        ['email','phone','realname','gender','age','company','id'].each{
            if(!params[it]) errors<<"$it 不能为空"
        }
        if(errors) return fail(errors.toString())
        def activity=Activity.get(params.int('id'))
        if(!activity) return fail("id=$params.id 的活动不存在")
        if(!User.Gender.values()*.toString().contains(params.gender)) return fail("参数 gender=$params.gender 不正确")
        def user=User.findByEmail(params.email)
        if(user&&user.activities.contains(activity)) return fail("该用户已报名 id=$activity.id 的活动")
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
        if(!user.validate()) return fail(user,'报名失败,参数不符合要求')
        user.activities<<activity
        activity.members<<user
        user.save()
        return render(view:'/success',model:[message:'报名成功',obj:user,objTemplate:'/user/info'])
    }
    
    def listMembers(){
        def activity=Activity.get(params.int('id'))
        if(!activity) return fail("id=$params.id 的活动不存在")
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def members=activity.members
        def offset=0<=page*size&&page*size<=members.size()-1?page*size:0
        def upperBound=offset+size<=members.size()-1?offset+size:members.size()-1
        def myPage = new MyPage(activity.members[offset..upperBound],activity.members.size(),size,page)
        render(view:'/my-page',model:[myPage:myPage,template:'/user/info'])
    }
    
    def query(){
        ['email','phone','id'].each{
            if(!params[it]) return fail("$it 不能为空")
        }
        def activity=Activity.get(params.int('id'))
        if(!activity) return fail("id=$params.id 的活动不存在")
        def user = User.find{email==params.email&&phone==params.phone}
        if(!user) return fail('找不到符合条件的用户')
        if(!user.activities.contains(activity)) return fail("该用户未报名 id=$activity.id 的活动")
        return render(view:'/success',model:[message:"用户已报名 id=$activity.id 的活动",obj:activity,objTemplate:'/activity/info'])
    }

    
    //用户添加活动时先初始化UE，创建某一id的活动，设置状态为未保存， 文件存放在 activity/id 中，并将该活动放入session(不允许用户同时编辑两个活动）
    def addUE(){
        //暂时不禁止有正在编辑活动时再次初始化UE
/*        if(session.activityInEdit){
            return render(view:'activity-in-edit',model:[activity:session.activityInEdit])
        }*/
        def unsaved = new Activity(title:'unsaved').save()
        session.activityInEdit=unsaved
        render editorService.processUEAction(request,response,unsaved.dir)
    }
    
    // params title,content 
    // 活动提交后需要重新初始化UE
    def addSubmit(){
        def activityInEdit = session.activityInEdit
        if(!activityInEdit) return fail('提交失败，找不到正在编辑的活动，可能是UE自上次提交后没有重新初始化')
        activityInEdit.attach()
        def htmlContent = params.content
        def files=[] as Set<String>
        new File(activityDir,"$activityInEdit.id").listFiles().each{
            if(!htmlContent?.contains(it.name)) it.delete()
            else files<<it.name
        }
        activityInEdit.with{
            title=params.title
            content=new Content(text:htmlContent,files:files)
        }
        if(!activityInEdit.validate()) return fail(activityInEdit,'添加失败')
        activityInEdit.saved=true
        session.activityInEdit=null
        render(view:'/success',model:[message:'添加成功',obj:activityInEdit,objTemplate:'/activity/info'])
    }
    
    def addDiscard(){
        if(!session.activityInEdit){
            return fail('没有正在编辑中的活动')
        }
        session.activityInEdit.with{
            it.dir.deleteDir()
            it.delete()
        }
        session.activityInEdit=null
        return render(view:'/success',model:[message:'废弃成功'])
    }
    
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy      = sortParams[0]?:'modifiedAt'
        def order       = sortParams[1]?:'desc'
        def activitys = Activity.findAll("from Activity as activity where activity.saved=true order by activity.$sortBy $order".toString(),[max:size,offset:page*size])
        render view:'/my-page',model:[myPage:new MyPage(activitys,Activity.countBySaved(true),size,page),template:'/activity/info']
    }
    
    //params id
    def delete(){
        //校验存在
        Activity activity
        def id = params.int('id')
        if(id==null||!(activity=Activity.get(id))||!activity.saved) return fail("id=$id 的活动不存在")
        activity.dir.deleteDir()
        activity.delete()
        render(view:'/success',model:[message:'删除成功'])
    }
    
    def updateUE(){
        //校验存在
        Activity activity
        def id = params.int('id')
        if(id==null||!(activity=Activity.get(id))) return fail("id=$id 的活动不存在")
        render editorService.processUEAction(request,response,activity.dir)
    }
    
    //params id,title,content
    def updateSubmit(){
        //校验存在
        Activity activity
        def id = params.int('id')
        if(id==null||!(activity=Activity.get(id))||!activity.saved) return fail("id=$id 的活动不存在,可能是id错误或未成功提交")
        
        def htmlContent=params.content
        def fileNames=[] as Set<String>
        new File(activityDir,"$activity.id").listFiles().each{
            if(!htmlContent.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        activity.with{
            title=params.title
            content.text=htmlContent
            content.files=fileNames
        }
        if(!activity.validate()) return fail(activity,'修改失败')
        render view:'/success',model:[message:'修改成功',objTemplate:'/activity/info',obj:activity]
    }
    
    def addGet(){
        if(!session.activityInEdit) return fail('没有正在编辑的活动')
        return render(view: '/success',model:[obj:session.activityInEdit,objTemplate:'/activity/details'])
    }
    
    def get(){
        //校验存在
        def id = params.int('id')
        def activity=Activity.get(id)
        if(!activity) return fail("id=$id 的活动不存在")
        if(!activity.saved) return fail("id=$id 的活动未成功提交，处于草稿状态")
        return render(view: '/success',model:[obj:activity,objTemplate:'/activity/details'])
    }
    
    private def fail(def obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    private def fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
}
