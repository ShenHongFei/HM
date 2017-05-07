package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.validation.constraints.Pattern

@Entity
@JsonIgnoreProperties( ['hibernateLazyInitializer','handler'] )
class News{
    
    static File DIR
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    Long    id
    
    @Column( unique = true,length = 200 )
    @Pattern( regexp = /[0-9a-zA-Z\u4e00-\u9fa5_-~`· ]{1,200}/,message = '新闻标题含有非法字符或长度不正确' )
    String title
    
    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    Content content
    
    Date publishedAt
    
    static void init(File parentDir){
        DIR=new File(parentDir,'news')
        if(!DIR.exists()) DIR.mkdir()
    }

    static File getDir(long id){
        File sub = new File(DIR,"$id")
        if(!sub.exists()) sub.mkdir()
        sub
    }
    
    File getDir(){
        getDir(id)
    }

}
