package hm

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

import java.text.SimpleDateFormat

class Application extends GrailsAutoConfiguration {
    
    public static def timeFormat=new SimpleDateFormat('yyyy-MM-dd a h:mm',Locale.CHINA)
    public static def fileTimeFormat=new SimpleDateFormat('yyyy-MM-dd-a-h-mm',Locale.CHINA)
    
public static File projectDir
    public static File webDir
    public static File dataDir
        
        public static File uploadDir
    
        public static File trainingDir

        public static File aboutDir
            public static File introductionDir
            public static File contactDir
            public static File galleryDir
    
//    static Boolean tableExists

    
    
    static{
        projectDir=new File(System.properties['user.dir'] as String)
        println "当前路径： $projectDir.absolutePath"
        
	    webDir=new File(projectDir,'web')

        (dataDir=                       new File(projectDir,'data'))       .mkdirs()
            
            (uploadDir=                 new File(dataDir,'upload'))               .mkdirs()
        
            (aboutDir=                  new File(dataDir,'about')).mkdirs()
                (contactDir=            new File(aboutDir,'contact')).mkdirs()
                (introductionDir=       new File(aboutDir,'introduction'))  .mkdirs()
                (galleryDir=            new File(aboutDir,'gallery')).mkdirs()
    }
    
    static getResetEmailTemplateInputStream(){
        Application.classLoader.getResourceAsStream('reset-email-template.html')
    }
    static getLawTemplateInputStream(){
        Application.classLoader.getResourceAsStream('law-template.html')
    }
    
    @Override
    void doWithApplicationContext(){
        println config
    }
    
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}


/*        Driver driver=new Driver()
        tableExists = driver.connect("jdbc:h2:file:${System.properties['development']?'D:':'~'}/HM/data/db/HM;AUTO_SERVER=TRUE;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE",[username:'root'] as Properties).getMetaData().getTables(null,null,"WORD_TYPES",null).next()*/


    