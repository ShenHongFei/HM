package hm

class Activity {
    
    enum Type{
        PRESENTATION,PREPARATION
    }
    
    String  title
    Content content
    Type type
    Boolean saved=false
    Date    publishedAt
    Date    modifiedAt
    
    List<User> members=[]

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
        new File(Application.activityDir,"$id").with{mkdirs();it}
    }
    
    @Override
    boolean equals(Object obj){
        return id==obj?.id
    }
}
