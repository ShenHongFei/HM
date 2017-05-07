package ueditor


import javax.servlet.http.HttpServletRequest

import ueditor.defination.ActionMap
import ueditor.defination.BaseState
import ueditor.defination.State
import ueditor.hunter.FileManager
import ueditor.hunter.ImageHunter

import static ueditor.defination.AppInfo.*

class ActionEnter{

    private HttpServletRequest request = null
    private String actionType = null
    private UEditorConfig config = null
    File storeDir

    ActionEnter(HttpServletRequest request,File storeDir){
        this.request=request
        this.actionType=request.getParameter("action")
        this.config=new UEditorConfig()
        this.storeDir=storeDir
    }

    String exec(){
        String callbackName = this.request.getParameter("callback")
        if(callbackName!=null){
            if(!validCallbackName(callbackName)){
                return new BaseState(false,ILLEGAL).toJSONString()
            }
            return callbackName+"("+this.invoke()+");"
        }else{
            return invoke()
        }
    }

    String invoke(){
        if(actionType==null||!ActionMap.mapping.containsKey(actionType)){
            return new BaseState(false,INVALID_ACTION).toJSONString()
        }
        if(this.config==null||!this.config.valid()){
            return new BaseState(false,CONFIG_ERROR).toJSONString()
        }
        State state = null
        int actionCode = ActionMap.getType(this.actionType)
        Map<String,Object> conf

        switch(actionCode){

            case ActionMap.CONFIG:
                storeDir.delete()
                return this.config.cfgObj.toString()
                
            case ActionMap.UPLOAD_IMAGE:
            case ActionMap.UPLOAD_SCRAWL:
            case ActionMap.UPLOAD_VIDEO:
            case ActionMap.UPLOAD_FILE:
                state=new Uploader(request,config.getConfig(actionCode),storeDir).doExec()
                break

            case ActionMap.CATCH_IMAGE:
                conf=config.getConfig(actionCode)
                String[] list = this.request.getParameterValues((String)conf.get("fieldName"))
                state=new ImageHunter(conf).capture(list)
                break

            case ActionMap.LIST_IMAGE:
            case ActionMap.LIST_FILE:
                conf=config.getConfig(actionCode)
                int start = this.getStartIndex()
                state=new FileManager(conf).listFile(start)
                break

        }

        return state.toJSONString()

    }

    int getStartIndex(){

        String start = this.request.getParameter("start")

        try{
            return Integer.parseInt(start)
        }catch(Exception e){
            return 0
        }

    }

    /**
     * callback参数验证
     */
    boolean validCallbackName(String name){

        if(name.matches('^[a-zA-Z_]+[\\w0-9_]*$')){
            return true
        }

        return false

    }

}
