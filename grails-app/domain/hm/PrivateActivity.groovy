package hm

class PrivateActivity {
    
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
        new File(Application.privateActivityDir,"$id").with{mkdirs();it}
    }

}
