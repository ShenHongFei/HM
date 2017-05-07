import config.WebAppConfig

class FilePath{
    static void main(String... args){
        String relativePath = WebAppConfig.FILE_DIR.toPath().relativize(new File(WebAppConfig.FILE_DIR,'introduction/aaa.txt').toPath())
        println relativePath.replace('\\','/')
    }
}
