package eu.gaiax.federatedcatalogue.repository.neo4j;

import eu.gaiax.federatedcatalogue.entity.neo4j.ServiceOffer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceOfferRepository extends Neo4jRepository<ServiceOffer, UUID> {
    ServiceOffer getByCredentialSubjectId(String credentialSubjectId);

    @Query("MATCH (e:ServiceOffering) RETURN e ORDER BY e.createdAt DESC LIMIT 1")
    ServiceOffer findLastRecord();

    @Query(value = "call{ :#{literal(#query)} } WITH DISTINCT node AS result  MATCH (result)-[r]->(p) " +
            " RETURN DISTINCT result,COLLECT(DISTINCT r) AS rels,Collect(DISTINCT p) as p \n" +
            " skip :#{literal(#skip)} limit :#{literal(#limit)}")
    List<ServiceOffer> finByQuery(@Param("query") String query, @Param("skip") int skip, @Param("limit") int limit, Pageable pageable);

    @Query(value = ":#{literal(#query)}" +
            " skip :#{literal(#skip)} limit :#{literal(#limit)}")
    List<ServiceOffer> finalAll(@Param("query") String query, @Param("skip") int skip, @Param("limit") int limit, Pageable pageable);

    @Query(value = "call{ :#{literal(#query)} }return count(node) as cnt")
    long count(@Param("query") String query);

}
