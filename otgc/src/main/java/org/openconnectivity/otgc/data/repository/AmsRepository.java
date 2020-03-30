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

import org.iotivity.OCAceConnectionType;
import org.iotivity.OCAceResource;
import org.iotivity.OCAceWildcard;
import org.iotivity.OCObt;
import org.iotivity.OCObtAclHandler;
import org.iotivity.OCObtDeviceStatusHandler;
import org.iotivity.OCObtStatusHandler;
import org.iotivity.OCSecurityAce;
import org.iotivity.OCSecurityAcl;
import org.iotivity.OCUuid;
import org.iotivity.OCUuidUtil;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAceResource;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAcl;
import org.openconnectivity.otgc.utils.constant.OcfWildcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class AmsRepository {

    @Inject
    AmsRepository() {

    }

    public Single<OcAcl> getAcl(String deviceId) {
        return  Single.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);

            OCObtAclHandler handler = (OCSecurityAcl acl) -> {
                if (acl != null) {
                    OcAcl aclRet = new OcAcl();
                    aclRet.parseOCRepresentation(acl);
                    emitter.onSuccess(aclRet);

                    /* Freeing the ACL structure */
                    OCObt.freeAcl(acl);
                } else {
                    String error = "GET ACL error";
                    Timber.d(error);
                    emitter.onError(new Exception(error));
                }
            };

            int ret = OCObt.retrieveAcl(uuid, handler);
            if (ret >= 0) {
                Timber.d("Successfully issued request to RETRIEVE /oic/sec/acl2");
            } else {
                String error = "ERROR issuing request to RETRIEVE /oic/sec/acl2";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    private Completable provisionAce(String deviceId, OCSecurityAce ace, List<String> verticalResources, long permission) {
        return Completable.create(emitter -> {
            int ret;
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);

            ret = setAceResources(ace, verticalResources);
            if (ret == -1) {
                String errorMsg = "ERROR: Could not create ACE resources";
                Timber.e(errorMsg);
                emitter.onError(new Exception(errorMsg));
            }

            OCObt.aceAddPermission(ace, (int)permission);

            OCObtDeviceStatusHandler handler = (OCUuid uuid, int status) -> {
                if (status >= 0) {
                    Timber.d("Successfully provisioned ACE to device " + OCUuidUtil.uuidToString(uuid));
                    emitter.onComplete();
                } else {
                    String errorMsg = "ERROR provisioning ACE to device " + OCUuidUtil.uuidToString(uuid);
                    Timber.e(errorMsg);
                    emitter.onError(new Exception(errorMsg));
                }
            };

            ret = OCObt.provisionAce(di, ace, handler);
            if (ret >= 0) {
                Timber.d("Successfully issued request to provision ACE");
            } else {
                String errorMsg = "ERROR issuing request to provision ACE";
                Timber.e(errorMsg);
                emitter.onError(new Exception(errorMsg));
            }
        });
    }

    public Completable provisionUuidAce(String deviceId, String subjectId, List<String> verticalResources, long permission) {
        OCUuid di = OCUuidUtil.stringToUuid(subjectId);

        OCSecurityAce ace = OCObt.newAceForSubject(di);
        if (ace == null) {
            String errorMsg = "ERROR: Could not create ACE";
            Timber.e(errorMsg);
            return Completable.error(new Exception(errorMsg));
        }

        return provisionAce(deviceId, ace, verticalResources, permission);
    }

    public Completable provisionRoleAce(String deviceId, String roleId, String roleAuthority, List<String> verticalResources, long permission) {
        OCSecurityAce ace = OCObt.newAceForRole(roleId, roleAuthority);
        if (ace == null) {
            String errorMsg = "ERROR: Could not create ACE";
            Timber.e(errorMsg);
            return Completable.error(new Exception(errorMsg));
        }

        return provisionAce(deviceId, ace, verticalResources, permission);
    }

    public Completable provisionConntypeAce(String deviceId, boolean isAuthCrypt, List<String> verticalResources, long permission) {
        OCSecurityAce ace = OCObt.newAceForConnection(isAuthCrypt ? OCAceConnectionType.OC_CONN_AUTH_CRYPT : OCAceConnectionType.OC_CONN_ANON_CLEAR);
        if (ace == null) {
            String errorMsg = "ERROR: Could not create ACE";
            Timber.e(errorMsg);
            return Completable.error(new Exception(errorMsg));
        }

        return provisionAce(deviceId, ace, verticalResources, permission);
    }

    public Completable provisionAuthWildcardAce(String deviceId) {
        return Completable.create(emitter -> {
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);

            OCObtDeviceStatusHandler handler = (OCUuid uuid, int status) -> {
                if (status >= 0) {
                    Timber.d("Successfully provisioned auth-crypt * ACE to device " + OCUuidUtil.uuidToString(uuid));
                    emitter.onComplete();
                } else {
                    String errorMsg = "ERROR provisioning ACE to device " + OCUuidUtil.uuidToString(uuid);
                    Timber.e(errorMsg);
                    emitter.onError(new IOException(errorMsg));
                }
            };

            int ret = OCObt.provisionAuthWildcardAce(di, handler);
            if (ret >= 0) {
                Timber.d("Successfully issued request to provision auth-crypt * ACE");
            } else {
                String errorMsg = "ERROR issuing request to provision auth-crypt * ACE";
                Timber.e(errorMsg);
                emitter.onError(new IOException(errorMsg));
            }
        });
    }

    public Completable provisionRoleWildcardAce(String deviceId, String roleId, String roleAuthority) {
        return Completable.create(emitter -> {
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);

            OCObtDeviceStatusHandler handler = (OCUuid uuid, int status) -> {
                if (status >= 0) {
                    Timber.d("Successfully provisioned role * ACE to device " + OCUuidUtil.uuidToString(uuid));
                    emitter.onComplete();
                } else {
                    String errorMsg = "ERROR provisioning ACE to device " + OCUuidUtil.uuidToString(uuid);
                    Timber.e(errorMsg);
                    emitter.onError(new IOException(errorMsg));
                }
            };

            int ret = OCObt.provisionRoleWildcardAce(di, roleId, roleAuthority, handler);
            if (ret >= 0) {
                Timber.d("Successfully issued request to provision role * ACE");
            } else {
                String errorMsg = "ERROR issuing request to provision role * ACE";
                Timber.e(errorMsg);
                emitter.onError(new IOException(errorMsg));
            }
        });
    }

    public Completable deleteAcl(String deviceId, long aceId) {
        return Completable.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);

            OCObtStatusHandler handler = (int status) -> {
                if (status >= 0) {
                    Timber.d("Delete ACE succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("Delete ACE error"));
                }
            };

            int ret = OCObt.deleteAceByAceId(uuid, (int)aceId, handler);
            if (ret >= 0) {
                Timber.d("Successfully issued request to DELETE /oic/sec/acl2 with aceid=" + aceId);
            } else {
                String error = "Could not send DELETE request to /oic/sec/acl2 with aceid=" + aceId;
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    private List<OcAceResource> getResources(List<String> verticalResources) {
        List<OcAceResource> resources = new ArrayList<>();
        for (String verticalResource : verticalResources) {
            OcAceResource res = new OcAceResource();
            if (OcfWildcard.isWildcard(verticalResource)) {
                res.setWildCard(verticalResource);
            } else {
                res.setHref(verticalResource);
            }
            /*List<String> types = new ArrayList<>();
            types.add("*");
            res.setResourceTypes(types);
            List<String> interfaces = new ArrayList<>();
            interfaces.add("*");
            res.setInterfaces(interfaces);*/
            resources.add(res);
        }

        return resources;
    }

    private int setAceResources(OCSecurityAce ace, List<String> resources) {
        for (String resource : resources) {
            OCAceResource res = OCObt.aceNewResource(ace);
            if (res == null) {
                String errorMsg = "ERROR: Could not allocate new resource for ACE";
                Timber.e(errorMsg);
                OCObt.freeAce(ace);
                return -1;
            }

            if (OcfWildcard.isWildcard(resource)) {
                if (resource.equals(OcfWildcard.OC_WILDCARD_ALL_NCR)) {
                    OCObt.aceResourceSetWc(res, OCAceWildcard.OC_ACE_WC_ALL);
                } else if (resource.equals(OcfWildcard.OC_WILDCARD_ALL_SECURE_NCR)) {
                    OCObt.aceResourceSetWc(res, OCAceWildcard.OC_ACE_WC_ALL_SECURED);
                } else if (resource.equals(OcfWildcard.OC_WILDCARD_ALL_NON_SECURE_NCR)) {
                    OCObt.aceResourceSetWc(res, OCAceWildcard.OC_ACE_WC_ALL_PUBLIC);
                }
            } else {
                OCObt.aceResourceSetHref(res, resource);
                OCObt.aceResourceSetWc(res, OCAceWildcard.OC_ACE_NO_WC);
            }

            // TODO: Set resource types

            // TODO: Set interfaces
        }

        return 0;
    }
}
