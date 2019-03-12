/*
 * *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  ******************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  ******************************************************************
 */

package org.openconnectivity.otgc.common.domain.usecase;

import androidx.annotation.VisibleForTesting;

import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openconnectivity.otgc.common.data.repository.IORepository;
import org.openconnectivity.otgc.common.data.repository.PlatformRepository;
import org.openconnectivity.otgc.common.data.repository.PreferencesRepository;
import org.openconnectivity.otgc.common.data.repository.ProvisioningRepository;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.sec.ECPrivateKey;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.BasicConstraints;
import org.spongycastle.asn1.x509.ExtendedKeyUsage;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.KeyPurposeId;
import org.spongycastle.asn1.x509.KeyUsage;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;
import timber.log.Timber;

public class InitializeIotivityUseCase {
    private static final String OIC_CLIENT_JSON_DB_FILE = "oic_svr_db_client.json";
    private static final String OIC_CLIENT_CBOR_DB_FILE = "oic_svr_db_client.dat";
    private static final String INTROSPECTION_CBOR_FILE = "introspection.dat";
    private static final String OIC_SQL_DB_FILE = "Pdm.db";

    private static final String CRT_FILE = "root.crt";
    private static final String PRIVATE_KEY_FILE = "root.prv";

    private final PreferencesRepository mPreferencesRepository;
    private final IORepository mIORepository;
    private final PlatformRepository mPlatformRepository;
    private final ProvisioningRepository mProvisioningRepository;

    @Inject
    InitializeIotivityUseCase(PreferencesRepository preferencesRepository,
                              IORepository ioRepository,
                              PlatformRepository platformRepository,
                              ProvisioningRepository provisioningRepository) {
        this.mPreferencesRepository = preferencesRepository;
        this.mIORepository = ioRepository;
        this.mPlatformRepository = platformRepository;
        this.mProvisioningRepository = provisioningRepository;
    }

    public Completable execute() {
        Completable iotivityCompletable =
                mPlatformRepository.initialize(OIC_CLIENT_CBOR_DB_FILE, INTROSPECTION_CBOR_FILE)
                    .andThen(mProvisioningRepository.initialize(OIC_SQL_DB_FILE));


        Completable completable;
        if (mPreferencesRepository.isFirstRun()) {
            // Copy CBOR file when application runs first time
            completable =
                    makeUniqueAndCopyOicSrvDbFile()
                    .andThen(mIORepository.copyFromAssetToFiles(INTROSPECTION_CBOR_FILE))
                    .andThen(Completable.fromAction(() -> mPreferencesRepository.setFirstRun(false)))
                    .andThen(iotivityCompletable)
                    .andThen(Completable.fromAction(() -> {
                        String piid = mPlatformRepository.getDevicePiid();
                        mPreferencesRepository.setPiid(piid);
                    }))
                    .andThen(mProvisioningRepository.doSelfOwnership())
                    .andThen(mProvisioningRepository.saveCertificates(
                            mIORepository.getBytesFromFile(CRT_FILE).blockingGet(),
                            mIORepository.getBytesFromFile(PRIVATE_KEY_FILE).blockingGet()
                    ));
        } else {
            completable = iotivityCompletable
                    .andThen(Completable.fromAction(() -> mPlatformRepository.setDevicePiid(mPreferencesRepository.getPiid())));
        }

        return completable;
    }

