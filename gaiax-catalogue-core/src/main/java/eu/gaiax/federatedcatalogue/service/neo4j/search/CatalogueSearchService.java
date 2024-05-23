package eu.gaiax.federatedcatalogue.service.neo4j.search;

import com.smartsensesolutions.java.commons.FilterRequest;
import eu.gaiax.federatedcatalogue.entity.neo4j.RuleRequest;
import eu.gaiax.federatedcatalogue.model.response.CataloguePage;
import eu.gaiax.federatedcatalogue.repository.neo4j.CatalogueSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CatalogueSearchService {
    private final CatalogueSearchRepository catalogueRepository;

    private final FreemarkerService freemarkerService;

    public CatalogueSearchService(CatalogueSearchRepository catalogueRepository, FreemarkerService freemarkerService) {
        this.catalogueRepository = catalogueRepository;
        this.freemarkerService = freemarkerService;
    }

    public Set<String> getAllNodes() {
        List<Value> values = catalogueRepository.findDistinctNodeLabels();
        Set<String> labels = new HashSet<>();

        for (Value value : values) {
            List<Object> listValue = value.asList();
            for (Object obj : listValue) {
                labels.add(obj.toString());
            }
        }
        return labels;
    }

    public List<Map<String, Object>> getNodeLabelsAndRelationships() {
        List<Map<String, Object>> selectors = new ArrayList<>();
        // Get distinct node labels
        Set<String> labels = getAllNodes();
        labels.remove("TermsAndConditions");
        labels.remove("RegistrationNumber");

        for (String label : labels) {
            Map<String, Object> selector = new HashMap<>();
            List<String> propertyLabel = catalogueRepository.findPropertyLabel(label);
            for(String property:propertyLabel){
                selector = new HashMap<>();
                selector.put("field", label);
                Set<String> relationshipTypes = catalogueRepository.findDistinctRelationshipTypesByLabel(label);
                if(!relationshipTypes.isEmpty()){
                        selector.put("edge", relationshipTypes.stream().findFirst().get());
                }
                selector.put("property", property);
                selectors.add(selector);
            }
        }
        return selectors;
    }

    public String getGrammarContent() {
        try {
            List<RuleRequest> rules = catalogueRepository.findAllRules();
            List<RuleRequest> outRules = catalogueRepository.findOutRules();
            rules.addAll(outRules);

            Set<String> dests = rules.stream().map(RuleRequest::getDst).collect(Collectors.toSet());
            dests.removeIf(Objects::isNull);
            Set<String> srcs = rules.stream().map(RuleRequest::getSrc).collect(Collectors.toSet());
            Map<String, Set<String>> destMap = new HashMap<>();
            Map<String, Set<String>> srcMap = new HashMap<>();

            for (String dest : dests) {
                Set<String> value = catalogueRepository.findDestination(dest);
                if (!value.isEmpty()) {
                    destMap.put(dest, value);
                }
            }
            for (String src : srcs) {
                Set<String> value = catalogueRepository.findSource(src);
                if (!value.isEmpty()) {
                    srcMap.put(src, value);
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("rules", rules);
            data.put("destMap", destMap);
            data.put("srcMap", srcMap);
            return freemarkerService.parseTemplate("peg.js", data);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public CataloguePage<String> getOptions(String option, String property, FilterRequest filterRequest) {
        List<String> options;
        int skip = filterRequest.getPage() * filterRequest.getSize();
        long count;
        PageRequest pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());
        String where = "";
        if (property.equalsIgnoreCase("formatType")) {
            if (filterRequest.getCriteria() != null) {
                where = " where toLower(individualValue) Contains toLower('" + String.join(",", filterRequest.getCriteria().get(0).getValues()) + "')";
            }
            options = catalogueRepository.findFormatTypeValues(where, skip, pageable.getPageSize());
            count = catalogueRepository.countFormatTypeValues(where);
        } else {
            if (filterRequest.getCriteria() != null) {
                where = " and toLower(n." + property + ") Contains toLower('" + String.join(",", filterRequest.getCriteria().get(0).getValues()) + "')";
            }
            count = catalogueRepository.count(option, where, property);
            options = catalogueRepository.findAllPropertyValues(option, property, where, skip, pageable.getPageSize());
        }
        Page<String> data = new PageImpl<>(options, pageable, count);
        return CataloguePage.of(data);
    }
}
