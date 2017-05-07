package util

import model.Model
import model.ModelAndView
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

import static model.ModelAndView.DEFAULT_JSON_VIEW_NAME as JSON


@ControllerAdvice
class IllegalArgumentExceptionHandler{

    @ExceptionHandler( IllegalArgumentException)
    ModelAndView handle( IllegalArgumentException e ){
        def m=new Model()
        m.msg<<e.localizedMessage
        -m>>JSON
    }
}
