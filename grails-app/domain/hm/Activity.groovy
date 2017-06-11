package hm

class Activity extends Item{
    
    static constraints = {
        title nullable:false,size:1..100
        author validator:{val,obj->true},cascadeValidation:false
    }
    
    List<User> members=[]
    static hasMany = [members:User]
    static mapping = {
        members lazy: false
    }
    
    public static File classDir=new File(Application.dataDir,'activity').with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }
}
