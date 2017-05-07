package repo

import model.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepo extends JpaRepository<Project,Long>{

    Project findByName(String name)
    Project findById(Long id)
}
