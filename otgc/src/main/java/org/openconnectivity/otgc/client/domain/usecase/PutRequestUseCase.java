package org.openconnectivity.otgc.client.domain.usecase;

import org.iotivity.base.OcRepresentation;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class PutRequestUseCase {
    private final IotivityRepository mIotivityRepository;

    @Inject
    public PutRequestUseCase(IotivityRepository iotivityRepository) {
        this.mIotivityRepository = iotivityRepository;
    }

    public Single<OcRepresentation> execute(String deviceId, String uri, List<String> resourceTypes, List<String> interfacesList,
                                            OcRepresentation rep) {
        return mIotivityRepository.getDeviceCoapsIpv6Host(deviceId)
                .flatMap(host ->
                        mIotivityRepository.constructResource(
                                host,
                                uri,
                                resourceTypes,
                                interfacesList))
                .flatMap(ocResource -> mIotivityRepository.put(ocResource, true, rep));
    }
}
