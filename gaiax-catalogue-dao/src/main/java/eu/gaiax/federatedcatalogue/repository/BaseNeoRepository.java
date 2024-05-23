package eu.gaiax.federatedcatalogue.repository;

import com.smartsensesolutions.java.commons.base.entity.BaseEntity;
import eu.gaiax.federatedcatalogue.neo4j.GaiaxEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;
@NoRepositoryBean
public interface BaseNeoRepository <T extends GaiaxEntity, ID>  extends Neo4jRepository<T, ID> {

    Page<T> findAll(String Query, Pageable pageable);

    /**
     * Method used for fetch count based on the JPA specification.
     ** @return Indicates the elements count.
     */
    long count(String Query);
}
