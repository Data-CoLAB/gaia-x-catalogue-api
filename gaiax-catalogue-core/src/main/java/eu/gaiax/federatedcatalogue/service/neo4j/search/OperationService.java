package eu.gaiax.federatedcatalogue.service.neo4j.search;

import eu.gaiax.federatedcatalogue.entity.neo4j.ServiceOffer;
import eu.gaiax.federatedcatalogue.model.request.And;
import eu.gaiax.federatedcatalogue.model.request.Or;
import eu.gaiax.federatedcatalogue.model.request.RecordFilter;
import eu.gaiax.federatedcatalogue.repository.neo4j.ServiceOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OperationService {

    private final ServiceOfferRepository serviceOfferRepository;

    public static void setRelationQuery(StringBuilder relation, String node, String operator, StringBuilder resultQuery, List<String> values,StringBuilder optionalQuery) {
        appendMatchRelation(relation, node, operator);
        appendCondition(relation, new StringBuilder().append("dst.name IN").append(values));
        appendResultQuery(resultQuery, relation);
    }

    public static void setOperatorQuery(StringBuilder relation, String node, String operator, String key, boolean neg,StringBuilder resultQuery, List<String> values,StringBuilder optionQuery) {
        if(neg){
            if(operator.equalsIgnoreCase("ANY") || operator.equalsIgnoreCase("IN")){
                operator="NOT IN";
            }
            if(operator.equalsIgnoreCase("=")){
                operator="!=";
            }
            else if(operator.equalsIgnoreCase("!=")){
                operator="=";
            }
        }
        String query = " MATCH (node:" + node + ") " + setOperatorRelation(operator.toUpperCase(), values, "node." + key,neg);
        relation.append(query);
        appendResultQuery(resultQuery, relation);
    }

    public static void setRelationWithPropertyQuery(StringBuilder relation, String node, String edges, String key, String operator,boolean neg, StringBuilder resultQuery, List<String> values,StringBuilder optionalQuery) {
        if(neg){
            if(operator.equalsIgnoreCase("ANY") || operator.equalsIgnoreCase("IN")){
                operator="NOT IN";
                optionalQuery(optionalQuery,node,edges);
            }
            if(operator.equalsIgnoreCase("=")){
                operator="!=";
                optionalQuery(optionalQuery,node,edges);

            }
            else if(operator.equalsIgnoreCase("!=")){
                operator="=";
            }
        }
        appendMatchRelation(relation, node, edges);
        appendCondition(relation, setOperatorRelation(operator.toUpperCase(), values, "dst." + key,neg));
        appendResultQuery(resultQuery, relation);
    }

    private static void appendMatchRelation(StringBuilder relation, String node, String operator) {
        relation.append(" MATCH (node:ServiceOffering)-[").append(operator).append("]->(dst:").append(node).append(") ");
    }

    private static void appendCondition(StringBuilder relation, StringBuilder condition) {
        relation.append(condition);
    }

    private static void appendResultQuery(StringBuilder resultQuery, StringBuilder relation) {
        resultQuery.append(" ").append(relation);
    }
    private static void optionalQuery(StringBuilder optionQuery,String node, String operator ){
        optionQuery.append( " union  MATCH (node:ServiceOffering)\n" +
               "WHERE NOT (node)-[:"+operator+"]->(:"+node+")\n" +
               "with distinct node return distinct node  ");
    }


    public static StringBuilder setOperatorRelation(String opr, List<String> values, String key,boolean neg) {
        StringBuilder operator = new StringBuilder();
        switch (opr) {
            case "CONTAIN" -> {
                if(neg){
                    if (isNumberOrDouble( values)) {
                        operator.append(" WHERE NOT toString(").append(key).append(") CONTAINS ").append(String.join(",", values));
                    } else {
                        operator.append(" WHERE  NOT toLower(").append(key).append(") CONTAINS toLower(").append(escapeString(String.join(",", values))).append(")");
                    }
                }else {
                    if (isNumberOrDouble( values)) {
                        operator.append(" WHERE toString(").append(key).append(") CONTAINS ").append(String.join(",", values));
                    } else {
                        operator.append(" WHERE toLower(").append(key).append(") CONTAINS toLower(").append(escapeString(String.join(",", values))).append(")");
                    }
                }

            }
            case "IN" -> {
            if ( isNumberOrDouble( values)) {
                operator.append(" WHERE toString(").append(key).append(") IN [").append(String.join(",", values)).append("]");

            } else {
                operator.append(" WHERE toLower(").append(key).append(") IN [").append(escapeString(String.join(",", values)).toLowerCase()).append("]");

            }
        }
            case "=" -> {
                if (isNumberOrDouble( values)) {
                    operator.append(" WHERE (toString(").append(key).append(") = ").append(String.join(",", values)).append(")");
                } else {
                    operator.append(" WHERE (toLower(").append(key).append(") = toLower(").append(escapeString(String.join(",", values))).append("))");
                }
            }
            case "!=" -> {
                if (isNumberOrDouble(values)) {
                    operator.append(" WHERE (toString(").append(key).append(") <>").append(String.join(",", values)).append(")");
                } else {
                    operator.append(" WHERE (toLower(").append(key).append(") <> toLower(").append(escapeString(String.join(",", values))).append("))");

                }
            }
            case "ANY" ->
                    operator.append(" WHERE (ANY(tag IN ").append(key).append(" WHERE tag IN [").append(String.join(",", values)).append("]))");

            case "NOT IN" ->{
                if (isNumberOrDouble(values)) {
                    operator.append("  WITH DISTINCT node, COLLECT(DISTINCT TOLOWER(").append(key).append(")) AS names WHERE NONE(name IN names WHERE name IN [")
                            .append(String.join(",", values).toLowerCase()).append("]) OR size(names) = 0");

                }else {
                    operator.append("  WITH DISTINCT node, COLLECT(DISTINCT TOLOWER(").append(key).append(")) AS names WHERE NONE(name IN names WHERE name IN [")
                            .append(escapeString(String.join(",", values)).toLowerCase()).append("]) OR size(names) = 0");
                }
            }
            case ">" ->
                    operator.append(" WHERE (").append(key).append(") > ").append( Double.parseDouble(escapeString(String.join(",", values)))).append(")");
            case "<" ->
                    operator.append(" WHERE (").append(key).append(") < ").append( Double.parseDouble(escapeString(String.join(",", values)))).append(")");

        }
        return operator;
    }


    static String escapeString(String filterCriteriaValue) {
        String[] metaCharacters = {"\\", "^", "{", "}", "[", "]", ".", "?", "|", "<", ">", "&", "%", "'"};
            if (isNumberOrDouble(filterCriteriaValue)) {
            return filterCriteriaValue; // Return the original string without escaping
        }
        for (String metChar : metaCharacters) {
            if (filterCriteriaValue.contains(metChar)) {
                filterCriteriaValue = filterCriteriaValue.replace(metChar, "\\" + metChar);
            }
        }
        return filterCriteriaValue;
    }
    static boolean isNumberOrDouble(String values) {
            try {
                    Double.parseDouble(values.replaceAll("\"",""));
                // If parsing succeeds, it's a number
            } catch (NumberFormatException e) {
                // If parsing fails, it's not a number
                return false;
            }
        return true;
    }
    static boolean isNumberOrDouble(List<String> values) {

                for (String value : values) {
                    try {
                        Double.parseDouble(value.replaceAll("\"",""));
                        // If parsing succeeds, it's a number
                    } catch (NumberFormatException e) {
                        // If parsing fails, it's not a number
                        return false;
                    }
                }
                // All values in the list are numbers
                return true;
    }

    public Page<ServiceOffer> getSearchDetails(RecordFilter filter, Pageable pageable, Sort sort) {
        StringBuilder query = buildQuery(filter, sort);

        int skip = pageable.getPageNumber() * pageable.getPageSize();
        List<ServiceOffer> results;
        if (filter.getQuery() != null) {
            results = executeQuery(String.valueOf(query), skip, pageable.getPageSize(), pageable);
        }else {
           results = finaAll(String.valueOf(query), skip, pageable.getPageSize(), pageable);
        }
        long count = executeCountQuery(query.toString());

        return new PageImpl<>(results, pageable, count);
    }

    private StringBuilder buildQuery(RecordFilter filter, Sort sort) {
        String orderBy = buildOrderBy(sort);
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder optionQuery = new StringBuilder();


        if (filter.getQuery() != null) {
            List<Or> orClauses = filter.getQuery().getOr();
            List<And> andClauses = filter.getQuery().getAnd();

            if (orClauses != null) {
                for (Or orClause : orClauses) {
                    OrEvaluator.evaluate(orClause, queryBuilder,optionQuery);
                }
            } else if (andClauses != null) {
                for (And andClause : andClauses) {
                    AndEvaluator.evaluate(andClause, queryBuilder,optionQuery);
                }
            }
            if (filter.getQuery().getNode() != null && (filter.getQuery().getEdge() != null || filter.getQuery().getKey() != null)) {
                StringBuilder relation = new StringBuilder();

                List<String> values = filter.getQuery().getValue().stream()
                        .map(s -> "\"" + s + "\"")
                        .collect(Collectors.toList());
                if (filter.getQuery().getKey() == null) {
                    setRelationQuery(relation, filter.getQuery().getNode(), filter.getQuery().edge, queryBuilder, values,optionQuery);
                } else {
                    if (filter.getQuery().getEdge() == null) {
                        setOperatorQuery(relation, filter.getQuery().getNode(), filter.getQuery().operator, filter.getQuery().key,filter.getQuery().neg, queryBuilder, values,optionQuery);
                    } else {
                        setRelationWithPropertyQuery(relation, filter.getQuery().getNode(), filter.getQuery().edge, filter.getQuery().key, filter.getQuery().operator, filter.getQuery().neg,queryBuilder, values,optionQuery);
                    }
                }
            }
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append(" WITH DISTINCT node ");
                queryBuilder.append(orderBy);
                queryBuilder.append(" RETURN DISTINCT node");
            }

        } else {
            queryBuilder.append("MATCH (node:ServiceOffering)-[r]->(p) ");
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append(" WITH DISTINCT node ,COLLECT(DISTINCT r) AS rels,Collect(DISTINCT p) as p");
                queryBuilder.append(orderBy);
                queryBuilder.append(" RETURN DISTINCT node,COLLECT(DISTINCT rels) AS rels,Collect(DISTINCT p) as p");
            }
        }
        queryBuilder.append(optionQuery);
        return queryBuilder;
    }

    private String buildOrderBy(Sort sort) {
        StringBuilder orderBy = new StringBuilder(" ORDER BY ");
        int i = 0;

        for (Sort.Order order : sort) {
            orderBy.append(" node.").append(order.getProperty()).append(" ").append(order.getDirection());

            if (i < sort.toList().size() - 1) {
                orderBy.append(",");
            }
            i++;
        }
        return orderBy.toString();
    }

    private List<ServiceOffer> executeQuery(String query, int skip, int pageSize, Pageable pageable) {
        log.info("Dynamic query: " + query);
        return this.serviceOfferRepository.finByQuery(query, skip, pageSize, pageable);
    }
    private List<ServiceOffer> finaAll(String query, int skip, int pageSize, Pageable pageable) {
        log.info("Dynamic query: " + query);
        return this.serviceOfferRepository.finalAll(query, skip, pageSize, pageable);
    }
    private long executeCountQuery(String countQuery) {
        return this.serviceOfferRepository.count(countQuery);    }

}
