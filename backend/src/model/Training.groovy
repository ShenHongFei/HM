package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.*
import javax.validation.constraints.Pattern

@Entity
@JsonIgnoreProperties( ['hibernateLazyInitializer','handler'] )
class Training{

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    Long   id

    @Column
    String name
    
    @ManyToOne
    Department department

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    Content content

    String getContextPath(){ "training/$id" }

    Map getContent(){
        def fl=[]
        content?.files?.each{
            fl<<[name:it.name,path:"file/$contextPath/$it.name".toString()]
        }
        [id:id,
         name:name,
         department:department.info,
         text:content?.text,
         files:fl,]
    }

    Map getInfo(){
        [id:id,
         name:name,
         department:department.info,]
    }
}
