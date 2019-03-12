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

package org.openconnectivity.otgc.common.data.repository;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProvisioningRepositoryTest {
    static {
        System.loadLibrary("ocstack-jni");
    }

    private PlatformRepository platformRepo;
    private ProvisioningRepository repo;

    @Before
    public void setUp() {
        platformRepo = new PlatformRepository(InstrumentationRegistry.getTargetContext());
        platformRepo.initialize("oic_svr_db_client.dat", "introspection.dat").blockingAwait();

        repo = new ProvisioningRepository(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() {
        platformRepo.close().blockingAwait();
    }

    @Test
    public void initialize_pdmDbFile() {
        repo.initialize("Pdm.db")
                .test()
                .assertComplete();

        repo.close()
                .test()
                .assertComplete();
    }
}
