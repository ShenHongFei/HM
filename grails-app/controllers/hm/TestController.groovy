package hm

import grails.gorm.transactions.Transactional
import grails.rest.*
import grails.converters.*
import groovy.json.JsonBuilder

@Transactional
class TestController {
	static responseFormats = ['json']
	
    def index() {
        
    }
}
