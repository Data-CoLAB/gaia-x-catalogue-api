package eu.gaiax.federatedcatalogue.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SelfDescriptionConstant {

    public static final String VERIFIABLE_CREDENTIAL = "verifiableCredential";
    public static final String CREDENTIAL_SUBJECT = "credentialSubject";
    public static final String GX_PROVIDED_BY = "gx:providedBy";
    public static final String GX_TERMS_AND_CONDITIONS = "gx:termsAndConditions";
    public static final String GX_URL = "gx:URL";
    public static final String GX_HASH = "gx:hash";
    public static final String GX_POLICY = "gx:policy";
    public static final String GX_DATA_ACCOUNT_EXPORT = "gx:dataAccountExport";
    public static final String GX_REQUEST_TYPE = "gx:requestType";
    public static final String GX_ACCESS_TYPE = "gx:accessType";
    public static final String GX_FORMAT_TYPE = "gx:formatType";
    public static final String GX_AGGREGATION_OF = "gx:aggregationOf";
    public static final String GX_EXPOSED_THROUGH = "gx:exposedThrough";
    public static final String GX_PRODUCED_BY = "gx:producedBy";
    public static final String GX_DEPENDS_ON = "gx:dependsOn";
    public static final String GX_DATA_PROTECTION_REGIME = "gx:dataProtectionRegime";
    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String GX_LEGAL_NAME = "gx:legalName";
    public static final String GX_NAME = "gx:name";
    public static final String GX_DESCRIPTION = "gx:description";
    public static final String GX_CONTAINS_PII = "gx:containsPII";
    public static final String GX_HEADQUARTER_ADDRESS = "gx:headquarterAddress";
    public static final String GX_COUNTRY_SUBDIVISION_CODE = "gx:countrySubdivisionCode";
    public static final String GX_COUNTRY_CODE = "gx:countryCode";
    public static final String GX_LABEL_LEVEL = "gx:labelLevel";
    public static final String GX_VAT_ID = "gx:vatID";
    public static final String GX_EORI = "gx:EORI";
    public static final String GX_LEI_CODE = "gx:leiCode";
    public static final String GX_TAX_ID = "gx:taxID";
    public static final String GX_EUID = "gx:EUID";
    public static final String GX_LEGAL_ADDRESS = "gx:legalAddress";
    public static final String GX_LEGAL_PARTICIPANT = "gx:LegalParticipant";
    public static final String GX_SERVICE_OFFERING = "gx:ServiceOffering";
    public static final String GX_PHYSICAL_RESOURCE = "gx:PhysicalResource";
    public static final String GX_VIRTUAL_SOFTWARE_RESOURCE = "gx:VirtualSoftwareResource";
    public static final String GX_VIRTUAL_DATA_RESOURCE = "gx:VirtualDataResource";
    public static final String GX_MAINTAINED_BY = "gx:maintainedBy";
    public static final String GX_OWNED_BY = "gx:ownedBy";
    public static final String GX_MANUFACTURED_BY = "gx:manufacturedBy";
    public static final String GX_COPYRIGHT_OWNED_BY = "gx:copyrightOwnedBy";
    public static final String GX_LOCATION_ADDRESS = "gx:locationAddress";
    public static final String GX_LOCATION = "gx:location";
    public static final String GX_REGISTRATION_NUMBER = "gx:legalRegistrationNumber";
    public static final String GX_LICENSE = "gx:license";

    public static JSONArray findVerifiableCredentials(JSONObject lookup) {
        if (lookup.has(VERIFIABLE_CREDENTIAL)) {
            return lookup.getJSONArray(VERIFIABLE_CREDENTIAL);
        }
        if (lookup.has(CREDENTIAL_SUBJECT)) {
            var array = new JSONArray();
            array.put(lookup);
            return array;
        }
        return new JSONArray();
    }

    public static JSONArray getAsArray(JSONObject object, String key) {
        if (object == null || !object.has(key)) {
            return new JSONArray();
        }
        Object value = object.get(key);
        if (!(value instanceof JSONArray)) {
            var array = new JSONArray();
            array.put(value);
            return array;
        }
        return object.getJSONArray(key);
    }

    public static String getAliasable(JSONObject object, String field) {
        if (object == null) {
            return null;
        }
        Object o = null;
        if (object.has(field)) {
            o = object.get(field);
        } else if (object.has("@" + field)) {
            o = object.get("@" + field);
        }
        if (o instanceof JSONArray a) {
            o = a.isEmpty() ? null : a.get(0);
        }
        return o == null ? null : o.toString();
    }

    public static String getId(JSONObject object) {
        return getAliasable(object, ID);
    }

    public static String getType(JSONObject object) {
        return getAliasable(object, TYPE);
    }
}
