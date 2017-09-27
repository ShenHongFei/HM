package hm

import java.text.SimpleDateFormat

class Logger{
    public static log_file=new File(Application.dataDir,"log-${Application.fileTimeFormat.format(new Date())}.txt")
    static out = new PrintStream(log_file)
    static time_format = new SimpleDateFormat('yyyy/MM/dd a h:mm',Locale.CHINA)
    
    static void log(String type,String msg){
        out.println("${time_format.format(new Date())} [$type] $msg")
    }
}
