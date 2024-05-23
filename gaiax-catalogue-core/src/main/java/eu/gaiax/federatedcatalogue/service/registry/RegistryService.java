package eu.gaiax.federatedcatalogue.service.registry;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistryService {
    private final RegistryClient registryClient;
    private TrustFrameworkShapes trustFrameworkShapes;

    @Value("${gaiax.x5u.check:true}")
    private boolean x5uCheckEnabled;

    public boolean isValidCertificateChain(String certChain) {
        if (!x5uCheckEnabled) {
            return true;
        }
        var sanitized = certChain.replaceAll("\n", "");
        return registryClient.checkChainRequest(new RegistryClient.CheckChainRequest(sanitized)).result();
    }

    public TrustFrameworkShapes getTrustFrameworkShapes() {
        if (trustFrameworkShapes == null) {
            var shapes = registryClient.getTrustFrameworkShapes();
            trustFrameworkShapes = new TrustFrameworkShapes(shapes);
        }
        return trustFrameworkShapes;
    }

}
