package util

import model.Model
import model.ModelAndView
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class ModelAndViewReturnValueHandler implements HandlerMethodReturnValueHandler{

    @Override
    boolean supportsReturnType(MethodParameter returnType){ ModelAndView.class.isAssignableFrom(returnType.getParameterType()) }

    @Override
    void handleReturnValue(Object returnValue,MethodParameter returnType,ModelAndViewContainer mavContainer,NativeWebRequest webRequest) throws Exception{
        ModelAndView vm = returnValue as ModelAndView
        Model m = vm.model
        ModelMap dm = mavContainer.model

        dm.clear()
        mavContainer.viewName=vm.view
        if(m){
            m.each{if(it.key&&it?.value!=null) dm.addAttribute(it.key as String,it.value)}
        }else{
            dm.addAttribute('result',false)

            def msg = m.msg
            assert msg
            dm.addAttribute('msg',msg)

            def attachment = m.attachment
            if(attachment) dm.addAttribute('attachment',attachment)
        }
    }
}
