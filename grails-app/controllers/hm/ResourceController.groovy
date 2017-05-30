package hm

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRange
import org.springframework.http.MediaType
import org.springframework.http.converter.ResourceHttpMessageConverter
import org.springframework.http.converter.ResourceRegionHttpMessageConverter
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.util.*
import org.springframework.web.accept.ContentNegotiationManager
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy
import org.springframework.web.accept.ServletPathExtensionContentNegotiationStrategy
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.support.ServletContextResource
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.resource.*
import org.springframework.web.servlet.support.WebContentGenerator

import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletOutputStream
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.file.Files

import static hm.Application.dataDir
import static hm.Application.projectDir
import static hm.Application.webDir

class ResourceController{
    
    static responseFormats = ['json','gsp']
    
    def HOME_PAGE='index.html'
    
    def contentNegotiationStrategy=new PathExtensionContentNegotiationStrategy()
    
    def resource(){
        def uri=URLDecoder.decode(request.requestURI-request.contextPath,'UTF-8')
        def resource
        uri-='/ueditor/dialogs/preview'
        if(uri=='/') uri='index.html'
        if(uri.contains('/data')){
            def fileuri=uri-'/data/'
            println "uri=$fileuri"
            resource=new File(dataDir,fileuri)
        }else{
            println "uri=$uri"
            resource=new File(webDir,uri)
        }
        if(!resource.exists()||resource.directory) {
            println "资源 $uri 不存在"
            return render(view:'/failure',model:[message:"RESOURCE ${uri} NOT FOUND".toString()],status:404)
        }
        response.addHeader('Content-Length',resource.size() as String)
        render(file:resource,contentType:contentNegotiationStrategy.getMediaTypeForResource(new FileSystemResource(resource)))
    }
    
}

