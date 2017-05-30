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
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.resource.*
import org.springframework.web.servlet.support.WebContentGenerator

import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.file.Files

import static hm.Application.dataDir
import static hm.Application.projectDir

class ResourceController{
    
    static responseFormats = ['json','gsp']
    
    def HOME_PAGE='index.html'
    def webResourceHandler=new MyResourceHttpRequestHandler()
    
    def contentNegotiationStrategy=new PathExtensionContentNegotiationStrategy()
    
    def web(){
        println "请求Web资源路径：$uri"
        if(new File(projectDir,uri).directory) return listDir()
        File file=new File("$projectDir/web$uri")
        if(!file.exists()) file=new File("${projectDir}${uri}")
        if(!file.exists()) return render(status:HttpServletResponse.SC_NOT_FOUND)
    
        def resource = new FileSystemResource(file)
        if (new ServletWebRequest(request,response).checkNotModified(resource.lastModified())) return
        
        
        render(file:file,contentType:contentNegotiationStrategy.getMediaTypeForResource(resource))
//        webResourceHandler.handleRequest(request,response,servletContext,uri)
    }
    
    def file(){
        String fileuri = uri-'/file/'
        println "请求文件路径：$fileuri"
        def file = new File(dataDir,fileuri)
        if(!file.exists()) return render(view:'/failure',model:[message:"FILE ${fileuri} NOT FOUND".toString()],status:404)
        response.addHeader('Content-Length',file.size() as String)
        file.withDataInputStream{ is->
            response.outputStream<<is
        }
        return 
    }
    
    def upload(){
        
    }
    
    def listDir(){
        render view:'list-dir',model:[dir:new File(projectDir,uri)]
    }
    
    def getUri(){
        URLDecoder.decode(request.requestURI-request.contextPath,'UTF-8')
    }
    

/**
 * {@code HttpRequestHandler} that serves static resources in an optimized way
 * according to the guidelines of Page Speed, YSlow, etc.
 *
 * <p>The {@linkplain #setLocations "locations"} property takes a list of Spring
 * {@link org.springframework.core.io.Resource} locations from which static resources are allowed to
 * be served by this handler. Resources could be served from a classpath location,
 * e.g. "classpath:/META-INF/public-web-resources/", allowing convenient packaging
 * and serving of resources such as .js, .css, and others in jar files.
 *
 * <p>This request handler may also be configured with a
 * {@link #setResourceResolvers(List) resourcesResolver} and
 * {@link #setResourceTransformers(List) resourceTransformer} chains to support
 * arbitrary resolution and transformation of resources being served. By default a
 * {@link org.springframework.web.servlet.resource.PathResourceResolver} simply finds resources based on the configured
 * "locations". An application can configure additional resolvers and
 * transformers such as the {@link org.springframework.web.servlet.resource.VersionResourceResolver} which can resolve
 * and prepare URLs for resources with a version in the URL.
 *
 * <p>This handler also properly evaluates the {@code Last-Modified} header (if
 * present) so that a {@code 304} status code will be returned as appropriate,
 * avoiding unnecessary overhead for resources that are already cached by the
 * client.
 *
 * @author Keith Donald
 * @author Jeremy Grelle
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 3.0.4
 */
    @SuppressWarnings("GroovyDocCheck")
    class MyResourceHttpRequestHandler extends WebContentGenerator{
        // Servlet 3.1 setContentLengthLong(long) available?
         static  boolean contentLengthLongAvailable = ClassUtils.hasMethod(ServletResponse.class, "setContentLengthLong", long.class)
    
          List<Resource> locations = new ArrayList<Resource>(4)
          List<ResourceResolver> resourceResolvers
         ResourceHttpMessageConverter resourceHttpMessageConverter=new ResourceHttpMessageConverter()
         ResourceRegionHttpMessageConverter resourceRegionHttpMessageConverter
         PathExtensionContentNegotiationStrategy contentNegotiationStrategy=new PathExtensionContentNegotiationStrategy()
    
        MyResourceHttpRequestHandler(){
            super(HttpMethod.GET.name(), HttpMethod.HEAD.name())
        }
    
