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

import android.content.Context;

import org.iotivity.OCFactoryPresetsHandler;
import org.iotivity.OCObt;
import org.iotivity.OCPki;
import org.openconnectivity.otgc.data.repository.IORepository;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.PreferencesRepository;
import org.openconnectivity.otgc.utils.constant.OtgcConstant;
import org.openconnectivity.otgc.utils.constant.OtgcMode;
import org.openconnectivity.otgc.utils.handler.DisplayNotValidCertificateHandler;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Completable;

public class InitializeIotivityUseCase {
    private final IotivityRepository iotivityRepository;
    private final IORepository ioRepository;
    private final PreferencesRepository settingRepository;

    private Context context;
    private DisplayNotValidCertificateHandler displayNotValidCertificateHandler;

    @Inject
    public InitializeIotivityUseCase(IotivityRepository iotivityRepository,
                                     IORepository ioRepository,
                                     PreferencesRepository settingRepository) {
        this.iotivityRepository = iotivityRepository;
        this.ioRepository = ioRepository;
        this.settingRepository = settingRepository;
    }

    public Completable execute(Context context, DisplayNotValidCertificateHandler displayNotValidCertificateHandler) {
        this.context = context;
        this.displayNotValidCertificateHandler = displayNotValidCertificateHandler;

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
        /* Current date */
        Date date = new Date();

        /* Kyrio end-entity cert */
        InputStream inputStream = context.getAssets().open(OtgcConstant.KYRIO_EE_CERTIFICATE);
        X509Certificate eeCert = ioRepository.getFileAsX509Certificate(inputStream).blockingGet();
        if (date.after(eeCert.getNotBefore()) && date.before(eeCert.getNotAfter())) {
            byte[] kyrioEeCertificate = ioRepository.getBytesFromFile(OtgcConstant.KYRIO_EE_CERTIFICATE).blockingGet();
            /* private key of Kyrio end-entity cert */
            byte[] kyrioEeKey = ioRepository.getBytesFromFile(OtgcConstant.KYRIO_EE_KEY).blockingGet();
            int credid = OCPki.addMfgCert(device, kyrioEeCertificate, kyrioEeKey);
            if (credid == -1) {
                throw new Exception("Add identity certificate error");
            }

            /* Kyrio intermediate cert */
            inputStream = context.getAssets().open(OtgcConstant.KYRIO_SUBCA_CERTIFICATE);
            X509Certificate subCaCert = ioRepository.getFileAsX509Certificate(inputStream).blockingGet();
            if (date.after(subCaCert.getNotBefore()) && date.before(subCaCert.getNotAfter())) {
                byte[] kyrioSubcaCertificate = ioRepository.getBytesFromFile(OtgcConstant.KYRIO_SUBCA_CERTIFICATE).blockingGet();
                if (OCPki.addMfgIntermediateCert(device, credid, kyrioSubcaCertificate) == -1) {
                    throw new Exception("Add intermediate certificate error");
                }
            } else {
                this.displayNotValidCertificateHandler.handler("Kyrio intermediate certificate is not valid");
            }
        } else {
            this.displayNotValidCertificateHandler.handler("Kyrio end entity certificate is not valid");
        }

        /* Kyrio root cert */
        inputStream = context.getAssets().open(OtgcConstant.KYRIO_ROOT_CERTIFICATE);
        X509Certificate caCert = ioRepository.getFileAsX509Certificate(inputStream).blockingGet();
        if (date.after(caCert.getNotBefore()) && date.before(caCert.getNotAfter())) {
            byte[] kyrioRootcaCertificate = ioRepository.getBytesFromFile(OtgcConstant.KYRIO_ROOT_CERTIFICATE).blockingGet();
            if (OCPki.addMfgTrustAnchor(device, kyrioRootcaCertificate) == -1) {
                throw new Exception("Add root certificate error");
            }
        } else {
            this.displayNotValidCertificateHandler.handler("Kyrio root certificate is not valid");
        }

        /* EonTi root cert */
        inputStream = context.getAssets().open(OtgcConstant.EONTI_ROOT_CERTIFICATE);
        caCert = ioRepository.getFileAsX509Certificate(inputStream).blockingGet();
        if (date.after(caCert.getNotBefore()) && date.before(caCert.getNotAfter())) {
            byte[] eontiRootcaCertificate = ioRepository.getBytesFromFile(OtgcConstant.EONTI_ROOT_CERTIFICATE).blockingGet();
            if (OCPki.addMfgTrustAnchor(device, eontiRootcaCertificate) == -1) {
                throw new Exception("Add root certificate error");
            }
        } else {
            this.displayNotValidCertificateHandler.handler("EonTi root certificate is not valid");
        }

        OCObt.shutdown();
    }
}
