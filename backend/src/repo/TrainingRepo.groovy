package repo

import model.Department
import model.Training
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TrainingRepo extends JpaRepository<Training,Long>{
    Training findByNameAndDepartment(String name,Department dp)
}