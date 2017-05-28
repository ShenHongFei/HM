package hm

import grails.boot.GrailsApp
import grails.boot.config.GrailsApplicationPostProcessor
import grails.boot.config.GrailsAutoConfiguration
import grails.core.GrailsApplication
import grails.plugin.json.view.JsonViewConfiguration

class Application extends GrailsAutoConfiguration {
    
    public static projectDir=         System.getProperty('development')?'D:/HM' as File:new File(System.getProperty('user.home'),'HM')
    public static dataDir=            new File(projectDir,'data')
    public static introductionDir=    new File(dataDir,'introduction')
    public static newsDir=            new File(dataDir,'news')
    public static uploadTmpDir=       new File(dataDir,'uploadTmp')
    
    public static InputStream resetEmailTemplate= this.classLoader.getResourceAsStream('reset-email-template.html')
    
    //初始化数据目录
    static{
        projectDir          .mkdirs()
        assert projectDir.exists()
        dataDir             .mkdirs()
        introductionDir     .mkdirs()
        newsDir             .mkdirs()
        uploadTmpDir        .mkdirs()
    }
    
    
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}