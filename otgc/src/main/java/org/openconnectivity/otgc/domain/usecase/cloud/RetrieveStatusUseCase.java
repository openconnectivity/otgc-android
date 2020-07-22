package org.openconnectivity.otgc.domain.usecase.cloud;

import org.openconnectivity.otgc.data.repository.CloudRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class RetrieveStatusUseCase {

    private final CloudRepository cloudRepository;

    @Inject
    public RetrieveStatusUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<Integer> execute() {
        return cloudRepository.retrieveState();
    }
}
