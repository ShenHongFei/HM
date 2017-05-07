package repo

import model.ModelAndView
import model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepo extends JpaRepository<User,Long>{

    User findByUsername(String username)
    User findByCookieId(String cookieId)
    User findByEmail(String email)
    User findById(Long id)
}