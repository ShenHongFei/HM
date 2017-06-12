package hm

import grails.gorm.transactions.Transactional

import java.lang.reflect.ParameterizedType

import static hm.User.GUEST

@Transactional
class ItemController<T extends Item>{
    
	static responseFormats = ['json']
    EditorService  editorService
    
    //用户添加新闻时先初始化UE，创建某一id的新闻，设置状态为未保存， 文件存放在 item/id 中，并将该新闻放入session(不允许用户同时编辑两个新闻）
    def addUE(){
        //暂时不禁止有正在编辑新闻时再次初始化UE
/*        if(session.itemInEdit){
            return render(view:'item-in-edit',model:[item:session.itemInEdit])
        }*/
        session.user.attach()
        itemInEdit=itemClass.newInstance(title:'unsaved',author:session.user==GUEST?null:session.user).save(failOnError:true)
        render editorService.processUEAction(request,response,itemInEdit.dir)
    }
    
    // params title,content 
    // 新闻提交后需要重新初始化UE
    @SuppressWarnings("GroovyAssignabilityCheck")
    def addSubmit(){
        if(!itemInEdit) return fail("提交失败，找不到正在编辑的${itemClassName}，可能是UE自上次提交后没有重新初始化")
        //相对ItemController增加类型校验及设置
        if(itemClass.declaredFields*.toString().grep(~/.*department.*/)){
            def departmentParam=params.department?:params.type
            if(!Department.values()*.toString().contains(departmentParam)) return fail("参数不正确，可能的值=${Department.values()}")
            itemInEdit.department=Department.valueOf(departmentParam)
        }
        if(itemClass.declaredFields*.toString().grep(~/.*${itemClass.simpleName}\.type.*/)){
            def typeEnumClass = Class.forName("$itemClass.name\$Type")
            if(!typeEnumClass.enumConstants*.toString().contains(params.type)) return fail("参数 type=$params.type 不正确，可能的type=${typeEnumClass.enumConstants}")
            itemInEdit.type=typeEnumClass.valueOf(params.type)
        }
        
        def files=[] as Set<String>
        itemInEdit.dir.listFiles().each{
            if(!params.content?.contains(it.name)) it.delete()
            else files<<it.name
        }
        itemInEdit.with{
            title=params.title
            content=new Content(text:params.content,files:files)
            modifiedAt=publishedAt=new Date()
        }
        //todo: 别的方法!!
        itemInEdit.attach()
        itemInEdit.author.attach()
        if(!itemInEdit.validate(deepValidate:false)) return fail(itemInEdit,'添加失败')
        itemInEdit.saved=true
        itemInEdit=null
        success message:'添加成功',obj:itemInEdit
    }
    
    def addDiscard(){
        if(!itemInEdit){
            return fail("没有正在编辑中的$itemClassName")
        }
        itemInEdit.with{
            it.dir.deleteDir()
            it.delete()
        }
        itemInEdit=null
        success message:'废弃成功'
    }
    
    //todo:分页参数为负值处理
    @SuppressWarnings("GroovyAssignabilityCheck")
    def list(){
        def page        = (params.page?:0) as Integer
        def size        = (params.size?:10) as Integer
        def sortParams  = ((params.sort as String)?.split(',') as List)?:[]
        def sortBy='modifiedAt'
        if(sortParams[0]&&itemClass.hasProperty(sortParams[0])) sortBy=sortParams[0]
        def order='desc'
        if(sortParams[1]&&['asc','desc'].contains(sortParams[1])) order=sortParams[1]
    
        //类型校验及设置
        if(itemClass.declaredFields*.toString().grep(~/.*department.*/)){
            def departmentParam=params.department?:params.type
            if(!Department.values()*.toString().contains(departmentParam)) return fail("参数不正确，可能的值=${Department.values()}")
            //类型过滤,分页及计数
            return pagenate(new MyPage(itemClass.findAll("from ${itemClassName} as item where item.saved=true and item.department='$departmentParam' order by item.$sortBy $order".toString(),[max:size,offset:page*size]),itemClass.countBySavedAndDepartment(true,departmentParam),size,page))
        }
        if(itemClass.declaredFields*.toString().grep(~/.*${itemClass.simpleName}\.type.*/)){
            def typeEnumClass = Class.forName("$itemClass.name\$Type")
            if(!typeEnumClass.enumConstants*.toString().contains(params.type)) return fail("参数 type=$params.type 不正确，可能的type=${typeEnumClass.enumConstants}")
            
            return pagenate(new MyPage(itemClass.findAll("from ${itemClassName} as item where item.saved=true and item.type='${params.type}' order by item.$sortBy $order".toString(),[max:size,offset:page*size]),itemClass.countBySavedAndType(true,params.type),size,page))
        }
        
        def items = itemClass.findAll("from ${itemClassName} as item where item.saved=true order by item.$sortBy $order".toString(),[max:size,offset:page*size])
        pagenate(new MyPage(items,itemClass.countBySaved(true),size,page))
    }
    
    //params id
    def delete(){
        //校验存在
        Item item
        def id = params.int('id')
        if(id==null||!(item=Item.get(id))||!item.saved) return fail("id=$id 的${itemClassName}不存在或处于草稿状态，无法删除")
        item.dir.deleteDir()
        item.delete()
        success(message:'删除成功')
    }
    
    def updateUE(){
        //校验存在
        Item item
        def id = params.int('id')
        if(id==null||!(item=itemClass.get(id))||!item.saved) return fail("id=$id 的${itemClassName}不存在")
        render editorService.processUEAction(request,response,item.dir)
    }
    
    //params id,title,content
    def updateSubmit(){
        //校验存在
        Item item
        def id = params.int('id')
        if(id==null||!(item=itemClass.get(id))||!item.saved) return fail("id=$id 的${itemClassName}不存在,可能是id错误或未成功提交")
    
        def fileNames=[] as Set<String>
        item.dir.listFiles().each{
            if(!params.content.contains(it.name)) it.delete()
            else fileNames<<it.name
        }
        item.with{
            title=params.title
            content.text=params.content
            content.files=fileNames
            modifiedAt=new Date()
        }
        item.author.attach()
        if(!item.validate()) return fail(item,'修改失败')
        success(message:'修改成功',obj:item)
    }
    
    def addGet(){
        if(!itemInEdit) return fail("没有正在编辑的${itemClassName}")
        success obj:itemInEdit
    }
    
    def get(){
        //校验存在
        def id = params.int('id')
        def item=itemClass.get(id)
        if(!item) return fail("id=$id 的${itemClassName}不存在")
        if(!item.saved) return fail("id=$id 的${itemClassName}未成功提交，处于草稿状态")
        success obj:item
    }
    
    protected getItemClass(){
        (this.class.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class
    }
    protected getItemClassName(){
        itemClass.simpleName
    }
    protected setItemInEdit(Item item){
        session["${itemName}InEdit"]=item
    }
    protected getItemInEdit(){
        session["${itemName}InEdit"] as Item
    }
    protected getItemName(){
        itemClass.simpleName[0].toLowerCase()+itemClass.simpleName[1..-1]
    }
    
    protected fail(obj,String failureMessage){
        render view:'/failure',model:[errors:obj?.errors,message:failureMessage]
    }
    protected fail(String failureMessage){
        render view:'/failure',model:[message:failureMessage]
    }
    protected fail(String failureMessage,String log){
        render view:'/failure',model:[message:failureMessage,log:log]
    }
    protected success(Map model){
        render view:'/success',model:model
    }
    protected pagenate(MyPage myPage){
        render view:'/my-page',model:[myPage:myPage]
    }
}
