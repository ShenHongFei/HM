package util

import model.Model
import model.ModelAndView
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

import static model.ModelAndView.DEFAULT_JSON_VIEW_NAME as JSON


@ControllerAdvice
class BindingResultExceptionHandler{

    @ExceptionHandler( BindException.class )
    ModelAndView handle( BindException e ){
        def m=new Model()
        m.msg=e.allErrors*.defaultMessage
        -m>>JSON
    }
}
