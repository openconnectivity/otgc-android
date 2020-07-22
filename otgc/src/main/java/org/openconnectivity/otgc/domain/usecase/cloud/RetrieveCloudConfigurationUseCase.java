package org.openconnectivity.otgc.domain.usecase.cloud;

import org.openconnectivity.otgc.data.repository.CloudRepository;
import org.openconnectivity.otgc.domain.model.resource.cloud.OcCloudConfiguration;

import javax.inject.Inject;

import io.reactivex.Single;

public class RetrieveCloudConfigurationUseCase {

    private final CloudRepository cloudRepository;

    @Inject
    public RetrieveCloudConfigurationUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<OcCloudConfiguration> execute() {
        return cloudRepository.retrieveCloudConfiguration();
    }
}
