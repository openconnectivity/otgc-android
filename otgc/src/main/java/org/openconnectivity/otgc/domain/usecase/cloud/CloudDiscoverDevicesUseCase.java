package org.openconnectivity.otgc.domain.usecase.cloud;

import org.openconnectivity.otgc.data.repository.CloudRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;

import javax.inject.Inject;

import io.reactivex.Observable;

public class CloudDiscoverDevicesUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudDiscoverDevicesUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Observable<Device> execute() {
        return cloudRepository.discoverDevices();
    }
}
