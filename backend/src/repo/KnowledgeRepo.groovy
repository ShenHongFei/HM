package repo

import model.Knowledge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface KnowledgeRepo extends JpaRepository<Knowledge,Long>{
    
}