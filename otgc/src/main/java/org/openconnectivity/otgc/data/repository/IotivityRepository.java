/*
 *  *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  *****************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  *****************************************************************
 */
package org.openconnectivity.otgc.data.repository;

import android.content.Context;

import org.iotivity.CborEncoder;
import org.iotivity.OCClientResponse;
import org.iotivity.OCCoreRes;
import org.iotivity.OCEndpoint;
import org.iotivity.OCEndpointUtil;
import org.iotivity.OCFactoryPresetsHandler;
import org.iotivity.OCIntrospection;
import org.iotivity.OCMain;
import org.iotivity.OCMainInitHandler;
import org.iotivity.OCObt;
import org.iotivity.OCObtDiscoveryHandler;
import org.iotivity.OCQos;
import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.iotivity.OCResponseHandler;
import org.iotivity.OCStatus;
import org.iotivity.OCStorage;
import org.iotivity.OCUuid;
import org.iotivity.OCUuidUtil;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;
import org.openconnectivity.otgc.domain.model.resource.virtual.p.OcPlatformInfo;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcEndpoint;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcRes;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcResource;
import org.openconnectivity.otgc.utils.constant.DiscoveryScope;
import org.openconnectivity.otgc.utils.constant.OcfResourceType;
import org.openconnectivity.otgc.data.entity.DeviceEntity;
import org.openconnectivity.otgc.data.persistence.dao.DeviceDao;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;
import org.openconnectivity.otgc.utils.constant.OtgcConstant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class IotivityRepository {
    private static final List<String> RESOURCE_TYPES_TO_FILTER;
    static {
        RESOURCE_TYPES_TO_FILTER = new ArrayList<>();

        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.DOXM);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.PSTAT);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.ACL2);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.CRED);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.CRL);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.CSR);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.ROLES);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.SECURITY_PROFILES);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.DEVICE);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.PLATFORM);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.INTROSPECTION);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.DEVICE_CONF);
    }

    private List<Device> unownedDevices = new ArrayList<>();
    private List<Device> ownedDevices = new ArrayList<>();
    private List<Device> allDevices = new ArrayList<>();

    private final Context ctx;
    private final DeviceDao deviceDao;
    private final PreferencesRepository preferencesRepository;

    @Inject
    public IotivityRepository(Context ctx, DeviceDao deviceDao, PreferencesRepository prefRepository) {
        this.ctx = ctx;
        this.deviceDao = deviceDao;
        this.preferencesRepository = prefRepository;
    }

    public Completable initOICStack() {
        return Completable.create(emitter -> {
            Timber.d("initOICStack");

            String filesDir = ctx.getFilesDir().getPath() + File.separator;
            File directory = new File(filesDir + OtgcConstant.OTGC_CREDS_DIR);
            if (!directory.exists()) {
                directory.mkdir();
            }

            Timber.d("Storage Config PATH : " + directory.getPath());
            if (OCStorage.storageConfig(directory.getPath()) != 0) {
                Timber.e("Failed to setup Storage Config.");
            }

            File introspectionFile = new File(filesDir + OtgcConstant.INTROSPECTION_CBOR_FILE);
            int size = (int) introspectionFile.length();
            byte[] introspectionData = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(introspectionFile));
                buf.read(introspectionData, 0, introspectionData.length);
                buf.close();
            } catch (FileNotFoundException e) {
                emitter.onError(e);
            } catch (IOException e) {
                emitter.onError(e);
            }
            OCIntrospection.setIntrospectionData(0 /* First device */, introspectionData);

            int ret = OCMain.mainInit(new OCMainInitHandler() {
                @Override
                public int initialize() {
                    Timber.d("In OCMainInitHandler.initilize()");
                    int ret = OCMain.initPlatform("OCF");
                    ret |= OCMain.addDevice("/oic/d", "oic.d.phone", "OTGC", "ocf.2.4.0", "ocf.res.1.3.0");
                    return ret;
                }

                @Override
                public void registerResources() {
                    Timber.d("In OCMainInitHandler.registerResources()");
                }

                @Override
                public void requestEntry() {
                    Timber.d("In OCMainInitHandler.requestEntry()");
                    OCObt.init();

                    emitter.onComplete();
                }
            });
            if (ret < 0) {
                String error = "OCMain.mainInit has failed with result: " + ret;
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    public Single<String> getDeviceId() {
        return Single.create(emitter -> {
            OCUuid uuid = OCCoreRes.getDeviceId(0 /* First registered device */);
            emitter.onSuccess(OCUuidUtil.uuidToString(uuid));
        });
    }

    public Completable setFactoryResetHandler(OCFactoryPresetsHandler handler) {
        return Completable.create(emitter -> {
            OCMain.setFactoryPresetsHandler(handler);
            emitter.onComplete();
        });
    }

    public Observable<Device> scanUnownedDevices() {
        return Completable.create(emitter -> {
            Timber.d("Discovering unowned devices");
            unownedDevices.clear();

            OCObtDiscoveryHandler handler = (uuid, endpoints) -> {
                String deviceId = OCUuidUtil.uuidToString(uuid);
                Timber.d("Discovered unowned device: " + deviceId);

                DeviceEntity device = deviceDao.findById(deviceId).blockingGet();
                if (device == null) {
                    deviceDao.insert(new DeviceEntity(deviceId, "", endpoints, DeviceType.UNOWNED, Device.NOTHING_PERMITS));
                } else {
                    deviceDao.insert(new DeviceEntity(deviceId, device.getName(), endpoints, DeviceType.UNOWNED, Device.NOTHING_PERMITS));
                }

                unownedDevices.add(new Device(DeviceType.UNOWNED, deviceId, new OcDeviceInfo(), endpoints, Device.NOTHING_PERMITS));
            };

            int ret;
            String scope = preferencesRepository.getDiscoveryScope();
            if (scope.equals(DiscoveryScope.DISCOVERY_SCOPE_SITE)) {
                ret = OCObt.discoverUnownedDevicesSiteLocalIPv6(handler);
            } else if (scope.equals(DiscoveryScope.DISCOVERY_SCOPE_REALM)) {
                ret = OCObt.discoverUnownedDevicesRealmLocalIPv6(handler);
            } else {
                ret = OCObt.discoverUnownedDevices(handler);
            }
            if (ret < 0) {
                String error = "ERROR discovering un-owned Devices.";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        }).timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
                .onErrorComplete()
                .andThen(Observable.fromIterable(unownedDevices));
    }

    public Observable<Device> scanOwnedDevices() {
        return Completable.create(emitter -> {
            Timber.d("Discovering owned devices");
            ownedDevices.clear();

            OCObtDiscoveryHandler handler = (uuid, endpoints) -> {
                String deviceId = OCUuidUtil.uuidToString(uuid);
                Timber.d("Discovered owned device: "+ deviceId);

                DeviceEntity device = deviceDao.findById(deviceId).blockingGet();
                if (device == null) {
                    deviceDao.insert(new DeviceEntity(deviceId, "", endpoints, DeviceType.OWNED_BY_SELF, Device.FULL_PERMITS));
                } else {
                    deviceDao.insert(new DeviceEntity(deviceId, device.getName(), endpoints, DeviceType.OWNED_BY_SELF, Device.FULL_PERMITS));
                }

                ownedDevices.add(new Device(DeviceType.OWNED_BY_SELF, deviceId, new OcDeviceInfo(), endpoints, Device.FULL_PERMITS));
            };

            int ret;
            String scope = preferencesRepository.getDiscoveryScope();
            if (scope.equals(DiscoveryScope.DISCOVERY_SCOPE_SITE)) {
                ret = OCObt.discoverOwnedDevicesSiteLocalIPv6(handler);
            } else if (scope.equals(DiscoveryScope.DISCOVERY_SCOPE_REALM)) {
                ret = OCObt.discoverOwnedDevicesRealmLocalIPv6(handler);
            } else {
                ret = OCObt.discoverOwnedDevices(handler);
            }
            if (ret < 0) {
                String error = "ERROR discovering owned Devices.";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        }).timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
                .onErrorComplete()
                .andThen(Observable.fromIterable(ownedDevices));
    }

    public Completable scanHosts() {
        return Completable.create(emitter -> {
            // Clear all devices list for devices owned by other
            allDevices.clear();

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcRes res = new OcRes();
                    res.parseOCRepresentation(response.getPayload());

                    OcResource resource = res.getResourceList().get(0);
                    String deviceId = resource.getAnchor().replace("ocf://", "");
                    List<String> endpoints = new ArrayList<>();
                    for (OcEndpoint ep : resource.getEndpoints()) {
                        endpoints.add(ep.getEndpoint());
                    }

                    DeviceEntity device = deviceDao.findById(deviceId).blockingGet();
                    if (device == null) {
                        deviceDao.insert(new DeviceEntity(deviceId, "", endpoints, DeviceType.OWNED_BY_OTHER, Device.NOTHING_PERMITS));
                        allDevices.add(new Device(DeviceType.OWNED_BY_OTHER, deviceId, new OcDeviceInfo(), endpoints, Device.NOTHING_PERMITS));
                    } else {
                        deviceDao.insert(new DeviceEntity(deviceId, device.getName(), endpoints, device.getType(), device.getPermits()));
                        allDevices.add(new Device(device.getType(), deviceId, new OcDeviceInfo(), endpoints, device.getPermits()));
                    }

                }
            };

            String scope = preferencesRepository.getDiscoveryScope();
            if (scope.equals(DiscoveryScope.DISCOVERY_SCOPE_SITE)) {
                if (!OCMain.doRealmLocalIPv6Multicast(OcfResourceUri.RES_URI, null, handler)) {
                    emitter.onError(new Exception("Error scanning hosts"));
                }
            } else if (scope.equals(DiscoveryScope.DISCOVERY_SCOPE_REALM)) {
                if (!OCMain.doSiteLocalIPv6Multicast(OcfResourceUri.RES_URI, null, handler)) {
                    emitter.onError(new Exception("Error scanning hosts"));
                }
            } else {
                if (!OCMain.doIPMulticast(OcfResourceUri.RES_URI, null, handler)) {
                    emitter.onError(new Exception("Error scanning hosts"));
                }
            }

        }).timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
                .onErrorComplete();
    }

    public Observable<Device> scanOwnedByOtherDevices() {
        return scanHosts()
                .andThen(Observable.fromIterable(allDevices))
                .filter(device -> {
                    boolean isNotUnowned = true;
                    for (Device d : unownedDevices) {
                        if (d.getDeviceId().equals(device.getDeviceId())) {
                            isNotUnowned = false;
                        }
                    }

                    return isNotUnowned;
                })
                .filter(device -> {
                    boolean isNotOwned = true;
                    for (Device d : ownedDevices) {
                        if (d.getDeviceId().equals(device.getDeviceId())) {
                            isNotOwned = false;
                        }
                    }

                    return isNotOwned;
                })
                .filter(device -> !device.getDeviceId().equals(getDeviceId().blockingGet()));
    }

    public Single<String> getNonSecureEndpoint(Device device) {
        return Single.create(emitter -> {
            String endpoint = device.getIpv6Host();
            if (endpoint == null) {
                endpoint = device.getIpv4Host();
            }
            emitter.onSuccess(endpoint);
        });
    }

    public Single<String> getSecureEndpoint(Device device) {
        return Single.create(emitter -> {
            String endpoint = device.getIpv6SecureHost();
            if (endpoint == null) {
                endpoint = device.getIpv4SecureHost();
            }
            emitter.onSuccess(endpoint);
        });
    }

    public Single<String> getNonSecureEndpoint(List<String> endpoints) {
        return Single.create(emitter -> {
            String ep = null;

            for (String endpoint : endpoints) {
                if (endpoint.startsWith("coap")
                        && endpoint.contains(".")) {
                    ep = endpoint;
                } else if (endpoint.startsWith("coap")) {
                    ep =endpoint;
                    break;
                }
            }

            emitter.onSuccess(ep);
        });
    }

    public Single<String> getSecureEndpoint(List<String> endpoints) {
        return Single.create(emitter -> {
            String ep = null;

            for (String endpoint : endpoints) {
                if (endpoint.startsWith("coaps")
                        && endpoint.contains(".")) {
                    ep = endpoint;
                } else if (endpoint.startsWith("coaps")) {
                    ep =endpoint;
                    break;
                }
            }

            emitter.onSuccess(ep);
        });
    }

    public Single<OcDeviceInfo> getDeviceInfo(String endpoint) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

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

            if (!OCMain.doGet(OcfResourceUri.DEVICE_INFO_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Get device info error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Single<OcPlatformInfo> getPlatformInfo(String endpoint) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcPlatformInfo platformInfo = new OcPlatformInfo();
                    platformInfo.setOCRepresentation(response.getPayload());
                    emitter.onSuccess(platformInfo);
                } else {
                    emitter.onError(new Exception("Get device platform error - code: " + code));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.PLATFORM_INFO_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Get device platform error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Single<String> getDeviceName(String deviceId) {
        return Single.create(emitter -> {
            DeviceEntity device = deviceDao.findById(deviceId).blockingGet();
            emitter.onSuccess(device.getName());
        });
    }

    public Completable setDeviceName(String deviceId, String deviceName) {
        return Completable.fromAction(() -> deviceDao.updateDeviceName(deviceId, deviceName));
    }

    public Completable updateDeviceType(String deviceId, DeviceType type, int permits) {
        return Completable.fromAction(() -> deviceDao.updateDeviceType(deviceId, type, permits));
    }

    public Maybe<DeviceEntity> getDeviceFromDatabase(String deviceId) {
        return deviceDao.findById(deviceId);
    }

    public int getDiscoveryTimeout() {
        return preferencesRepository.getDiscoveryTimeout();
    }

    public Single<List<OcResource>> findVerticalResources(String host) {
        return findResources(host)
                .map(ocRes -> {
                    List<OcResource> resourceList = new ArrayList<>();
                    for (OcResource resource : ocRes.getResourceList()) {
                        for (String resourceType : resource.getResourceTypes()) {
                            if (!RESOURCE_TYPES_TO_FILTER.contains(resourceType)
                                    && !resourceType.startsWith("oic.d.")) {
                                resourceList.add(resource);
                                break;
                            }
                        }
                    }

                    return resourceList;
                });
    }

    public Single<OcRes> findResources(String host) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(host, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcRes ocRes = new OcRes();
                    ocRes.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(ocRes);
                } else {
                    emitter.onError(new Exception("Find resources error - code: " + code));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.RES_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Find resources error"));
            }

            try {
                Thread.sleep(getDiscoveryTimeout() * 1_000L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                Timber.e(ex.getMessage());
            }
        });
    }

    public Single<OcRes> findResource(String host, String resourceType) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(host, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcRes res = new OcRes();
                    res.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(res);
                } else {
                    emitter.onError(new Exception("Find resource error - code: " + code));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.RES_URI, ep, OcfResourceUri.RESOURCE_TYPE_FILTER + resourceType, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Find resource error"));
            }

            try {
                Thread.sleep(getDiscoveryTimeout() * 1_000L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                Timber.e(ex.getMessage());
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Single<OCRepresentation> get(String host, String uri, String deviceId) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(host, ep, new String[1]);
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK)) {
                    emitter.onSuccess(response.getPayload());
                } else {
                    emitter.onError(new Exception("GET request error - code: " + code));
                }
            };

            if (!OCMain.doGet(uri, ep, null, handler, OCQos.LOW_QOS)) {
                emitter.onError(new Exception("Error in GET request"));
            }
        });
    }

    public Completable post(String host, String uri, String deviceId, OCRepresentation rep, Object valueArray) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(host, ep, new String[1]);
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_CHANGED) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("POST " + uri + " error - code: " + code));
                }
            };

            if (OCMain.initPost(uri, ep, null, handler, OCQos.HIGH_QOS)) {
                CborEncoder root = OCRep.beginRootObject();
                parseOCRepresentionToCbor(root, rep, valueArray);
                OCRep.endRootObject();

                if (!OCMain.doPost()) {
                    emitter.onError(new Exception("Do POST " + uri + " error"));
                }
            } else {
                emitter.onError(new Exception("Init POST " + uri + " error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
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
                default:
                    break;
            }

            rep = rep.getNext();
        }
    }

    public void close() {
        Timber.d("Calling OCMain.mainShutdown()");
        OCMain.mainShutdown();
    }
}