    private Completable makeUniqueAndCopyOicSrvDbFile() {
        return mIORepository.getAssetAsJSON(OIC_CLIENT_JSON_DB_FILE)
                .map(jsonObject -> {
                    String uuid = UUID.randomUUID().toString();
                    setDeviceUuid(jsonObject, uuid);
                    X509Certificate caCertificate = mIORepository.getAssetAsX509Certificate(CRT_FILE).blockingGet();
                    PrivateKey caPrivateKey = mIORepository.getAssetAsPrivateKey(PRIVATE_KEY_FILE).blockingGet();
                    ASN1Sequence pkSeqCa = (ASN1Sequence)ASN1Sequence.fromByteArray(caPrivateKey.getEncoded());
                    PrivateKeyInfo pkInfoCa = PrivateKeyInfo.getInstance(pkSeqCa);
                    ECPrivateKey devPrivateKeyCa = ECPrivateKey.getInstance(pkInfoCa.parsePrivateKey());
                    addTrustCa(jsonObject, uuid,bytesToHex(devPrivateKeyCa.getEncoded()), bytesToHex(caCertificate.getEncoded()));

                    // public/private key pair that we are creating certificate for
                    ECGenParameterSpec ecParamSpec = new ECGenParameterSpec("secp256r1");
                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
                    keyPairGenerator.initialize(ecParamSpec);
                    KeyPair keyPair = keyPairGenerator.generateKeyPair();

                    // Public Key
                    PublicKey devPublicKey = keyPair.getPublic();

                    // Private Key is initially formatted as PKCS#8 and IoTivity mbedtls implementation
                    // does not support this format, so a reformat to SEC1 is required
                    ASN1Sequence pkSeq = (ASN1Sequence) ASN1Sequence.fromByteArray(keyPair.getPrivate().getEncoded());
                    PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(pkSeq);
                    ECPrivateKey devPrivateKey = ECPrivateKey.getInstance(pkInfo.parsePrivateKey());

                    // Identity/Manufacturer certificate
                    X509Certificate certificate = generateCertificate(uuid,
                            devPublicKey,
                            caPrivateKey,
                            caCertificate);

                    addCert(jsonObject, uuid, bytesToHex(devPrivateKey.getEncoded()), bytesToHex(certificate.getEncoded()));
                    addMfgCert(jsonObject, uuid, bytesToHex(devPrivateKey.getEncoded()), bytesToHex(certificate.getEncoded()));

                    return jsonObject;
                }).map(jsonObject -> {
                    JSONArray credsArray = jsonObject.getJSONObject("cred").getJSONArray("creds");
                    CBORObject credsCborArray = CBORObject.NewArray();
                    for (int i = 0; i < credsArray.length(); i++) {
                        CBORObject credCbor = CBORObject.NewMap();
                        credCbor.Add("credid", credsArray.getJSONObject(i).getInt("credid"));
                        credCbor.Add("subjectuuid", credsArray.getJSONObject(i).getString("subjectuuid"));
                        credCbor.Add("credtype", credsArray.getJSONObject(i).getInt("credtype"));

                        CBORObject publicDataCbor = CBORObject.NewMap();
                        publicDataCbor.Add("encoding", credsArray.getJSONObject(i).getJSONObject("publicdata").getString("encoding"));
                        publicDataCbor.Add("data",
                                hexStringToByteArray(credsArray.getJSONObject(i).getJSONObject("publicdata").getString("data")));
                        credCbor.Add("publicdata", publicDataCbor);

                        try {
                            JSONObject privateDataJson = credsArray.getJSONObject(i).getJSONObject("privatedata");
                            CBORObject privateDataCbor = CBORObject.NewMap();
                            privateDataCbor.Add("encoding", privateDataJson.getString("encoding"));
                            privateDataCbor.Add("data",
                                    hexStringToByteArray(privateDataJson.getString("data")));
                            credCbor.Add("privatedata", privateDataCbor);
                        } catch (JSONException e) {
                            Timber.d("Credential has no privatedata");
                        }

                        credCbor.Add("credusage", credsArray.getJSONObject(i).getString("credusage"));

                        credsCborArray.Add(credCbor);
                    }

                    CBORObject credCbor = CBORObject.NewMap();
                    credCbor.Add("creds", credsCborArray);
                    credCbor.Add("rowneruuid", jsonObject.getJSONObject("cred").getString("rowneruuid"));

                    JSONArray rtArray = jsonObject.getJSONObject("cred").getJSONArray("rt");
                    CBORObject rtCborArray = CBORObject.NewArray();
                    for (int i = 0; i < rtArray.length(); i++) {
                        rtCborArray.Add(rtArray.getString(i));
                    }
                    credCbor.Add("rt", rtCborArray);

                    JSONArray ifArray = jsonObject.getJSONObject("cred").getJSONArray("if");
                    CBORObject ifCborArray = CBORObject.NewArray();
                    for (int i = 0; i < ifArray.length(); i++) {
                        ifCborArray.Add(ifArray.getString(i));
                    }
                    credCbor.Add("if", ifCborArray);

                    CBORObject aclCbor = CBORObject.FromJSONString(jsonObject.getJSONObject("acl").toString(), CBOREncodeOptions.Default);
                    CBORObject pstatCbor = CBORObject.FromJSONString(jsonObject.getJSONObject("pstat").toString(), CBOREncodeOptions.Default);
                    CBORObject doxmCbor = CBORObject.FromJSONString(jsonObject.getJSONObject("doxm").toString(), CBOREncodeOptions.Default);

                    CBORObject map = CBORObject.NewMap();
                    map.Add("acl", aclCbor.EncodeToBytes());
                    map.Add("pstat", pstatCbor.EncodeToBytes());
                    map.Add("doxm", doxmCbor.EncodeToBytes());
                    map.Add("cred", credCbor.EncodeToBytes());

                    return map;
                })
                .flatMapCompletable(cbor -> mIORepository.saveCborToFiles(OIC_CLIENT_CBOR_DB_FILE, cbor));
    }

