package hm

class Activity extends Item{
    
    static constraints = {
        title nullable:false,size:1..100
        author validator:{val,obj->true},cascadeValidation:false
    }
    
    List<User> members=[]
    
    public static File classDir=new File(Application.dataDir,'activity').with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }
}
