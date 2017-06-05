package hm

class ActivityNews{
    
    String  title
    Content content
    Boolean saved=false
    Date    publishedAt
    Date    modifiedAt
    
    static constraints = {
        title nullable:false,size:1..100
        /*matches:/[0-9a-zA-Z\u4e00-\u9fa5_-~`· ]{1,200}/*/
    }
    
    def beforeInsert(){
        publishedAt=new Date()
        true
    }
    
    def beforeUpdate(){
        modifiedAt=new Date()
        true
    }
    
    def getDir(){
        new File(Application.newsDir,"$id").with{mkdirs();it}
    }
}
//    static hasOne = [content:Content]
