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

package org.openconnectivity.otgc.domain.usecase;

import org.iotivity.OCFactoryPresetsHandler;
import org.iotivity.OCObt;
import org.iotivity.OCPki;
import org.openconnectivity.otgc.data.repository.CertRepository;
import org.openconnectivity.otgc.data.repository.IORepository;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.PreferencesRepository;
import org.openconnectivity.otgc.utils.constant.OtgcConstant;
import org.openconnectivity.otgc.utils.constant.OtgcMode;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.sec.ECPrivateKey;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;

import javax.inject.Inject;

import io.reactivex.Completable;

public class InitializeIotivityUseCase {
    private final IotivityRepository iotivityRepository;
    private final CertRepository certRepository;
    private final IORepository ioRepository;
    private final PreferencesRepository settingRepository;

    @Inject
    public InitializeIotivityUseCase(IotivityRepository iotivityRepository,
                                     CertRepository certRepository,
                                     IORepository ioRepository,
                                     PreferencesRepository settingRepository) {
        this.iotivityRepository = iotivityRepository;
        this.certRepository = certRepository;
        this.ioRepository = ioRepository;
        this.settingRepository = settingRepository;
    }

    public Completable execute() {
        Completable initOic = iotivityRepository.initOICStack();

        Completable completable = iotivityRepository.setFactoryResetHandler(factoryReset);
        if (settingRepository.isFirstRun()) {
            completable = completable
                    .andThen(ioRepository.copyFromAssetToFiles(OtgcConstant.INTROSPECTION_CBOR_FILE))
                    .andThen(initOic)
                    .andThen(Completable.fromAction(() -> settingRepository.setFirstRun(false)))
                    .andThen(Completable.fromAction(() -> settingRepository.setMode(OtgcMode.OBT)));
        } else {
            completable = completable
                    .andThen(initOic);
        }

        return completable;

    }

    private OCFactoryPresetsHandler factoryReset = (device -> {
        try {
            factoryResetHandler(device);
        } catch (Exception e) {
            // TODO:
        }
    });
    private void factoryResetHandler(long device) throws Exception {
        String uuid = iotivityRepository.getDeviceId().blockingGet();

        X509Certificate caCertificate = ioRepository.getAssetAsX509Certificate(OtgcConstant.ROOT_CERTIFICATE).blockingGet();
        PrivateKey caPrivateKey = ioRepository.getAssetAsPrivateKey(OtgcConstant.ROOT_PRIVATE_KEY).blockingGet();

        // Store root CA as trusted anchor
        String strCACertificate = certRepository.x509CertificateToPemString(caCertificate).blockingGet();
        if (OCPki.addTrustAnchor(device, strCACertificate.getBytes()) == -1) {
            throw new Exception("Add trust anchor error");
        }
        if (OCPki.addMfgTrustAnchor(device, strCACertificate.getBytes()) == -1) {
            throw new Exception("Add manufacturer trust anchor error");
        }

        // public/private key pair that we are creating certificate for
        ECGenParameterSpec ecParamSpec = new ECGenParameterSpec("secp256r1");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        keyPairGenerator.initialize(ecParamSpec);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Public key
        PublicKey publicKey = keyPair.getPublic();
        // PrivateKey
        ASN1Sequence pkSeq = (ASN1Sequence)ASN1Sequence.fromByteArray(keyPair.getPrivate().getEncoded());
        PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(pkSeq);
        ECPrivateKey privateKey = ECPrivateKey.getInstance(pkInfo.parsePrivateKey());
        String strPrivateKey = certRepository.privateKeyToPemString(privateKey).blockingGet();

        X509Certificate identityCertificate = certRepository.generateIdentityCertificate(uuid, publicKey, caPrivateKey).blockingGet();
        String strIdentityCertificate = certRepository.x509CertificateToPemString(identityCertificate).blockingGet();
        if (OCPki.addMfgCert(device, strIdentityCertificate.getBytes(), strPrivateKey.getBytes()) == -1) {
            throw new Exception("Add identity certificate error");
        }

        OCObt.shutdown();
    }
}
