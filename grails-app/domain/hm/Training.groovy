package hm

class Training extends Item {
    
    Department department
    
    public static File classDir=new File(Application.dataDir,'training').with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }
}
