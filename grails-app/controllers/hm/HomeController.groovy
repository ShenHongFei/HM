package hm

class HomeController {
	static responseFormats = ['json','html', 'xml']
	
    def index() {
        render 'home!'
    }
}
