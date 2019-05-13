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

package org.openconnectivity.otgc.domain.usecase.credential;

import org.openconnectivity.otgc.data.repository.CertRepository;
import org.openconnectivity.otgc.data.repository.IORepository;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.CmsRepository;
import org.openconnectivity.otgc.data.repository.PstatRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.constant.OcfDosType;
import org.openconnectivity.otgc.utils.constant.OtgcConstant;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.pkcs.PKCS10CertificationRequest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.inject.Inject;

import io.reactivex.Completable;

public class ProvisionIdentityCertificateUseCase {
    private final IotivityRepository iotivityRepository;
    private final CmsRepository cmsRepository;
    private final PstatRepository pstatRepository;
    private final CertRepository certRepository;
    private final IORepository ioRepository;

    @Inject
    public ProvisionIdentityCertificateUseCase(IotivityRepository iotivityRepository,
                                               PstatRepository pstatRepository,
                                               CmsRepository cmsRepository,
                                               CertRepository certRepository,
                                               IORepository ioRepository) {
        this.iotivityRepository = iotivityRepository;
        this.pstatRepository = pstatRepository;
        this.cmsRepository = cmsRepository;
        this.certRepository = certRepository;
        this.ioRepository = ioRepository;
    }

    /**
     * Use case steps:
     *  1.  POST    /oic/sec/pstat  -> dos.s=2 (RFPRO)
     *  2.  GET     /oic/sec/csr
     *  3.  POST    /oic/sec/cred   -> identity certificate
     *  4.  POST    /oic/sec/pstat  -> dos.s=3 (RFNOP)
     */

    public Completable execute(Device device) {
        return iotivityRepository.getSecureEndpoint(device)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(cmsRepository.retrieveCsr(endpoint))
                                .flatMapCompletable(csr -> {
                                    // Convert CSR
                                    PKCS10CertificationRequest certRequest = certRepository.getPKCS10CertRequest(csr).blockingGet();

                                    // Get Public Key from CSR
                                    SubjectPublicKeyInfo publicKeyInfo = certRequest.getSubjectPublicKeyInfo();
                                    PublicKey publicKey = certRepository.getPublicKeyFromBytes(publicKeyInfo.getPublicKeyData().getBytes()).blockingGet();
                                    // Get Private Key of Root CA
                                    PrivateKey caPrivateKey = ioRepository.getAssetAsPrivateKey(OtgcConstant.ROOT_PRIVATE_KEY).blockingGet();

                                    // Generate the certificate in PEM format
                                    X509Certificate cert = certRepository.generateIdentityCertificate(device.getDeviceId(), publicKey, caPrivateKey).blockingGet();
                                    String identityCert = certRepository.x509CertificateToPemString(cert).blockingGet();

                                    return cmsRepository.provisionIdentityCertificate(endpoint, device.getDeviceId(), identityCert);
                                })
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, OcfDosType.OC_DOSTYPE_RFNOP)));
    }
}
