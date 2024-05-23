package eu.gaiax.federatedcatalogue.ces;

import eu.gaiax.federatedcatalogue.client.CESClient;
import eu.gaiax.federatedcatalogue.credential.VerifiableCredential;
import eu.gaiax.federatedcatalogue.entity.postgres.CesProcessTracker;
import eu.gaiax.federatedcatalogue.service.CatalogueIngestionService;
import eu.gaiax.federatedcatalogue.service.postgres.CesProcessTrackerService;
import eu.gaiax.federatedcatalogue.service.postgres.ECesStatus;
import eu.gaiax.federatedcatalogue.utils.CESConstant;
import eu.gaiax.federatedcatalogue.utils.Validate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CESLookupService {

    private final CESClient cesClient;
    private final CesProcessTrackerService processTrackerService;
    private final CatalogueIngestionService catalogueIngestionService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("CES lookup ready, last ingested was {}", this.processTrackerService.getLastIngestProcessId());
    }

    @Scheduled(fixedDelayString = "${gaiax.ces.sync.interval:5000}", initialDelayString = "${gaiax.ces.sync.delay:5000}")
    public void cesLookup() {
        var lastReceivedId = this.processTrackerService.getLastIngestProcessId();
        try {
            var ingested = cesLookup(lastReceivedId);
            if (!ingested.isEmpty()) {
                log.info("Ingestion process has been completed: processed {}.", ingested.size());
            }
        } catch (Exception ex) {
            log.error("Consume credential-event failed with lastReceivedId {}", lastReceivedId, ex);
        }
    }

    private Collection<String> cesLookup(String lastReceivedId) {
        List<Object> cesCredentials = this.cesClient.fetchCredentials(lastReceivedId, 0L, 10L);
        if (CollectionUtils.isEmpty(cesCredentials)) {
            return Collections.emptyList();
        }
        List<String> ingested = new ArrayList<>();
        JSONArray credentials = new JSONArray(cesCredentials);
        for (var c : credentials) {
            var obj = (JSONObject) c;
            ingested.add(obj.getString(CESConstant.ID));
            this.ingestCredentialEvent(obj);
        }
        ingested.addAll(cesLookup(ingested.get(ingested.size() - 1)));
        return ingested;
    }

    private void ingestCredentialEvent(JSONObject credentialEvent) {
        String receivedId = credentialEvent.getString(CESConstant.ID);
        log.info("Ingestion process starts for {}", receivedId);
        this.processTrackerService.update(receivedId, ECesStatus.IN_PROGRESS, null, credentialEvent.toString());
        try {
            var complianceVc = new VerifiableCredential(credentialEvent.getJSONObject(CESConstant.DATA));
            this.catalogueIngestionService.ingestComplianceCredentials(complianceVc);
            this.processTrackerService.update(receivedId, ECesStatus.DONE, null, credentialEvent.toString());
        } catch (Exception ex) {
            log.error("Ingestion failed for {}", receivedId, ex);
            this.processTrackerService.update(receivedId, ECesStatus.FAILED, ex.getMessage(), credentialEvent.toString());
        }
        log.info("Ingestion process completed for {}", receivedId);
    }

    public void ingestByCesId(String cesId) {
        CesProcessTracker processTracker = this.processTrackerService.getByCesId(cesId);
        Validate.isNull(processTracker).launch("invalid.ces.id");
        try {
            JSONObject credential = new JSONObject(processTracker.getCredential());
            this.ingestCredentialEvent(credential);
        } catch (Exception ex) {
            log.error("Not able to ingest the data with given cesId {}", cesId);
            throw ex;
        }
    }
}
