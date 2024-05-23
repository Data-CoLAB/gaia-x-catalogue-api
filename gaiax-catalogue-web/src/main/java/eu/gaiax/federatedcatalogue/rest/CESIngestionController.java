package eu.gaiax.federatedcatalogue.rest;

import eu.gaiax.federatedcatalogue.ces.CESLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static eu.gaiax.federatedcatalogue.utils.constant.ApplicationRestConstant.GAIA_X_BASE_PATH;
import static eu.gaiax.federatedcatalogue.utils.constant.ApplicationRestConstant.INGEST_BY_CES_ID;

@RestController
@RequestMapping(value = GAIA_X_BASE_PATH)
@RequiredArgsConstructor
public class CESIngestionController {

    private final CESLookupService cesLookupService;

    @PostMapping(value = INGEST_BY_CES_ID)
    public String ingestByCesId(@PathVariable("cesId") String cesId) {
        this.cesLookupService.ingestByCesId(cesId);
        return "Success";
    }
}
