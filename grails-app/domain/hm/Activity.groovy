package hm

class Activity {
    
    enum Type{
        PROJECT,PUBLIC_WELFARE
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
}
