package eu.gaiax.federatedcatalogue.credential;

import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant.CREDENTIAL_SUBJECT;

@Getter
public class VerifiableCredential extends Credential {

    private final List<CredentialSubject> subjects;

    public VerifiableCredential(JSONObject json) {
        super(json);
        this.subjects = new ArrayList<>();
        super.getArray(CREDENTIAL_SUBJECT).forEach(s -> subjects.add(new CredentialSubject(this, (JSONObject) s)));
    }

}
