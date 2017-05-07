import config.WebAppConfig
import groovy.text.SimpleTemplateEngine
import model.User
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.web.context.ConfigurableWebApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

import javax.mail.MessagingException
import javax.mail.internet.MimeMessage


class SendEmailTest{
    static void main(String... args){
//        uuidTest()
//        resourceTest()
        fillTest()
    }
    
    static void sendTest(){
        ApplicationContext context = new AnnotationConfigApplicationContext(TestAppConfig.class)
        JavaMailSenderImpl sender = (JavaMailSenderImpl)context.getBean("mailSender")
        //构建邮件
        MimeMessage message=sender.createMimeMessage()
        try {
            //使用MimeMessageHelper构建Mime类型邮件
            MimeMessageHelper helper= new MimeMessageHelper(message,true)
            helper.setFrom("350986489@qq.com")
            helper.setTo("shf19961008@163.com")
            message.setSubject("Spring Mail Test")
            //第二个参数true表明信息类型是multipart类型
            helper.setText("<a href=\"http://45.78.7.182\">测试</a>",true)
            sender.send(message)
        } catch (MessagingException e){
            e.printStackTrace()
        }
    }
    
    static void resourceTest(){
        println ClassLoader.getSystemResource('reset-email.html').text
    }
    
    static void fillTest(){
        User u=new User(username:'aaa',id:13,resetKey:'asdfjlasjdg')
        println new SimpleTemplateEngine().createTemplate(WebAppConfig.RESET_EMAIL_TEMPLATE).make([username:u.username,id:u.id,resetKey:u.resetKey]).toString()
    }
    
    static void uuidTest(){
        println UUID.randomUUID()
    }
}
