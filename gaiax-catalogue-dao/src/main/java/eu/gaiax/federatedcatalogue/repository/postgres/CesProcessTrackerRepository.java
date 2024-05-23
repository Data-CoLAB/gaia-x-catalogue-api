package eu.gaiax.federatedcatalogue.repository.postgres;

import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import eu.gaiax.federatedcatalogue.entity.postgres.CesProcessTracker;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CesProcessTrackerRepository extends BaseRepository<CesProcessTracker, UUID> {
    CesProcessTracker findByCesId(String cesId);

    CesProcessTracker findTopByOrderByCreatedAtDesc();
}
