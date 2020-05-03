package ${groupId}.repository;

import ${groupId}.domain.${resource};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${resource}Repository extends JpaRepository<${resource}, Long>{

}
