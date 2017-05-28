server{
    contextPath='/'
    port=80
    if(System.getProperty('PORT')){
        port=7777
    }
}

grails{
    gorm.default.constraints = {
        '*'(nullable: true)
    }
    web.url.converter = 'hyphenated'
//    views.json.compileStatic=false
    controllers.upload.maxFileSize=50000000
    controllers.upload.maxRequestSize=50000000
}

eventCompileStart = {
    projectCompiler.srcDirectories << "${basedir}/grails-app/model"
}

dataSource{
    dbCreate='update'
    username='root'
    password=''
    pooled=true
    jmxExport=true
    driverClassName='org.h2.Driver'
}

environments{
    development {
        dataSource{
            url='jdbc:h2:file:D:/HM/data/db/HM;AUTO_SERVER=TRUE;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE'
        }
    }
    production{
        dataSource{
            url='jdbc:h2:file:~/HM/data/db/HM;AUTO_SERVER=TRUE;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE'
        }
    }
    
}