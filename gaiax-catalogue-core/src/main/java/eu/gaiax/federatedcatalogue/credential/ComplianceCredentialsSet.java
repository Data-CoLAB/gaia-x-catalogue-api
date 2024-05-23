package eu.gaiax.federatedcatalogue.credential;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant.CREDENTIAL_SUBJECT;
import static eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant.getAsArray;

@Slf4j
public class ComplianceCredentialsSet {

    private final CredentialResolver credentialResolver;

    @Getter
    private final VerifiableCredential complianceVc;

    @Getter
    private CredentialsSet resolvedCredentials;

    public ComplianceCredentialsSet(CredentialResolver credentialResolver, VerifiableCredential complianceVc) {
        this.credentialResolver = credentialResolver;
        this.complianceVc = complianceVc;
    }

    public void resolve() {
        List<VerifiableCredential> resolved = new ArrayList<>();
        for (CredentialSubject complianceVc : complianceVc.getSubjects()) {
            try {
                var vc = credentialResolver.fetch(complianceVc.getId());
                if (vc.has(CREDENTIAL_SUBJECT)) {
                    resolved.add(new VerifiableCredential(vc));
                } else if (vc.has("selfDescriptionCredential")) {
                    log.warn("VC {} is using the wrong spec... extracting VCs from selfDescriptionCredential VP", complianceVc.getId());
                    var knownVcs = getAsArray(vc.getJSONObject("selfDescriptionCredential"), "verifiableCredential");
                    knownVcs.forEach(kvc -> resolved.add(new VerifiableCredential((JSONObject) kvc)));
                } else {
                    log.warn("VC {} has no credential subject", complianceVc.getId());
                }
            } catch (Exception e) {
                log.warn("Failed to resolve VC {} : {}", complianceVc.getId(), e.getMessage());
            }
        }
        this.resolvedCredentials = new CredentialsSet(resolved);
    }

    public ComplianceCredentialsSet resolved() {
        if (this.resolvedCredentials == null) {
            this.resolve();
        }
        return this;
    }

}
