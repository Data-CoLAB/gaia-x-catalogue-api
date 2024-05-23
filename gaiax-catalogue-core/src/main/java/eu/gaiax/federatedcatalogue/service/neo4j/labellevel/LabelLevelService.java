package eu.gaiax.federatedcatalogue.service.neo4j.labellevel;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import org.springframework.stereotype.Service;

import static eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant.GX_LABEL_LEVEL;

@Service
public class LabelLevelService {

    public String getLabelLevel(CredentialSubject credentialSubject) {
        // Not implemented yet
        return credentialSubject.getString(GX_LABEL_LEVEL);
    }
}
