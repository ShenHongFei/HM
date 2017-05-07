package model

import groovy.transform.ToString

class ModelAndView{
    public static final String DEFAULT_JSON_VIEW_NAME ='.json'

    String                     view                   = DEFAULT_JSON_VIEW_NAME

    Model                      model

    boolean asBoolean(){ model.result }
}



@SuppressWarnings( "GroovyUnusedDeclaration" )
@ToString(ignoreNulls = true,includeNames = true)
class Model extends LinkedHashMap<String,Object>{

    { init() }


    Model positive(){ result = true; this }
    Model negative(){ result = false; this }

    Model leftShift( x ){
        if( x == null ) return this
        switch(x){
            case String:
                msg << x; return this
            case Boolean:
                result = x; return this
            case User:
                this['user'] = x; return this
            case Map:
                super<<(x as Map);return this
            default:
                this['attachment'] = x; return this
        }
    }
    ModelAndView rightShift( view ){ new ModelAndView(view: view,model: this) }

    boolean asBoolean(){ result }

    def asType(x){
        if(x instanceof ModelAndView) return new ModelAndView(view: ModelAndView.DEFAULT_JSON_VIEW_NAME,model: this) }

    void init(){
        result = true
        msg=[]
    }

    boolean isModified(){!(this==new Model())}

}
