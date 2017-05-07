package ueditor

import org.json.JSONArray
import org.json.JSONObject
import ueditor.defination.ActionMap

class UEditorConfig{

    static final String configFileName = "ueditor-config.json"
    JSONObject cfgObj = null
    // 涂鸦上传filename定义
    final static String SCRAWL_FILE_NAME = "scrawl"
    // 远程图片抓取filename定义
    final static String REMOTE_FILE_NAME = "remote"

    /*
     * 通过一个给定的路径构建一个配置管理器， 该管理器要求地址路径所在目录下必须存在config.properties文件
     */

    UEditorConfig(){
        try{
            String configContent = readConfigFile(new File(this.class.classLoader.getResource('ueditor/ueditor-config.json').toURI()))
            cfgObj=new JSONObject(configContent)
        }catch(Exception e){
            e.printStackTrace()
        }
    }

    // 验证配置文件加载是否正确
    boolean valid(){
        return this.cfgObj!=null
    }

    Map<String,Object> getConfig(int type){

        Map<String,Object> conf = new HashMap<String,Object>()
        String savePath = null

        switch(type){
            case ActionMap.UPLOAD_FILE:
                conf.put("isBase64","false")
                conf.put("maxSize",this.cfgObj.getLong("fileMaxSize"))
                conf.put("allowFiles",this.getArray("fileAllowFiles"))
                conf.put("fieldName",this.cfgObj.getString("fileFieldName"))
                savePath=this.cfgObj.getString("filePathFormat")
                break

            case ActionMap.UPLOAD_IMAGE:
                conf.put("isBase64","false")
                conf.put("maxSize",this.cfgObj.getLong("imageMaxSize"))
                conf.put("allowFiles",this.getArray("imageAllowFiles"))
                conf.put("fieldName",this.cfgObj.getString("imageFieldName"))
                savePath=this.cfgObj.getString("imagePathFormat")
                break

            case ActionMap.UPLOAD_VIDEO:
                conf.put("maxSize",this.cfgObj.getLong("videoMaxSize"))
                conf.put("allowFiles",this.getArray("videoAllowFiles"))
                conf.put("fieldName",this.cfgObj.getString("videoFieldName"))
                savePath=this.cfgObj.getString("videoPathFormat")
                break

            case ActionMap.UPLOAD_SCRAWL:
                conf.put("filename",SCRAWL_FILE_NAME)
                conf.put("maxSize",this.cfgObj.getLong("scrawlMaxSize"))
                conf.put("fieldName",this.cfgObj.getString("scrawlFieldName"))
                conf.put("isBase64","true")
                savePath=this.cfgObj.getString("scrawlPathFormat")
                break

            case ActionMap.CATCH_IMAGE:
                conf.put("filename",REMOTE_FILE_NAME)
                conf.put("filter",this.getArray("catcherLocalDomain"))
                conf.put("maxSize",this.cfgObj.getLong("catcherMaxSize"))
                conf.put("allowFiles",this.getArray("catcherAllowFiles"))
                conf.put("fieldName",this.cfgObj.getString("catcherFieldName")+"[]")
                savePath=this.cfgObj.getString("catcherPathFormat")
                break

            case ActionMap.LIST_IMAGE:
                conf.put("allowFiles",this.getArray("imageManagerAllowFiles"))
                conf.put("dir",this.cfgObj.getString("imageManagerListPath"))
                conf.put("count",this.cfgObj.getInt("imageManagerListSize"))
                break

            case ActionMap.LIST_FILE:
                conf.put("allowFiles",this.getArray("fileManagerAllowFiles"))
                conf.put("dir",this.cfgObj.getString("fileManagerListPath"))
                conf.put("count",this.cfgObj.getInt("fileManagerListSize"))
                break

        }
        conf.put("savePath",savePath)
        return conf
    }


    String[] getArray(String key){
        JSONArray jsonArray = this.cfgObj.getJSONArray(key)
        String[] result = new String[jsonArray.length()]
        int len = jsonArray.length();
        for(int i = 0;i<len;i++){
            result[i]=jsonArray.getString(i)
        }
        return result
    }

    String readConfigFile(File configFile) throws IOException{
        StringBuilder builder = new StringBuilder()
        try{
            InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile),"UTF-8")
            BufferedReader bfReader = new BufferedReader(reader)
            String tmpContent
            while((tmpContent=bfReader.readLine())!=null){
                builder.append(tmpContent)
            }
            bfReader.close()
        }catch(UnsupportedEncodingException e){
        }
        return filter(builder.toString())
    }

    // 过滤输入字符串, 剔除多行注释以及替换掉反斜杠
    static String filter(String input){
        return input.replaceAll("/\\*[\\s\\S]*?\\*/","")
    }
    
    static void main(String... args){
        println new UEditorConfig().cfgObj
    }

}
