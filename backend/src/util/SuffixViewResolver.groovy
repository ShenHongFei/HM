package util

import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.oxm.xstream.XStreamMarshaller
import org.springframework.stereotype.Component
import org.springframework.web.servlet.View
import org.springframework.web.servlet.view.BeanNameViewResolver
import org.springframework.web.servlet.view.InternalResourceViewResolver
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import org.springframework.web.servlet.view.xml.MarshallingView

@Component
class SuffixViewResolver extends BeanNameViewResolver{

    @Autowired
    MappingJackson2JsonView json
    @Autowired
    InternalResourceViewResolver jspResolver
    @Autowired
    MarshallingView xml

    @Override
    View resolveViewName( String viewName,Locale locale ) throws BeansException{
        ApplicationContext context = applicationContext
        switch(viewName.split(/\./)[-1]){
            case 'jsp' :case 'html': return jspResolver.resolveViewName(viewName,locale)
            case 'xml' : return xml
            default: return json
        }
    }
}