package repo

import model.Activity
import model.Department
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityRepo extends JpaRepository<Activity,Long>{
    Activity findByName(String name)
    Activity findById(Long id)
}