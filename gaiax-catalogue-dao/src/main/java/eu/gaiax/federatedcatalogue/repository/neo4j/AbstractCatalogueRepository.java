package eu.gaiax.federatedcatalogue.repository.neo4j;

import eu.gaiax.federatedcatalogue.neo4j.GaiaxEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface AbstractCatalogueRepository<T extends GaiaxEntity, ID>  extends Neo4jRepository<T, UUID> {


    @Override
    Page<T> findAll(Pageable pageable);
}
