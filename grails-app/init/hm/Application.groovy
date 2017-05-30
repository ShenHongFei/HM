package hm

import grails.boot.GrailsApp
import grails.boot.config.GrailsApplicationPostProcessor
import grails.boot.config.GrailsAutoConfiguration
import grails.core.GrailsApplication
import grails.plugin.json.view.JsonViewConfiguration
import org.h2.Driver

class Application extends GrailsAutoConfiguration {
    
    public static File projectDir
    public static File dataDir        
    public static File introductionDir
    public static File newsDir        
    public static File uploadTmpDir
//    static Boolean tableExists
    public static File webDir
    
    public static InputStream resetEmailTemplate
    
    static{
        projectDir=new File(System.properties['user.dir'] as String)
        println "当前路径： $projectDir.absolutePath"
        
        (dataDir=            new File(projectDir,'data'))       .mkdirs()
        (introductionDir=    new File(dataDir,'introduction'))  .mkdirs()
        (newsDir=            new File(dataDir,'news'))          .mkdirs()
        (uploadTmpDir=       new File(dataDir,'uploadTmp'))     .mkdirs()
        
        webDir=new File(projectDir,'web')
        assert webDir.exists()
        
        resetEmailTemplate= Application.classLoader.getResourceAsStream('reset-email-template.html')
        
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


    