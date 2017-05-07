package config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer

@Configuration
class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{
//    配置AbstractAnnotationConfigDispatcherServletInitializer实现DispatcherServlet启动以及上下文装载

    @Override
    protected Class<?>[] getRootConfigClasses(){ WebAppConfig }

/*the configuration classes for the dispatcher servlet application context or
 *  null  if all configuration is specified through root config classes.*/
    @Override
    protected Class<?>[] getServletConfigClasses(){ null }


    @Override
    protected String[] getServletMappings(){ '/' }

    @Override
    protected String getServletName(){ 'springmvc' }



}
