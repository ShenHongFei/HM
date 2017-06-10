package hm

class ActivityNews extends Item<ActivityNews>{
    
    public static File classDir=new File(Application.dataDir,'activity-news').with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }
}
