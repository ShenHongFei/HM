package hm

class Knowledge extends Item{
    
    enum Type{
        PROJECT,WELFARE
    }
    
    Type type
    
    public static File classDir=new File(Application.dataDir,'knowledge').with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }
}
