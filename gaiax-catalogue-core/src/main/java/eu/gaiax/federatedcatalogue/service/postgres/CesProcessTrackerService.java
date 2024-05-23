package eu.gaiax.federatedcatalogue.service.postgres;

import eu.gaiax.federatedcatalogue.entity.postgres.CesProcessTracker;
import eu.gaiax.federatedcatalogue.repository.postgres.CesProcessTrackerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CesProcessTrackerService {

    private final CesProcessTrackerRepository processTrackerRepository;

    public void update(String cesId, ECesStatus status, String reason, String credential) {
        CesProcessTracker cesProcessTracker = this.processTrackerRepository.findByCesId(cesId);
        if (Objects.nonNull(cesProcessTracker)) {
            cesProcessTracker.setReason(reason);
            cesProcessTracker.setStatus(status.getId());
            cesProcessTracker.setUpdateAt(new Date());
            this.processTrackerRepository.save(cesProcessTracker);
        } else {
            this.processTrackerRepository.save(
                    CesProcessTracker
                            .builder()
                            .cesId(cesId)
                            .reason(reason)
                            .status(status.getId())
                            .credential(credential)
                            .build()
            );
        }
    }

    public String getLastIngestProcessId() {
        CesProcessTracker cesProcessTracker = this.processTrackerRepository.findTopByOrderByCreatedAtDesc();
        if (Objects.isNull(cesProcessTracker)) {
            return null;
        }
        return cesProcessTracker.getCesId();
    }

    public CesProcessTracker getByCesId(String cesId) {
        return this.processTrackerRepository.findByCesId(cesId);
    }

}
