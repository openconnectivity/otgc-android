package org.openconnectivity.otgc.data.repository;

import org.iotivity.CborEncoder;
import org.iotivity.OCClientResponse;
import org.iotivity.OCCloud;
import org.iotivity.OCCloudContext;
import org.iotivity.OCCloudHandler;
import org.iotivity.OCCloudStatusMask;
import org.iotivity.OCCoreRes;
import org.iotivity.OCDiscoveryAllHandler;
import org.iotivity.OCDiscoveryFlags;
import org.iotivity.OCEndpoint;
import org.iotivity.OCEndpointUtil;
import org.iotivity.OCMain;
import org.iotivity.OCQos;
import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.iotivity.OCResponseHandler;
import org.iotivity.OCStatus;
import org.iotivity.OCUuid;
import org.iotivity.OCUuidUtil;
import org.openconnectivity.otgc.data.entity.DeviceEntity;
import org.openconnectivity.otgc.data.persistence.dao.DeviceDao;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;
import org.openconnectivity.otgc.domain.model.resource.cloud.OcCloudConfiguration;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;
import org.openconnectivity.otgc.domain.model.resource.virtual.p.OcPlatformInfo;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcEndpoint;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcResource;
import org.openconnectivity.otgc.utils.constant.OcfResourceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class CloudRepository {

    private final DeviceDao deviceDao;
    private final PreferencesRepository preferencesRepository;

    private List<Device> devices = new ArrayList<>();
    private Map<String, List<String>> resources = new HashMap<>();

    @Inject
    public CloudRepository(PreferencesRepository preferencesRepository,
                           DeviceDao deviceDao) {
        this.preferencesRepository = preferencesRepository;
        this.deviceDao = deviceDao;
    }

    private OCCloudHandler handler = (ctx, status) -> {
        Timber.d("Cloud Manager Status:");
        if ((status & OCCloudStatusMask.OC_CLOUD_REGISTERED) == OCCloudStatusMask.OC_CLOUD_REGISTERED) {
            Timber.d("\t\t-Registered");
        }
        if ((status & OCCloudStatusMask.OC_CLOUD_TOKEN_EXPIRY) == OCCloudStatusMask.OC_CLOUD_TOKEN_EXPIRY) {
            Timber.d("\t\t-Token Expiry: ");
            if (ctx != null) {
                Timber.d(this.retrieveTokenExpiry().blockingGet() + "\n");
            }
        }
        if ((status & OCCloudStatusMask.OC_CLOUD_FAILURE) == OCCloudStatusMask.OC_CLOUD_FAILURE) {
            Timber.e("\t\t-Failure");
        }
        if ((status & OCCloudStatusMask.OC_CLOUD_LOGGED_IN) == OCCloudStatusMask.OC_CLOUD_LOGGED_IN) {
            Timber.d("\t\t-Logged In");
        }
        if ((status & OCCloudStatusMask.OC_CLOUD_LOGGED_OUT) == OCCloudStatusMask.OC_CLOUD_LOGGED_OUT) {
            Timber.d("\t\t-Logged Out");
        }
        if ((status & OCCloudStatusMask.OC_CLOUD_DEREGISTERED) == OCCloudStatusMask.OC_CLOUD_DEREGISTERED) {
            Timber.d("\t\t-DeRegistered");
        }
        if ((status & OCCloudStatusMask.OC_CLOUD_REFRESHED_TOKEN) == OCCloudStatusMask.OC_CLOUD_REFRESHED_TOKEN) {
            Timber.d("\t\t-Refreshed Token");
        }
    };

    public Completable initCloud() {
        OCCloudContext ctx = OCCloud.getContext(0);

        if (ctx != null) {
            String apn = ctx.getStore().getAuth_provider();
            String url = ctx.getStore().getCi_server();
            String at = ctx.getStore().getAccess_token();
            String uuid = ctx.getStore().getSid();

            return provisionCloudConfiguration(apn, url, at, uuid);
        }

        return Completable.complete();
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
            if (ctx != null) {
                OCCloud.managerStart(ctx, this.handler);
                OCCloud.provisionConfResource(ctx, cloudUrl, accessToken, cloudUuid, authProvider);
            }
            emitter.onComplete();
        });
    }

    public Completable register() {
        return Completable.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            if (ctx == null) {
                emitter.onError(new Exception("Cloud context is null"));
            }

            int ret = OCCloud.registerCloud(ctx, this.handler);
            if (ret < 0) {
                String error = "Could not issue Cloud Register request.";
                Timber.e(error);
                emitter.onError(new Exception(error));
            } else {
                Timber.d("Issued Cloud Register request.");
            }
        })
        .timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
        .onErrorComplete();
    }

    public Completable deregister() {
        return Completable.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            if (ctx == null) {
                emitter.onError(new Exception("Cloud context is null"));
            }

            int ret = OCCloud.deregisterCloud(ctx, this.handler);
            if (ret < 0) {
                String error = "Could not issue Cloud Deregister request.";
                Timber.e(error);
                emitter.onError(new Exception(error));
            } else {
                Timber.d("Issued Cloud Deregister request.");
            }
        })
        .timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
        .onErrorComplete();
    }

    public Completable login() {
        return Completable.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            if (ctx == null) {
                emitter.onError(new Exception("Cloud context is null"));
            }

            int ret = OCCloud.login(ctx, this.handler);
            if (ret < 0) {
                String error = "Could not issue Cloud Login request.";
                Timber.e(error);
                emitter.onError(new Exception(error));
            } else {
                Timber.d("Issued Cloud Login request.");
            }
        })
        .timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
        .onErrorComplete();
    }

    public Completable logout() {
        return Completable.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            if (ctx == null) {
                emitter.onError(new Exception("Cloud context is null"));
            }

            int ret = OCCloud.logout(ctx, this.handler);
            if (ret < 0) {
                String error = "Could not issue Cloud Logout request.";
                Timber.e(error);
                emitter.onError(new Exception(error));
            } else {
                Timber.d("Issued Cloud Logout request.");
            }
        })
        .timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
        .onErrorComplete();
    }

    public Completable retrieveTokenExpiry() {
        return Completable.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            if (ctx == null) {
                emitter.onError(new Exception("Cloud context is null"));
            }

            OCCloud.getTokenExpiry(ctx);
            emitter.onComplete();
        });
    }

    public Completable refreshToken() {
        return Completable.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            if (ctx == null) {
                emitter.onError(new Exception("Cloud context is null"));
            }

            int ret = OCCloud.refreshToken(ctx, this.handler);
            if (ret < 0) {
                String error = "Could not issue refresh token request.";
                Timber.e(error);
                emitter.onError(new Exception(error));
            } else {
                Timber.d("Issued refresh token request.");
            }
        })
        .timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
        .onErrorComplete();
    }

    public Observable<Device> discoverDevices() {
        return Completable.create(emitter -> {
            devices.clear();

            OCCloudContext ctx = OCCloud.getContext(0);

            OCDiscoveryAllHandler handler =
                    (String anchor, String uri, String[] types, int interfaceMask, OCEndpoint endpoints,
                     int resourcePropertiesMask, boolean more) -> {
                        String deviceId = anchor.substring(anchor.lastIndexOf('/') + 1);

                        DeviceEntity device = deviceDao.findById(deviceId).blockingGet();
                        if (device == null) {
                            deviceDao.insert(new DeviceEntity(deviceId, "", endpoints, DeviceType.CLOUD, Device.NOTHING_PERMITS));
                        }

                        Device device1 = new Device(DeviceType.CLOUD, deviceId, new OcDeviceInfo(), endpoints, Device.NOTHING_PERMITS);
                        if (!devices.contains(device1)) {
                            devices.add(device1);
                        }

                        List<String> uriList = new ArrayList<>();
                        if (resources.containsKey(deviceId)) {
                            uriList = resources.get(deviceId);
                        }
                        uriList.add(uri);
                        resources.put(deviceId, uriList);

                        if(!more) {
                            emitter.onComplete();
                            return OCDiscoveryFlags.OC_STOP_DISCOVERY;
                        }
                        return OCDiscoveryFlags.OC_CONTINUE_DISCOVERY;
                    };

            if (ctx == null){
                emitter.onComplete();
            } else if (OCCloud.discoverResources(ctx, handler) != 0) {
                String error = "ERROR: could not issue discovery request";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        }).timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
                .onErrorComplete()
                .andThen(Observable.fromIterable(devices))
                .filter(device -> !device.getDeviceId().equals(getDeviceId().blockingGet()));
    }

    public Single<List<String>> getResources(String deviceId) {
        return Single.create(emitter -> {
            if (resources.containsKey(deviceId)) {
                emitter.onSuccess(resources.get(deviceId));
            } else {
                emitter.onError(new Exception("Device UUID is not found"));
            }
        });
    }

    public Single<String> retrieveUri(String deviceUuid, String resourceUri) {
        return Single.create(emitter -> {
            if (resources.containsKey(deviceUuid)) {
                List<String> uriList = resources.get(deviceUuid);
                String res = "";
                for (String uri : uriList) {
                    if (uri.endsWith(resourceUri)) {
                        res = uri;
                        break;
                    }
                }

                if (!res.isEmpty()) {
                    emitter.onSuccess(res);
                } else {
                    emitter.onError(new Exception("URI is not found"));
                }
            } else {
                emitter.onError(new Exception("Device UUID is not found"));
            }
        });
    }

    public Single<OCEndpoint> retrieveEndpoint() {
        return Single.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);
            emitter.onSuccess(ctx.getCloudEndpoint());
        });
    }

    public Single<OcDeviceInfo> retrieveDeviceInfo(OCEndpoint endpoint, String uri) {
        return Single.create(emitter -> {
            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcDeviceInfo deviceInfo = new OcDeviceInfo();
                    deviceInfo.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(deviceInfo);
                } else {
                    emitter.onError(new Exception("Get device info error - code: " + code));
                }
            };

            if (!OCMain.doGet(uri, endpoint, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Get device info error"));
            }
        });
    }

    public Single<OcPlatformInfo> retrievePlatformInfo(OCEndpoint endpoint, String uri) {
        return Single.create(emitter -> {
            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcPlatformInfo platformInfo = new OcPlatformInfo();
                    platformInfo.setOCRepresentation(response.getPayload());
                    emitter.onSuccess(platformInfo);
                } else {
                    emitter.onError(new Exception("Get device info error - code: " + code));
                }
            };

            if (!OCMain.doGet(uri, endpoint, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Get device info error"));
            }
        });
    }

    public Observable<OcResource> discoverAllResources(String deviceId) {
        return Observable.create(emitter -> {
            OCCloudContext ctx = OCCloud.getContext(0);

            OCDiscoveryAllHandler handler =
                    (String anchor, String uri, String[] types, int interfaceMask, OCEndpoint endpoints,
                     int resourcePropertiesMask, boolean more) -> {
                        String uuid = anchor.substring(anchor.lastIndexOf('/') + 1);
                        if (deviceId.equals(uuid)) {
                            OcResource resource = new OcResource();
                            resource.setAnchor(anchor);
                            resource.setHref(uri);

                            List<OcEndpoint> epList = new ArrayList<>();
                            OCEndpoint ep = endpoints;
                            while (ep != null) {
                                OcEndpoint endpoint = new OcEndpoint();
                                endpoint.setEndpoint(OCEndpointUtil.toString(ep));
                                epList.add(endpoint);
                                ep = ep.getNext();
                            }
                            resource.setEndpoints(epList);
                            resource.setPropertiesMask((long)resourcePropertiesMask);
                            resource.setResourceTypes(Arrays.asList(types));
                            emitter.onNext(resource);
                        }

                        if(!more) {
                            emitter.onComplete();
                            return OCDiscoveryFlags.OC_STOP_DISCOVERY;
                        }
                        return OCDiscoveryFlags.OC_CONTINUE_DISCOVERY;
                    };

            if (ctx != null && OCCloud.discoverResources(ctx, handler) >= 0)
            {
                Timber.d("Successfully issued resource discovery request");
            } else {
                String error = "ERROR issuing resource discovery request";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    public Single<List<OcResource>> discoverVerticalResources(String deviceId) {
        return discoverAllResources(deviceId)
                .toList()
                .map(resources -> {
                    List<OcResource> resourceList = new ArrayList<>();
                    for (OcResource resource : resources) {
                        for (String resourceType : resource.getResourceTypes()) {
                            if (OcfResourceType.isVerticalResourceType(resourceType)
                                    && !resourceType.startsWith("oic.d.")) {
                                resourceList.add(resource);
                                break;
                            }
                        }
                    }

                    return resourceList;
                });
    }

    public Single<OCRepresentation> get(OCEndpoint endpoint, String uri, String deviceId) {
        return Single.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(endpoint, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK)) {
                    emitter.onSuccess(response.getPayload());
                } else {
                    emitter.onError(new Exception("GET request error - code: " + code));
                }
            };

            if (!OCMain.doGet(uri, endpoint, null, handler, OCQos.LOW_QOS)) {
                emitter.onError(new Exception("Error in GET request"));
            }
        });
    }

    public Completable post(OCEndpoint endpoint, String uri, String deviceId, OCRepresentation rep, Object valueArray) {
        return Completable.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(endpoint, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_CHANGED) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("POST " + uri + " error - code: " + code));
                }
            };

            if (OCMain.initPost(uri, endpoint, null, handler, OCQos.HIGH_QOS)) {
                CborEncoder root = OCRep.beginRootObject();
                parseOCRepresentionToCbor(root, rep, valueArray);
                OCRep.endRootObject();

                if (!OCMain.doPost()) {
                    emitter.onError(new Exception("Do POST " + uri + " error"));
                }
            } else {
                emitter.onError(new Exception("Init POST " + uri + " error"));
            }
        });
    }

    public Completable post(OCEndpoint endpoint, String uri, String deviceId, Map<String, Object> values) {
        return Completable.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(endpoint, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_CHANGED) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("POST " + uri + " error - code: " + code));
                }
            };

            if (OCMain.initPost(uri, endpoint, null, handler, OCQos.HIGH_QOS)) {
                CborEncoder root = OCRep.beginRootObject();
                parseOCRepresentionToCbor(root, values);
                OCRep.endRootObject();

                if (!OCMain.doPost()) {
                    emitter.onError(new Exception("Do POST " + uri + " error"));
                }
            } else {
                emitter.onError(new Exception("Init POST " + uri + " error"));
            }
        });
    }

    private void parseOCRepresentionToCbor(CborEncoder parent, OCRepresentation rep, Object valueArray) {
        while (rep != null) {
            switch (rep.getType()) {
                case OC_REP_BOOL:
                    OCRep.setBoolean(parent, rep.getName(), rep.getValue().getBool());
                    break;
                case OC_REP_INT:
                    OCRep.setLong(parent, rep.getName(), rep.getValue().getInteger());
                    break;
                case OC_REP_DOUBLE:
                    OCRep.setDouble(parent, rep.getName(), rep.getValue().getDouble());
                    break;
                case OC_REP_STRING:
                    OCRep.setTextString(parent, rep.getName(), rep.getValue().getString());
                    break;
                case OC_REP_INT_ARRAY:
                    OCRep.setLongArray(parent, rep.getName(), (long[])valueArray);
                    break;
                case OC_REP_DOUBLE_ARRAY:
                    OCRep.setDoubleArray(parent, rep.getName(), (double[])valueArray);
                    break;
                case OC_REP_STRING_ARRAY:
                    OCRep.setStringArray(parent, rep.getName(), (String[])valueArray);
                    break;
                case OC_REP_BOOL_ARRAY:
                    OCRep.setBooleanArray(parent, rep.getName(), (boolean[])valueArray);
                    break;
                default:
                    break;
            }

            rep = rep.getNext();
        }
    }

    private void parseOCRepresentionToCbor(CborEncoder parent, Map<String, Object> values) {
        for (String key : values.keySet()) {
            if (values.get(key) instanceof Boolean) {
                OCRep.setBoolean(parent, key, (boolean)values.get(key));
            } else if (values.get(key) instanceof Integer) {
                OCRep.setLong(parent, key, (Integer)values.get(key));
            } else if (values.get(key) instanceof Double) {
                OCRep.setDouble(parent, key, (Double)values.get(key));
            } else if (values.get(key) instanceof String) {
                OCRep.setTextString(parent, key, (String)values.get(key));
            } else if (values.get(key) instanceof List) {
                if (((List) values.get(key)).get(0) instanceof String) {
                    String[] ret = new String[((List<String>)values.get(key)).size()];
                    for (int i=0; i< ((List<String>)values.get(key)).size(); i++) {
                        ret[i] = ((List<String>)values.get(key)).get(i);
                    }
                    OCRep.setStringArray(parent, key, ret);
                } else if (((List) values.get(key)).get(0) instanceof Integer) {
                    long[] ret = new long[((List<Integer>)values.get(key)).size()];
                    for (int i=0; i< ((List<Integer>)values.get(key)).size(); i++) {
                        ret[i] = ((List<Integer>)values.get(key)).get(i);
                    }
                    OCRep.setLongArray(parent, key, ret);
                } else if (((List) values.get(key)).get(0) instanceof Double) {
                    double[] ret = new double[((List<Double>)values.get(key)).size()];
                    for (int i=0; i< ((List<Double>)values.get(key)).size(); i++) {
                        ret[i] = ((List<Double>)values.get(key)).get(i);
                    }
                    OCRep.setDoubleArray(parent, key, ret);
                } else if (((List) values.get(key)).get(0) instanceof Boolean) {
                    boolean[] ret = new boolean[((List<Boolean>)values.get(key)).size()];
                    for (int i=0; i< ((List<Boolean>)values.get(key)).size(); i++) {
                        ret[i] = ((List<Boolean>)values.get(key)).get(i);
                    }
                    OCRep.setBooleanArray(parent, key, ret);
                }
            }
        }
    }

    public Single<String> getDeviceId() {
        return Single.create(emitter -> {
            OCUuid uuid = OCCoreRes.getDeviceId(0 /* First registered device */);
            emitter.onSuccess(OCUuidUtil.uuidToString(uuid));
        });
    }

    public int getDiscoveryTimeout() {
        return preferencesRepository.getDiscoveryTimeout();
    }
}