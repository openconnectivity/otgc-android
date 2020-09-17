package org.openconnectivity.otgc.domain.usecase.cloud;

import io.reactivex.Single;
import org.openconnectivity.otgc.data.repository.CloudRepository;
import org.openconnectivity.otgc.domain.model.client.SerializableResource;
import org.openconnectivity.otgc.domain.model.devicelist.Device;

import javax.inject.Inject;

public class CloudGetResourceUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudGetResourceUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<SerializableResource> execute(Device device, SerializableResource resource) {
        return cloudRepository.retrieveEndpoint()
                .flatMap(endpoint -> cloudRepository.get(endpoint, resource.getUri(), device.getDeviceId()))
                .map(ocRepresentation -> {
                    resource.setProperties(ocRepresentation);
                    return resource;
                });
    }
}
