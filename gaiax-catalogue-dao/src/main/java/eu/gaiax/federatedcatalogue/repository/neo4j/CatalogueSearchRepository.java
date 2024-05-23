package eu.gaiax.federatedcatalogue.repository.neo4j;

import eu.gaiax.federatedcatalogue.entity.neo4j.Node;
import eu.gaiax.federatedcatalogue.entity.neo4j.RuleRequest;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CatalogueSearchRepository extends Neo4jRepository<Node, UUID> {
    @Query("MATCH (n) RETURN DISTINCT labels(n)")
    List<Value> findDistinctNodeLabels();
    @Query("MATCH (node:ServiceOffering)-[r]->(b:`:#{literal(#label)}`) RETURN DISTINCT TYPE(r) AS rel")
    Set<String> findDistinctRelationshipTypesByLabel(@Param("label")String label);

    @Query(" MATCH (node:ServiceOffering)-[r]-> (n:`:#{literal(#label)}`) :#{literal(#where)} return DISTINCT toString(n.:#{literal(#property)})  AS name skip :#{literal(#skip)} limit :#{literal(#limit)}")
    List<String> findAllPropertyValues(@Param("label")String label,@Param("property")String property,@Param("where") String where,@Param("skip") int skip, @Param("limit") int limit);
    @Query(" MATCH (so:`:#{literal(#label)}`) UNWIND keys(so) AS propertyLabel WITH propertyLabel WHERE propertyLabel IN ['name', 'formatType','accessType','requestType'] RETURN DISTINCT propertyLabel")
    List<String> findPropertyLabel(@Param("label")String label);
    @Query(" MATCH (node:ServiceOffering)-[r]-> (n:`DataAccountExport`) with DISTINCT  apoc.coll.flatten(Collect( n.formatType))  as name with name AS array, ';' AS separator with REDUCE(mergedString = \"\",item IN array | mergedString  +CASE WHEN mergedString='' THEN '' ELSE separator END + item) AS mergedString UNWIND split(mergedString, ';') AS individualValue with distinct individualValue :#{literal(#where)}  RETURN individualValue skip :#{literal(#skip)} limit :#{literal(#limit)} ")
    List<String> findFormatTypeValues(@Param("where") String where,@Param("skip") int skip, @Param("limit") int limit);
    @Query(" MATCH (node:ServiceOffering)-[r]-> (n:`DataAccountExport`) with DISTINCT  apoc.coll.flatten(Collect( n.formatType))  as name with name AS array, ';' AS separator with REDUCE(mergedString = \"\",item IN array | mergedString  +CASE WHEN mergedString='' THEN '' ELSE separator END + item) AS mergedString UNWIND split(mergedString, ';') AS individualValue  with distinct individualValue :#{literal(#where)} RETURN count(*)")
    int  countFormatTypeValues(@Param("where") String where);
    @Query("MATCH p=((node:ServiceOffering)-[relationship]->(destination)) WHERE NONE(relationship IN [relationship IN RELATIONSHIPS(p) | TYPE(relationship)] " +
            "WHERE relationship IN ['TERMS_AND_CONDITIONS','REGISTRATION_NUMBER','DATA_ACCOUNT_EXPORT']) UNWIND LABELS(node) AS src UNWIND LABELS(destination) AS dst RETURN DISTINCT src, TYPE(LAST(RELATIONSHIPS(p))) AS rel, dst")
    List<RuleRequest> findAllRules();

    @Query("MATCH p=((node)<-[relationship]-(destination:ServiceOffering)) WHERE NONE(relationship IN [relationship IN RELATIONSHIPS(p) | TYPE(relationship)] WHERE relationship IN ['TERMS_AND_CONDITIONS','AGGREGATION_OF','LOCATED_IN','PROVIDED_BY','DATA_PROTECTION_REGIME','DEPENDS_ON']) UNWIND LABELS(node) AS src UNWIND LABELS(destination) AS dst RETURN DISTINCT src, TYPE(LAST(RELATIONSHIPS(p))) AS rel")
    List<RuleRequest> findOutRules();
    @Query("MATCH(n:`:#{literal(#label)}`)  UNWIND KEYS(n) AS keys WITH DISTINCT keys\n" +
            "WHERE keys  IN ['name', 'trustIndex','labelLevel','accessType','requestType'] return keys as  values")
    Set<String> findSource(@Param("label")String label);
    @Query(" MATCH (node:ServiceOffering)-[r]-> (n:`:#{literal(#label)}`)  RETURN DISTINCT n.name AS values")
    Set<String> findDestination(@Param("label")String label);

    @Query(value ="MATCH (node:ServiceOffering)-[r]-> (n:`:#{literal(#label)}`) :#{literal(#where)} with DISTINCT toString(n. :#{literal(#property)}) AS name with distinct name return count(*)")
    long count(@Param("label")String label,@Param("where")String where,@Param("property")String property);
}
