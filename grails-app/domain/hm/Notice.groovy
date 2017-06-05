package hm

class Notice {
    
    enum Type{
        PROJECT,HM
    }
    
    String  title
    Content content
    Type type
    Boolean saved=false
    Date    publishedAt
    Date    modifiedAt
    
    static constraints = {
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
        new File(Application.noticeDir,"$id").with{mkdirs();it}
    }
}