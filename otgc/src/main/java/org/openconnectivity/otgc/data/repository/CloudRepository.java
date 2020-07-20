package org.openconnectivity.otgc.data.repository;

import org.iotivity.OCCloud;
import org.iotivity.OCCloudContext;
import org.openconnectivity.otgc.domain.model.resource.cloud.OcCloudConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

@Singleton
public class CloudRepository {

    @Inject
    public CloudRepository() {

    }

    public Single<Integer> retrieveState() {
        return Single.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            short status = ctx.getStore().getStatus();
            emitter.onSuccess((int)status);
        });
    }

    public Single<OcCloudConfiguration> retrieveCloudConfiguration() {
        return Single.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);

            String apn = ctx.getStore().getAuth_provider();
            String url = ctx.getStore().getCi_server();
            String at = ctx.getStore().getAccess_token();
            String uuid = ctx.getStore().getSid();

            emitter.onSuccess(new OcCloudConfiguration(apn, url, at, uuid));
        });
    }

    public Completable provisionCloudConfiguration(String authProvider, String cloudUrl, String accessToken, String cloudUuid) {
        return Completable.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            OCCloud.provisionConfResource(ctx, authProvider, cloudUrl, accessToken, cloudUuid);
            emitter.onComplete();
        });
    }
}