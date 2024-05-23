package eu.gaiax.federatedcatalogue.service.registry;

import org.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "GaiaxRegistryClient", url = "${gaiax.host.registry:https://registry.lab.gaia-x.eu/development}")
public interface RegistryClient {

    /**
     * Check if any of the given certificates in a certificate chain is
     * resolvable against an endorsed trust anchor.
     * @param certs the certificate chain without spaces nor new line characters
     * @return CheckChainResponse
     */
    @PostMapping(value = "/api/trustAnchor/chain")
    CheckChainResponse checkChainRequest(@RequestBody CheckChainRequest certs);

    @GetMapping(value = "/api/trusted-shape-registry/v1/shapes/trustframework")
    String getTrustFrameworkShapes();

    record CheckChainRequest (String certs) {}
    record CheckChainResponse (boolean result) {}
}
