package hm

class Item<T>{
    
    String  title
    Content content
    Boolean saved=false
    Date    publishedAt
    Date    modifiedAt
    User author

    static belongsTo = [author:User]
    
    static constraints = {
        title nullable:false,size:1..100
        /*matches:/[0-9a-zA-Z\u4e00-\u9fa5_-~`· ]{1,200}/*/
        author validator:{val,obj->val!=null}
    }
    

    @Override
    boolean equals(Object obj){
        return id==obj?.id
    }
}


/*    def beforeInsert(){
        publishedAt=new Date()
        true
    }*/

/*    def beforeUpdate(){
        modifiedAt=new Date()
        true
    }*/