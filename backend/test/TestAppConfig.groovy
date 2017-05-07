import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.apache.commons.dbcp2.BasicDataSource
import org.hibernate.dialect.MySQL57InnoDBDialect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.io.FileSystemResource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.oxm.xstream.XStreamMarshaller
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.view.InternalResourceViewResolver
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import org.springframework.web.servlet.view.xml.MarshallingView
import util.ModelAndViewReturnValueHandler
import util.SuffixViewResolver

import javax.sql.DataSource


@Configuration
//@ComponentScan(basePackages = [/*'repo','system','util','model','aspect','service'*/])
class TestAppConfig{



//    邮件
    @Bean('mailSender')
    JavaMailSenderImpl getJavaMailSenderImpl(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl()
        mailSender.host='smtp.qq.com'
        mailSender.port=465//端口号，QQ邮箱需要使用SSL，端口号465或587
        mailSender.username='350986489'
        mailSender.password='pniljmfgcgzobiij'
        mailSender.defaultEncoding='UTF-8'
        mailSender.javaMailProperties=[
                'mail.smtp.timeout':25000,
                'mail.smtp.auth':true,
                'mail.smtp.starttls.enable':true,//STARTTLS是对纯文本通信协议的扩展。它提供一种方式将纯文本连接升级为加密连接（TLS或SSL）
                'mail.smtp.socketFactory.port':465,
                'mail.smtp.socketFactory.class':'javax.net.ssl.SSLSocketFactory',
                'mail.smtp.socketFactory.fallback':false,
        ] as Properties
        mailSender
    }


}