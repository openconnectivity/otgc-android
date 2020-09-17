package org.openconnectivity.otgc.domain.usecase.cloud;

import io.reactivex.Single;
import org.openconnectivity.otgc.data.repository.CloudRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.virtual.p.OcPlatformInfo;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;

import javax.inject.Inject;

public class CloudRetrievePlatformInfoUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudRetrievePlatformInfoUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<OcPlatformInfo> execute(Device device) {
        return cloudRepository.retrieveUri(device.getDeviceId(), OcfResourceUri.PLATFORM_INFO_URI)
                .flatMap(uri -> cloudRepository.retrieveEndpoint()
                        .flatMap(endpoint -> cloudRepository.retrievePlatformInfo(endpoint, uri)));
    }
}
