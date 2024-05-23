package eu.gaiax.federatedcatalogue.rest;

import com.smartsensesolutions.java.commons.FilterRequest;
import eu.gaiax.federatedcatalogue.model.request.Pageable;
import eu.gaiax.federatedcatalogue.model.request.RecordFilter;
import eu.gaiax.federatedcatalogue.model.response.CataloguePage;
import eu.gaiax.federatedcatalogue.model.response.CommonResponse;
import eu.gaiax.federatedcatalogue.model.response.ServiceDetailResponse;
import eu.gaiax.federatedcatalogue.model.response.ServiceListResponse;
import eu.gaiax.federatedcatalogue.service.neo4j.search.CatalogueSearchService;
import eu.gaiax.federatedcatalogue.service.neo4j.serviceoffering.ServiceOfferService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static eu.gaiax.federatedcatalogue.utils.constant.ApplicationRestConstant.GAIA_X_BASE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = GAIA_X_BASE_PATH + "/catalogue")
@RequiredArgsConstructor
public class CatalogueSearchResource {

    private static final Logger log = LoggerFactory.getLogger(CatalogueSearchResource.class);
    private final CatalogueSearchService catalogueSearchService;

    private final ServiceOfferService serviceOfferService;

    @GetMapping
    public CataloguePage<ServiceListResponse> getQuery(Pageable pageable) {
        return serviceOfferService.getDefault(pageable);
    }

    public record Tag(String value, long count){}

    @GetMapping(value = "/tags", produces = APPLICATION_JSON_VALUE)
    public List<Tag> getTags() {
        return List.of(
                new Tag("JavaScript", 38),
                new Tag("React", 30),
                new Tag("Nodejs", 28),
                new Tag("Express", 25),
                new Tag("HTML5", 33),
                new Tag("MongoDB", 18),
                new Tag("'CSS3'", 20)
        );
    }


    @GetMapping(value = "/selector", produces = APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getAllNodes() {
        return catalogueSearchService.getNodeLabelsAndRelationships();
    }

    @GetMapping(value = "/grammar", produces = "application/javascript")
    public String getGrammar() {
        return catalogueSearchService.getGrammarContent();
    }

    @PostMapping(value = "/selector/option/{option}/property/{property}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public CataloguePage<String> getValues(@PathVariable("option") String option, @PathVariable("property") String property, @RequestBody FilterRequest filterRequest) {
        return catalogueSearchService.getOptions(option, property, filterRequest);
    }

    @PostMapping(value = "/search", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public CataloguePage<ServiceListResponse> executeQuery(@RequestBody RecordFilter filter) {
        return serviceOfferService.getSearchData(filter);
    }

    @GetMapping(value = "/service-details", produces = APPLICATION_JSON_VALUE)
    public CommonResponse<ServiceDetailResponse> getServiceDetailsById(@RequestParam(value = "id") UUID id) {
        return CommonResponse.of(serviceOfferService.getServiceDetailsById(id));
    }
}


