package eu.gaiax.federatedcatalogue.service.neo4j.search;

import eu.gaiax.federatedcatalogue.model.request.And;
import eu.gaiax.federatedcatalogue.model.request.Or;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static eu.gaiax.federatedcatalogue.service.neo4j.search.OperationService.*;

public class OrEvaluator {
    public static void evaluate(Or orClause, StringBuilder resultQuery,StringBuilder optionQuery) {
        if (orClause == null) {
            return;
        }
        if (StringUtils.hasText(resultQuery) && orClause.and != null) {
            resultQuery.append(" RETURN DISTINCT node ");
            resultQuery.append(" union ");
        }
        evaluateAndClauses(orClause.and, resultQuery,optionQuery);
        evaluateCondition(orClause, resultQuery,optionQuery);
        evaluateOrClauses(orClause.or, resultQuery,optionQuery);
    }

    private static void evaluateAndClauses(List<And> andClauses, StringBuilder resultQuery,StringBuilder optionQuery) {
        if (andClauses == null) {
            return;
        }
        for (And innerAnd : andClauses) {
            AndEvaluator.evaluate(innerAnd, resultQuery,optionQuery);
        }
    }

    private static void evaluateOrClauses(List<Or> orClauses, StringBuilder resultQuery,StringBuilder  optionQuery) {
        if (orClauses == null) {
            return;
        }
        for (Or innerOr : orClauses) {
            evaluate(innerOr, resultQuery,optionQuery);
        }
    }

    private static void evaluateCondition(Or orClause, StringBuilder resultQuery,StringBuilder optionalQuery) {
        if (orClause.node != null && (orClause.edge != null || orClause.operator != null)) {
            if (StringUtils.hasText(resultQuery)) {
                resultQuery.append(" RETURN DISTINCT node");
                resultQuery.append(" union  ");
            }
            StringBuilder relation = new StringBuilder();
            StringBuilder attribute = new StringBuilder();
            List<String> values = orClause.getValue().stream()
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.toList());

            if (orClause.getKey() == null) {
                setRelationQuery(relation, orClause.node, orClause.edge, resultQuery, values,optionalQuery);
            } else if (orClause.getEdge() == null) {
                setOperatorQuery(attribute, orClause.node, orClause.operator, orClause.key,orClause.neg, resultQuery, values,optionalQuery);
            } else {
                setRelationWithPropertyQuery(attribute, orClause.node, orClause.edge, orClause.key, orClause.operator,orClause.neg, resultQuery, values,optionalQuery);
            }
            resultQuery.append(" WITH DISTINCT node");
        }
    }
}
