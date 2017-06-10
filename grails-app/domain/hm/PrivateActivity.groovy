package hm

class PrivateActivity extends Item{
    
    Department department
    
    public static File classDir=new File(Application.dataDir,'private-activity').with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }

}
