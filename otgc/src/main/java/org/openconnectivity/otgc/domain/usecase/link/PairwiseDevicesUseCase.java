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

package org.openconnectivity.otgc.domain.usecase.link;

import org.openconnectivity.otgc.data.repository.AmsRepository;
import org.openconnectivity.otgc.data.repository.CertRepository;
import org.openconnectivity.otgc.data.repository.CmsRepository;
import org.openconnectivity.otgc.data.repository.IORepository;
import org.openconnectivity.otgc.data.repository.PstatRepository;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.constant.OcfDosType;
import org.openconnectivity.otgc.utils.constant.OtgcConstant;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.pkcs.PKCS10CertificationRequest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import io.reactivex.Completable;

public class PairwiseDevicesUseCase {
    private final IotivityRepository iotivityRepository;
    private final PstatRepository pstatRepository;
    private final CmsRepository cmsRepository;
    private final CertRepository certRepository;
    private final IORepository ioRepository;
    private final AmsRepository amsRepository;

    @Inject
    public PairwiseDevicesUseCase(IotivityRepository iotivityRepository,
                                  PstatRepository pstatRepository,
                                  CmsRepository cmsRepository,
                                  CertRepository certRepository,
                                  IORepository ioRepository,
                                  AmsRepository amsRepository)
    {
        this.iotivityRepository = iotivityRepository;
        this.pstatRepository = pstatRepository;
        this.cmsRepository = cmsRepository;
        this.certRepository = certRepository;
        this.ioRepository = ioRepository;
        this.amsRepository = amsRepository;
    }

    public Completable execute(Device client, Device server)
    {
        /*SecureRandom secureRandom = new SecureRandom();
        byte[] symmetricKey = new byte[16];
        secureRandom.nextBytes(symmetricKey);
        SecretKey secretKey = new SecretKeySpec(symmetricKey, "AES");

        Completable clientPairwise = iotivityRepository.getSecureEndpoint(client)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, client.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(cmsRepository.createPskCredential(endpoint, client.getDeviceId(), server.getDeviceId(), secretKey.getEncoded()))
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, client.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP)));

        Completable serverPairwise = iotivityRepository.getSecureEndpoint(server)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, server.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(cmsRepository.createPskCredential(endpoint, server.getDeviceId(), client.getDeviceId(), secretKey.getEncoded()))
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, server.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP)));*/

        Completable clientPairwise = iotivityRepository.getSecureEndpoint(client)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, client.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(cmsRepository.retrieveCsr(endpoint, client.getDeviceId()))
                                .flatMapCompletable(csr -> {
                                    // Convert CSR
                                    PKCS10CertificationRequest certRequest = certRepository.getPKCS10CertRequest(csr).blockingGet();

                                    // Get Public Key from CSR
                                    SubjectPublicKeyInfo publicKeyInfo = certRequest.getSubjectPublicKeyInfo();
                                    PublicKey publicKey = certRepository.getPublicKeyFromBytes(publicKeyInfo.getPublicKeyData().getBytes()).blockingGet();
                                    // Get Private Key of Root CA
                                    PrivateKey caPrivateKey = ioRepository.getAssetAsPrivateKey(OtgcConstant.ROOT_PRIVATE_KEY).blockingGet();

                                    // Get Root CA
                                    X509Certificate rootCa = ioRepository.getAssetAsX509Certificate(OtgcConstant.ROOT_CERTIFICATE).blockingGet();
                                    String rootCert = certRepository.x509CertificateToPemString(rootCa).blockingGet();

                                    // Generate the identity certificate in PEM format
                                    //X509Certificate idCert = certRepository.generateIdentityCertificate("*", publicKey, caPrivateKey).blockingGet();
                                    X509Certificate idCert = certRepository.generateIdentityCertificate(client.getDeviceId(), publicKey, caPrivateKey).blockingGet();
                                    String identityCert = certRepository.x509CertificateToPemString(idCert).blockingGet();

                                    return cmsRepository.provisionIdentityCertificate(endpoint, client.getDeviceId(), rootCert, identityCert);
                                })
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, client.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP)));

        Completable serverPairwise = iotivityRepository.getSecureEndpoint(server)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, server.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(cmsRepository.retrieveCsr(endpoint, server.getDeviceId()))
                                .flatMapCompletable(csr -> {
                                    // Convert CSR
                                    PKCS10CertificationRequest certRequest = certRepository.getPKCS10CertRequest(csr).blockingGet();

                                    // Get Public Key from CSR
                                    SubjectPublicKeyInfo publicKeyInfo = certRequest.getSubjectPublicKeyInfo();
                                    PublicKey publicKey = certRepository.getPublicKeyFromBytes(publicKeyInfo.getPublicKeyData().getBytes()).blockingGet();
                                    // Get Private Key of Root CA
                                    PrivateKey caPrivateKey = ioRepository.getAssetAsPrivateKey(OtgcConstant.ROOT_PRIVATE_KEY).blockingGet();

                                    // Get Root CA
                                    X509Certificate rootCa = ioRepository.getAssetAsX509Certificate(OtgcConstant.ROOT_CERTIFICATE).blockingGet();
                                    String rootCert = certRepository.x509CertificateToPemString(rootCa).blockingGet();

                                    // Generate the certificate in PEM format
                                    //X509Certificate cert = certRepository.generateIdentityCertificate("*", publicKey, caPrivateKey).blockingGet();
                                    X509Certificate cert = certRepository.generateIdentityCertificate(server.getDeviceId(), publicKey, caPrivateKey).blockingGet();
                                    String identityCert = certRepository.x509CertificateToPemString(cert).blockingGet();

                                    return cmsRepository.provisionIdentityCertificate(endpoint, server.getDeviceId(), rootCert, identityCert);
                                })
                                .andThen(amsRepository.provisionConntypeAcl(endpoint, server.getDeviceId(), true, new ArrayList<>(Arrays.asList("*")), 31))
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, server.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP)));

        return clientPairwise
                .andThen(serverPairwise);
    }
}
