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

import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.PEMKeyPair;
import org.spongycastle.openssl.PEMParser;
import org.spongycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class IORepository {

    private static final int BUFFER_SIZE = 1024;

    private final Context mContext;

    @Inject
    public IORepository(Context context) {
        this.mContext = context;
    }

    public Completable copyFromAssetToFiles(String fileName) {
        return Completable.create(emitter -> {
            int length;
            byte[] buffer = new byte[BUFFER_SIZE];
            try (InputStream inputStream =
                         mContext.getAssets().open(fileName);
                 OutputStream outputStream =
                         new FileOutputStream(mContext.getFilesDir().getPath() + File.separator + fileName)
            ) {
                File file = new File(mContext.getFilesDir().getPath());
                //check files directory exists
                if (!(file.exists() && file.isDirectory())) {
                    file.mkdirs();
                }
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (NullPointerException e) {
                Timber.e("Null pointer exception: %s", e.getMessage());
                emitter.onError(e);
            } catch (FileNotFoundException e) {
                Timber.e("File not found: %s", e.getMessage());
                emitter.onError(e);
            } catch (IOException e) {
                Timber.e("%s file copy failed", fileName);
                emitter.onError(e);
            }

            emitter.onComplete();
        });
    }

    public Single<PrivateKey> getAssetAsPrivateKey(String fileName) {
        return Single.create(emitter -> {
            try (PEMParser pemReader = new PEMParser(new InputStreamReader(mContext.getAssets().open(fileName)))) {
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                Object keyPair = pemReader.readObject();
                if (keyPair instanceof PrivateKeyInfo) {
                    PrivateKey pk = converter.getPrivateKey((PrivateKeyInfo) keyPair);
                    emitter.onSuccess(pk);
                } else {
                    PrivateKey pk = converter.getPrivateKey(((PEMKeyPair) keyPair).getPrivateKeyInfo());
                    emitter.onSuccess(pk);
                }
            } catch (IOException x) {
                // Shouldn't occur, since we're only reading from strings
                emitter.onError(x);
            }
        });
    }

    public Single<X509Certificate> getAssetAsX509Certificate(String fileName) {
        return Single.create(emitter -> {
            try (InputStream inputStream =
                         mContext.getAssets().open(fileName)) {
                Security.addProvider(new BouncyCastleProvider());
                CertificateFactory factory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
                X509Certificate caCert = (X509Certificate) factory.generateCertificate(inputStream);
                emitter.onSuccess(caCert);
            } catch (FileNotFoundException e) {
                Timber.e("File not found: %s", e.getMessage());
                emitter.onError(e);
            } catch (IOException e) {
                Timber.e("%s file storage failed", fileName);
                emitter.onError(e);
            }
        });
    }

    public Single<byte[]> getBytesFromFile(String path) {
        return Single.fromCallable(() -> {
            byte[] fileBytes;
            try (InputStream inputStream =
                         mContext.getAssets().open(path)) {
                fileBytes = new byte[inputStream.available()];
                inputStream.read(fileBytes);
            }

            return fileBytes;
        });
    }
}
