package hm

class Content {
    
    String text
    Set<String> files=[]
    static constraints = {
        text maxSize:10000
    }
    
}

//    static hasMany = [files:String]
//    static belongsTo = [news:News]