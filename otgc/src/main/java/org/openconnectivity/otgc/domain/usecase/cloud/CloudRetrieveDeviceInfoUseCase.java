package org.openconnectivity.otgc.domain.usecase.cloud;

import io.reactivex.Single;
import org.openconnectivity.otgc.data.repository.CloudRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;

import javax.inject.Inject;

public class CloudRetrieveDeviceInfoUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudRetrieveDeviceInfoUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<OcDeviceInfo> execute(Device device) {
        return cloudRepository.retrieveUri(device.getDeviceId(), OcfResourceUri.DEVICE_INFO_URI)
                .flatMap(uri -> cloudRepository.retrieveEndpoint()
                        .flatMap(endpoint -> cloudRepository.retrieveDeviceInfo(endpoint, uri)));
    }
}
