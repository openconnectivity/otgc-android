package org.openconnectivity.otgc.domain.usecase;

import org.openconnectivity.otgc.data.repository.PreferencesRepository;
import org.openconnectivity.otgc.data.repository.ProvisioningRepository;
import org.openconnectivity.otgc.utils.constant.OtgcMode;

import javax.inject.Inject;

import io.reactivex.Completable;

public class ResetClientModeUseCase {

    private final ProvisioningRepository provisioningRepository;
    private final PreferencesRepository preferencesRepository;

    @Inject
    public ResetClientModeUseCase(ProvisioningRepository provisioningRepository,
                                  PreferencesRepository preferencesRepository) {
        this.provisioningRepository = provisioningRepository;
        this.preferencesRepository = preferencesRepository;
    }

    public Completable execute() {
        return provisioningRepository.resetSvrDb()
                .andThen(Completable.fromAction(() -> preferencesRepository.setMode(OtgcMode.CLIENT)));
    }
}
