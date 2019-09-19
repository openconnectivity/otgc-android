package org.openconnectivity.otgc.domain.usecase;

import org.openconnectivity.otgc.data.repository.PreferencesRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetModeUseCase {

    private final PreferencesRepository preferencesRepository;

    @Inject
    public GetModeUseCase(PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    public Single<String> execute() {
        return Single.just(preferencesRepository.getMode());
    }
}
