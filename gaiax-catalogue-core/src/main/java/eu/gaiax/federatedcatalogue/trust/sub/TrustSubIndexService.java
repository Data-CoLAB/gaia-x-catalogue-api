package eu.gaiax.federatedcatalogue.trust.sub;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;
import eu.gaiax.federatedcatalogue.trust.TrustIndex;

public interface TrustSubIndexService {
    TrustIndex.SubIndex getSubIndex();
    Double compute(CredentialSubject credentialSubject, CredentialsSet credentials);
}
