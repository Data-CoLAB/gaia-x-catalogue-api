package eu.gaiax.federatedcatalogue.credential;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class CredentialSubject extends Credential {
    private VerifiableCredential verifiableCredential;

    public CredentialSubject(VerifiableCredential verifiableCredential, JSONObject json) {
        super(json);
        this.verifiableCredential = verifiableCredential;
    }

    public CredentialSubject() {
        super();
    }
}
