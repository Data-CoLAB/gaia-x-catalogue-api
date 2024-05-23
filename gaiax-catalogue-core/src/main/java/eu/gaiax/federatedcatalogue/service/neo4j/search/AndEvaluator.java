package eu.gaiax.federatedcatalogue.service.neo4j.search;

import eu.gaiax.federatedcatalogue.model.request.And;
import eu.gaiax.federatedcatalogue.model.request.Or;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static eu.gaiax.federatedcatalogue.service.neo4j.search.OperationService.*;

@Service
public class AndEvaluator {
    public static void evaluate(And andClause, StringBuilder resultQuery,StringBuilder optionQuery) {
        if (andClause == null) {
            return;
        }

        evaluateOrClauses(andClause.or, resultQuery,optionQuery);
        evaluateAndClauses(andClause.and, resultQuery,optionQuery);
        evaluateCondition(andClause, resultQuery,optionQuery);
    }

    private static void evaluateOrClauses(List<Or> orClauses, StringBuilder resultQuery,StringBuilder optionQuery) {
        if (orClauses == null) {
            return;
        }

        for (Or orClause : orClauses) {
            OrEvaluator.evaluate(orClause, resultQuery,optionQuery);
        }
    }

    private static void evaluateAndClauses(List<And> andClauses, StringBuilder resultQuery,StringBuilder optionQuery) {
        if (andClauses == null) {
            return;
        }

        for (And innerAnd : andClauses) {
            evaluate(innerAnd, resultQuery,optionQuery);
        }
    }

    private static void evaluateCondition(And andClause, StringBuilder resultQuery,StringBuilder optionalQuery) {
        if (andClause.node != null && (andClause.edge != null || andClause.operator != null)) {
            StringBuilder relation = new StringBuilder();
            StringBuilder attribute = new StringBuilder();
            List<String> values = andClause.getValue().stream()
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.toList());
            if (andClause.getKey() == null && andClause.getOperator() == null) {
                setRelationQuery(relation, andClause.getNode(), andClause.edge, resultQuery, values,optionalQuery);
            } else {
                if (andClause.getEdge() == null) {
                    setOperatorQuery(attribute, andClause.getNode(), andClause.operator, andClause.key, andClause.neg,resultQuery, values,optionalQuery);
                } else {
                    setRelationWithPropertyQuery(attribute, andClause.getNode(), andClause.edge, andClause.key, andClause.operator,andClause.neg, resultQuery, values,optionalQuery);
                }
            }
            resultQuery.append(" WITH DISTINCT node");
        }
    }
}




