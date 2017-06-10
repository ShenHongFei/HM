package hm

class Notice extends Item{
    
    enum Type{
        PROJECT,HM
    }
    Type type
    
    public static File classDir=new File(Application.dataDir,'notice').with{mkdirs();it}.with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }
}
