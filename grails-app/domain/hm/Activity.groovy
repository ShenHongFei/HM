package hm

class Activity extends Item{
    enum Type{
        PRESENTATION,PREPARATION
    }
    
    Type type
    
    List<User> members=[]
    
    public static File classDir=new File(Application.dataDir,'activity').with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }
}