    private void setDeviceUuid(JSONObject jsonObject, String deviceUuid) throws JSONException {
        jsonObject.getJSONObject("doxm").put("deviceuuid", deviceUuid);
    }

    private void addTrustCa(JSONObject jsonObject, String deviceUuid, String rawPrivateKey, String derCertificate) throws JSONException {
        addCertificate(jsonObject, deviceUuid, 2, derCertificate, rawPrivateKey, "oic.sec.cred.trustca");
    }

    private void addCert(JSONObject jsonObject, String deviceUuid, String rawPrivateKey, String derCertificate) throws JSONException {
        addCertificate(jsonObject, deviceUuid, 3, derCertificate, rawPrivateKey, "oic.sec.cred.cert");
    }

    private void addMfgCert(JSONObject jsonObject, String deviceUuid, String rawPrivateKey, String derCertificate) throws JSONException {
        addCertificate(jsonObject, deviceUuid, 4, derCertificate, rawPrivateKey, "oic.sec.cred.mfgcert");
    }

    private void addCertificate(JSONObject jsonObject,
                                String deviceUuid,
                                int credId,
                                String derCertificate,
                                String rawPrivateKey,
                                String credUsage) throws JSONException {
        JSONObject credObject = new JSONObject();
        credObject.put("credid", credId);
        credObject.put("subjectuuid", deviceUuid);
        credObject.put("credtype", 8);

        JSONObject publicDataObject = new JSONObject();
        publicDataObject.put("encoding", "oic.sec.encoding.der");
        publicDataObject.put("data", derCertificate);
        credObject.put("publicdata", publicDataObject);

        if (rawPrivateKey != null &&
                (credUsage.equals("oic.sec.cred.cert") || credUsage.equals("oic.sec.cred.mfgcert") || credUsage.equals("oic.sec.cred.trustca"))) {
            JSONObject privateDataObject = new JSONObject();
            privateDataObject.put("encoding", "oic.sec.encoding.raw");
            privateDataObject.put("data", rawPrivateKey);
            credObject.put("privatedata", privateDataObject);
        }

        credObject.put("credusage", credUsage);

        jsonObject.getJSONObject("cred").getJSONArray("creds").put(credObject);
    }

    @VisibleForTesting
    public X509Certificate generateCertificate(String deviceUuid, PublicKey publicKey, PrivateKey caPrivateKey, X509Certificate caCert) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        BigInteger serialNumber = new BigInteger(160, new SecureRandom());
        Date startDate = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24);
        Date expiryDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10));
        X500Name issuer = new X500Name("C=US, O=Open Connectivity Foundation, CN=Root CA");
        X500Name subject = new X500Name("CN=uuid:" + deviceUuid);

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer, serialNumber, startDate, expiryDate, subject, publicKey);

        certBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
        certBuilder.addExtension(Extension.keyUsage, true,
                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment |
                        KeyUsage.dataEncipherment | KeyUsage.keyAgreement));

        ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(KeyPurposeId.getInstance(new ASN1ObjectIdentifier("1.3.6.1.4.1.44924.1.6")));
        certBuilder.addExtension(Extension.extendedKeyUsage, false, extendedKeyUsage);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withECDSA")
                .setProvider(new BouncyCastleProvider()).build(caPrivateKey);
        byte[] certBytes = certBuilder.build(signer).getEncoded();
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
        return (X509Certificate) certificateFactory.generateCertificate(
                new ByteArrayInputStream(certBytes));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
