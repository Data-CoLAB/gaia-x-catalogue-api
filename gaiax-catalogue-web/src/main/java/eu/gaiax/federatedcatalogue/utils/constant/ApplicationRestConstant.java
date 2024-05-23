package eu.gaiax.federatedcatalogue.utils.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationRestConstant {

    public static final String GAIA_X_BASE_PATH = "/gaiax";
    public static final String VALIDATE_POLICY = "/validate/policy";
    public static final String INGEST_BY_CES_ID = "/ingest/{cesId}";
}