        /**
         * Processes a resource request.
         * <p>Checks for the existence of the requested resource in the configured list of locations.
         * If the resource does not exist, a {@code 404} response will be returned to the client.
         * If the resource exists, the request will be checked for the presence of the
         * {@code Last-Modified} header, and its value will be compared against the last-modified
         * timestamp of the given resource, returning a {@code 304} status code if the
         * {@code Last-Modified} value  is greater. If the resource is newer than the
         * {@code Last-Modified} value, or the header is not present, the content resource
         * of the resource will be written to the response with caching headers
         * set to expire one year in the future.
         */
        void handleRequest(HttpServletRequest request,HttpServletResponse response,ServletContext servletContext,String uri) throws ServletException, IOException {
            Resource resource=new FileSystemResource("$projectDir/web$uri")
            if(!resource.exists()) resource=new FileSystemResource("${projectDir}${uri}")
//            Resource resource = new ServletContextResource(servletContext,uri=='/'?HOME_PAGE:uri)
        
            if (!resource?.exists()) {
                println "文件未找到： $uri  "
                response.sendError(HttpServletResponse.SC_NOT_FOUND)
                return
            }
        
            if (HttpMethod.OPTIONS.matches(request.method)) {
                response.setHeader("Allow", getAllowHeader())
                return
            }
        
            // Supported methods and required session
            checkRequest(request)
        
            // Header phase
            if (new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
                return
            }
        
            // Apply cache settings, if any
            prepareResponse(response)
        
            // Check the media type for the resource
            MediaType mediaType = getMediaType(request, resource)
        
            // Content phase
            if (METHOD_HEAD==request.method) {
                setHeaders(response, resource, mediaType)
                return
            }
        
            ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response)
            if (request.getHeader(HttpHeaders.RANGE) == null) {
                setHeaders(response, resource, mediaType)
                this.resourceHttpMessageConverter.write(resource, mediaType, outputMessage)
                return
            }
            
            response.setHeader(HttpHeaders.ACCEPT_RANGES,"bytes")
            ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request)
            try{
                List<HttpRange> httpRanges = inputMessage.getHeaders().getRange()
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT)
                if(httpRanges.size()==1){
                    ResourceRegion resourceRegion = httpRanges.get(0).toResourceRegion(resource)
                    this.resourceRegionHttpMessageConverter.write(resourceRegion,mediaType,outputMessage)
                }else{
                    this.resourceRegionHttpMessageConverter.write(HttpRange.toResourceRegions(httpRanges,resource),mediaType,outputMessage)
                }
            }
            catch(IllegalArgumentException ex){
                response.setHeader("Content-Range","bytes */"+resource.contentLength())
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
            }
        }
        
        
        
        
        /**
         * Determine the media type for the given request and the resource matched
         * to it. This implementation tries to determine the MediaType based on the
         * file extension of the Resource via
         * {@link ServletPathExtensionContentNegotiationStrategy#getMediaTypeForResource}.
         * @param request the current request
         * @param resource the resource to check
         * @return the corresponding media type, or {@code null} if none found
         */
        @SuppressWarnings("deprecation")
        protected MediaType getMediaType(HttpServletRequest request,Resource resource) {
            return this.contentNegotiationStrategy.getMediaTypeForResource(resource)
        }
        
        
        /**
         * Set headers on the given servlet response.
         * Called for GET requests as well as HEAD requests.
         * @param response current servlet response
         * @param resource the identified resource (never {@code null})
         * @param mediaType the resource's media type (never {@code null})
         * @throws IOException in case of errors while setting the headers
         */
        protected void setHeaders(HttpServletResponse response,Resource resource,MediaType mediaType) throws IOException {
            long length = resource.contentLength()
            if (length > Integer.MAX_VALUE) {
                if (contentLengthLongAvailable) response.setContentLengthLong(length)
                else response.setHeader(HttpHeaders.CONTENT_LENGTH,Long.toString(length)) }
            else{
                response.setContentLength((int) length)
            }
            
            if (mediaType != null) {
                response.setContentType(mediaType.toString())
            }
            if (resource instanceof EncodedResource) {
                response.setHeader(HttpHeaders.CONTENT_ENCODING, ((EncodedResource) resource).getContentEncoding())
            }
            if (resource instanceof VersionedResource) {
                response.setHeader(HttpHeaders.ETAG, "\"" + ((VersionedResource) resource).getVersion() + "\"")
            }
            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes")
        }
        
        
        @Override
        String toString() {
            return "ResourceHttpRequestHandler [locations=" + getLocations() + ", resolvers=" + getResourceResolvers() + "]"
        }
    }
    
}

