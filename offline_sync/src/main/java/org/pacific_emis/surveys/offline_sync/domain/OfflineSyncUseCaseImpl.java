package org.pacific_emis.surveys.offline_sync.domain;

import org.pacific_emis.surveys.core.data.model.Survey;
import org.pacific_emis.surveys.core.utils.LifecycleListener;
import org.pacific_emis.surveys.offline_sync.data.accessor.OfflineAccessor;
import io.reactivex.Completable;

public class OfflineSyncUseCaseImpl implements OfflineSyncUseCase, OfflineAccessor.SyncUseCase {

    private final LifecycleListener lifecycleListener;
    private final OfflineAccessor offlineAccessor;

    private Survey targetSurvey;
    private Survey externalSurvey;

    public OfflineSyncUseCaseImpl(LifecycleListener lifecycleListener, OfflineAccessor offlineAccessor) {
        this.lifecycleListener = lifecycleListener;
        this.offlineAccessor = offlineAccessor;
        this.offlineAccessor.setSyncUseCase(this);
    }

    @Override
    public void executeAsInitiator(Survey survey) {
        targetSurvey = survey;
    }

    @Override
    public Completable executeAsReceiver() {
        return offlineAccessor.becomeAvailableToConnect();
    }

    @Override
    public Survey getTargetSurvey() {
        return targetSurvey;
    }

    @Override
    public void setTargetSurvey(Survey targetSurvey) {
        this.targetSurvey = targetSurvey;
    }

    @Override
    public void finish() {
        offlineAccessor.disconnect();
        offlineAccessor.stopDiscoverDevices();
        offlineAccessor.becomeUnavailableToConnect();
    }

    @Override
    public void setExternalSurvey(Survey survey) {
        this.externalSurvey = survey;
    }

    @Override
    public Survey getExternalSurvey() {
        return externalSurvey;
    }
}
