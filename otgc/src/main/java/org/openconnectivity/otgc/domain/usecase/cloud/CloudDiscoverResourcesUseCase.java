package org.openconnectivity.otgc.domain.usecase.cloud;

import org.openconnectivity.otgc.data.repository.CloudRepository;
import org.openconnectivity.otgc.domain.model.client.SerializableResource;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class CloudDiscoverResourcesUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudDiscoverResourcesUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<List<SerializableResource>> execute(Device device) {
        return
                Completable.complete()
                        .delay(1, TimeUnit.SECONDS)
                        .andThen(cloudRepository.discoverVerticalResources(device.getDeviceId())
                                .map(ocResources -> {
                                    List<SerializableResource> serializableResources = new ArrayList<>();
                                    for (OcResource resource : ocResources) {
                                        SerializableResource serializableResource = new SerializableResource();
                                        serializableResource.setUri(resource.getHref());
                                        serializableResource.setResourceTypes(resource.getResourceTypes());
                                        serializableResource.setResourceInterfaces(resource.getInterfaces());
                                        serializableResource.setObservable(false);

                                        serializableResources.add(serializableResource);
                                    }

                                    Collections.sort(serializableResources,
                                            (r1, r2) -> r1.getUri().compareTo(r2.getUri()));

                                    return serializableResources;
                                }));
    }
}
