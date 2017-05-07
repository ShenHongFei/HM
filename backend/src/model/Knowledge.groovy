package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Entity
@JsonIgnoreProperties( ['hibernateLazyInitializer','handler'] )
class Knowledge{

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    Long   id

    @Column
    String name
    
    @ManyToOne
    Project project
    
    enum Category{
        PROJECT,PUBLIC_WELFARE,SHARE
    }
    @Enumerated(value = EnumType.STRING)
    @NotNull(message = '分类必须为PROJECT,PUBLIC_WELFARE,SHARE这三者之一')
    Category category

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    Content content

    String getContextPath(){ "knowledge/$id" }

    Map getContent(){
        def fl=[]
        content.files?.each{
            fl<<[name:it.name,path:"file/$contextPath/$it.name".toString()]
        }
        [id:id,
         name:name,
         category:category.toString(),
         project:project?.info,
         text:content?.text,
         files:fl]
    }

    Map getInfo(){
        [id:id,
         category:category.toString(),
         project:project?.info,
         name:name]
    }
}
