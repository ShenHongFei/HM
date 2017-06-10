package hm

class News extends Item{
    
    public static File classDir=new File(Application.dataDir,'news').with{mkdirs();it}
    
    File getDir(){
        new File(classDir,"$id").with{mkdirs();it}
    }
}
//    static hasOne = [content:Content]
