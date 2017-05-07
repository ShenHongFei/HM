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
class Notice{

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    Long    id

    @Column( unique = true,length = 20 )
    @Pattern( regexp = /[0-9a-zA-Z\u4e00-\u9fa5_-~`· ]{1,20}/,message = '项目名含有非法字符或长度不正确' )
    String  name

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    Content content

    Date publishAt

    String getContextPath(){ "project/$id" }

    Map getContent(){
        def fl=[]
        content.files?.each{
            fl<<[name:it.name,path:"file/$contextPath/$it.name".toString()]
        }
        [id:id,
         name:name,
         text:content.text,
         files:fl]
    }

    Map getInfo(){
        [id:id,
         name:name]
    }
}
