package hm

class Knowledge {
    
    enum Type{
        PROJECT,WELFARE
    }
    
    String  title
    Content content
    Type type
    Boolean saved=false
    Date    publishedAt
    Date    modifiedAt
    
    static constraints = {
        title nullable:false,size:1..100
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
        new File(Application.knowledgeDir,"$id").with{mkdirs();it}
    }
}
