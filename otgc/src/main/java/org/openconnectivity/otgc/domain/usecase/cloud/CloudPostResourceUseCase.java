package org.openconnectivity.otgc.domain.usecase.cloud;

import io.reactivex.Completable;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.data.repository.CloudRepository;
import org.openconnectivity.otgc.domain.model.client.SerializableResource;
import org.openconnectivity.otgc.domain.model.devicelist.Device;

import javax.inject.Inject;
import java.util.Map;

public class CloudPostResourceUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudPostResourceUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Completable execute(Device device, SerializableResource resource, OCRepresentation rep, Object valueArray) {
        return cloudRepository.retrieveEndpoint()
                .flatMapCompletable(endpoint -> cloudRepository.post(endpoint, resource.getUri(), device.getDeviceId(), rep, valueArray));
    }

    public Completable execute(Device device, SerializableResource resource, Map<String, Object> values) {
        return cloudRepository.retrieveEndpoint()
                .flatMapCompletable(endpoint -> cloudRepository.post(endpoint, resource.getUri(), device.getDeviceId(), values));
    }
}
