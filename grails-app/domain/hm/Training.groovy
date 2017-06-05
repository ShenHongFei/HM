package hm

class Training {
    
    String  title
    Content content
    Department department
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
        new File(Application.trainingDir,"$id").with{mkdirs();it}
    }
}
