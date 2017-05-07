package aspect

import model.Model
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

import static model.ModelAndView.DEFAULT_JSON_VIEW_NAME as JSON


@Aspect
@Component
@SuppressWarnings("GrMethodMayBeStatic")
class ModelAndViewSystemAspect{

    private static final Logger l = LogManager.getLogger()

    @Around('execution(public * system.*.*(..))')
    FirstInitModelLastAssemblyModelAndView(ProceedingJoinPoint pjp){
        l.info('初始化模型')
        def m = pjp.target.m as Model
        m.clear()
        m.init()

        def retVal = pjp.proceed()

        switch(retVal?.getClass()){
            case String:
                log(retVal,null)
                return retVal
            case Page:
                log('page',retVal)
                return retVal
            case Model:
                log(JSON,retVal)
                return retVal>>JSON
            case null:
                if(!m.isModified()) return null
                log(JSON,m)
                return m>>JSON
            default:
                return retVal
        }
    }

    private log(v,m){
        l.info("""
            组装模型与视图：
            视图：       $v
            模型：       $m
            """)
    }
}
