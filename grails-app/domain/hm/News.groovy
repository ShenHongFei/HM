package hm

class News {
    
    String  title
    Boolean saved=false
    Date    publishedAt
    Date    modifiedAt
    Content content
    
    static constraints = {
        title nullable:false,matches:/[0-9a-zA-Z\u4e00-\u9fa5_-~`· ]{1,200}/
    }
    
    def beforeUpdate(){
        modifiedAt=new Date()
    }
}
//    static hasOne = [content:Content]
