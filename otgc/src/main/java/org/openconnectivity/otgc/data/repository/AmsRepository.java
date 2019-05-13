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

import org.iotivity.CborEncoder;
import org.iotivity.OCClientResponse;
import org.iotivity.OCEndpoint;
import org.iotivity.OCEndpointUtil;
import org.iotivity.OCMain;
import org.iotivity.OCQos;
import org.iotivity.OCResponseHandler;
import org.iotivity.OCStatus;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAce;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAceResource;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAceSubject;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAceSubjectType;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAcl;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;

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

    public Single<OcAcl> getAcl(String endpoint) {
        return  Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                if (response.getCode().equals(OCStatus.OC_STATUS_OK)) {
                    OcAcl acl = new OcAcl();
                    acl.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(acl);
                } else {
                    Timber.d("GET ACL error - Status code: " + response.getCode());
                    emitter.onError(new Exception("GET ACL error - Status code: " + response.getCode()));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.ACL2_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("GET ACL error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    private Completable provisionAcl(String endpoint, OcAcl acl) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK) || code.equals(OCStatus.OC_STATUS_CHANGED)) {
                    Timber.d("Provision ACL succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new IOException("Provision ACE error"));
                }
            };

            if (OCMain.initPost(OcfResourceUri.ACL2_URI, ep, null, handler, OCQos.LOW_QOS)) {
                CborEncoder root = acl.parseToCbor();

                if (OCMain.doPost()) {
                    Timber.d("Sent POST request to /oic/sec/acl2");
                } else {
                    String error = "Could not send POST request to /oic/sec/acl2";
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            } else {
                String error = "Could not init POST request to /oic/sec/acl2";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Completable provisionUuidAcl(String endpoint, String subjectId, List<String> verticalResources, long permission) {
        OcAceSubject subject = new OcAceSubject();
        subject.setType(OcAceSubjectType.UUID_TYPE);
        subject.setUuid(subjectId);
        OcAce ace = new OcAce();
        ace.setSubject(subject);
        ace.setPermission((permission));
        ace.setResources(getResources(verticalResources));
        List<OcAce> aceList = new ArrayList<>();
        aceList.add(ace);

        OcAcl acl = new OcAcl();
        acl.setAceList(aceList);
        return provisionAcl(endpoint, acl);
    }

    public Completable provisionRoleAcl(String endpoint, String roleId, String roleAuthority, List<String> verticalResources, long permission) {
        OcAceSubject subject = new OcAceSubject();
        subject.setType(OcAceSubjectType.ROLE_TYPE);
        subject.setRoleId(roleId);
        subject.setAuthority(roleAuthority);
        OcAce ace = new OcAce();
        ace.setSubject(subject);
        ace.setPermission(permission);
        ace.setResources(getResources(verticalResources));
        List<OcAce> aceList = new ArrayList<>();
        aceList.add(ace);

        OcAcl acl = new OcAcl();
        acl.setAceList(aceList);
        return provisionAcl(endpoint, acl);
    }

    public Completable provisionConntypeAcl(String endpoint, boolean isAuthCrypt, List<String> verticalResources, long permission) {
        OcAceSubject subject = new OcAceSubject();
        subject.setType(OcAceSubjectType.CONN_TYPE);
        subject.setConnType(isAuthCrypt ? "auth-crypt" : "anon-clear");
        OcAce ace = new OcAce();
        ace.setSubject(subject);
        ace.setPermission(permission);
        ace.setResources(getResources(verticalResources));
        List<OcAce> aceList = new ArrayList<>();
        aceList.add(ace);

        OcAcl acl = new OcAcl();
        acl.setAceList(aceList);
        return provisionAcl(endpoint, acl);
    }

    public Completable deleteAcl(String endpoint, long aceId) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK) || code.equals(OCStatus.OC_STATUS_DELETED)) {
                    Timber.d("Delete ACE succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new IOException("Delete ACE error"));
                }
            };

            if (!OCMain.doDelete(OcfResourceUri.ACL2_URI, ep, OcfResourceUri.DELETE_ACE_QUERY + aceId, handler, OCQos.HIGH_QOS)) {
                String error = "Could not send DELETE request to /oic/sec/acl2 with aceid=" + aceId;
                Timber.e(error);
                emitter.onError(new Exception(error));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    private List<OcAceResource> getResources(List<String> verticalResources) {
        List<OcAceResource> resources = new ArrayList<>();
        for (String verticalResource : verticalResources) {
            OcAceResource res = new OcAceResource();
            res.setHref(verticalResource);
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
}
