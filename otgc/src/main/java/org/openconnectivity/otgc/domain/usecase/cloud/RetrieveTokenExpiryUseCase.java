package org.openconnectivity.otgc.domain.usecase.cloud;

import org.openconnectivity.otgc.data.repository.CloudRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class RetrieveTokenExpiryUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public RetrieveTokenExpiryUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Completable execute() {
        return cloudRepository.retrieveTokenExpiry();
    }
}
