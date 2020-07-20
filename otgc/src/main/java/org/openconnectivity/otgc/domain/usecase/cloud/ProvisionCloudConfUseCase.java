package org.openconnectivity.otgc.domain.usecase.cloud;

import org.openconnectivity.otgc.data.repository.CloudRepository;

import javax.inject.Inject;

import io.reactivex.Completable;

public class ProvisionCloudConfUseCase {

    private final CloudRepository cloudRepository;

    @Inject
    public ProvisionCloudConfUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Completable execute(String authProvider, String cloudUrl, String accessToken, String cloudUuid) {
        return cloudRepository.provisionCloudConfiguration(authProvider, cloudUrl, accessToken, cloudUuid);
    }
}
