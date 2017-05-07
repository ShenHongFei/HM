package repo

import model.ModelAndView
import model.News
import model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface NewsRepo extends JpaRepository<News,Long>{
    
    News findByTitle(String title)
    News findById(Long id)
    News findFirstByOrderByIdDesc()
}