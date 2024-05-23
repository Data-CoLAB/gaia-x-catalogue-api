package eu.gaiax.federatedcatalogue.repository.neo4j;

import eu.gaiax.federatedcatalogue.entity.neo4j.DataProtectionRegime;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DataProtectionRegimeRepository extends Neo4jRepository<DataProtectionRegime, UUID> {
}
