package repo

import model.Department
import org.springframework.data.domain.Example
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DepartmentRepo extends JpaRepository<Department,Long>{

    Department findByName(String name)
    Department findById(Long id)
}
