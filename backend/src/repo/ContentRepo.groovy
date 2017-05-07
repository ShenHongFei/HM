package repo

import model.Content
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContentRepo extends JpaRepository<Content,Long>{

}