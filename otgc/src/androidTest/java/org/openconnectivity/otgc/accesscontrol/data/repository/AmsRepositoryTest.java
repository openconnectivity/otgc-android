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

package org.openconnectivity.otgc.accesscontrol.data.repository;

import androidx.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AmsRepositoryTest {
    static {
        System.loadLibrary("ocstack-jni");
    }

    /*private PlatformRepository platformRepository;
    private ProvisioningRepository provisioningRepository;
    private IotivityRepository iotivityRepository;
    private AmsRepository amsRepository;

    private OcSecureResource ocSecureResource;

    OicSecAce ace;*/

    @Before
    public void setUp() {
        /*platformRepository = new PlatformRepository(InstrumentationRegistry.getTargetContext());
        platformRepository.initialize("oic_srv_db_client.dat", "introspection.dat").blockingAwait();

        provisioningRepository = new ProvisioningRepository(InstrumentationRegistry.getTargetContext());
        provisioningRepository.initialize("Pdm.db").blockingAwait();

        iotivityRepository = new IotivityRepository(InstrumentationRegistry.getTargetContext(), null);

        amsRepository = new AmsRepository();*/
    }

    @After
    public void tearDown() {
        /*provisioningRepository.close().blockingAwait();
        platformRepository.close().blockingAwait();*/
    }

    @Ignore
    @Test
    public void provisionAcl_aceWithInvalidSubjectUuidReturnsError() {
        /*ocSecureResource = iotivityRepository
                .findOcSecureResource("11111111-2222-3333-4444-555555555555")
                .blockingGet();
        OicSecAceSubject subject = new OicSecAceSubject(AceSubjectType.SUBJECT_UUID.getValue(), "", null, null);
        ace = new OicSecAce(0, subject, 31, null, null);

        amsRepository.provisionAcl(ocSecureResource, ace)
                .test()
                .assertError(OcException.class);*/
    }
}
