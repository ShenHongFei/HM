package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.validation.constraints.Pattern

@Entity
@JsonIgnoreProperties( ['hibernateLazyInitializer','handler'] )
class Project{

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    Long   id

    @Column(unique = true)
    String name
    
    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    Content content

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
