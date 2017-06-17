package hm

import org.springframework.core.io.FileSystemResource
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy

import static hm.Application.dataDir
import static hm.Application.webDir

class ResourceController{
    
    static responseFormats = ['json','gsp']
    
    def contentNegotiationStrategy=new PathExtensionContentNegotiationStrategy()
    
    def resource(){
        def uri=URLDecoder.decode(request.requestURI-request.contextPath,'UTF-8')
        def resource
        uri-='/ueditor/dialogs/preview'
        if(uri=='/') uri='index.html'
        if(uri.contains('/data')){
            def dataUri=uri-'/data/'
            println "DATA-URI=\t$dataUri"
            resource=new File(dataDir,dataUri)
            response.addHeader('Content-Disposition',"attachment; filename=\"${URLEncoder.encode(resource.name,'UTF-8')}\"")
        }else{
            println "WEB-URI=\t$uri"
            resource=new File(webDir,uri)
        }
        if(!resource.exists()||resource.directory) {
            println "资源 $uri 不存在"
            return render(view:'/failure',model:[message:"RESOURCE ${uri} NOT FOUND".toString()],status:404)
        }
        response.addHeader('Content-Length',resource.size() as String)
        try{
            render(file:resource,contentType:contentNegotiationStrategy.getMediaTypeForResource(new FileSystemResource(resource)))
        }catch(any){}
    }
}

