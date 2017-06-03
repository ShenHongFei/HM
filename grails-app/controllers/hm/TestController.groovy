package hm

import grails.gorm.transactions.Transactional
import grails.rest.*
import grails.converters.*

@Transactional
class TestController {
	static responseFormats = ['json', 'xml']
	
    def index() {
        def user = new User(id:100,email:'aaa@bbb.com',password:'123').save()
        println user.id
        render user.id
        
    }
}
