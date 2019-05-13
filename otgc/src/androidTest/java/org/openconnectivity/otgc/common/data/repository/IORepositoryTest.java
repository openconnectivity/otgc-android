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

package org.openconnectivity.otgc.common.data.repository;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openconnectivity.otgc.data.repository.IORepository;

import java.io.FileNotFoundException;

@RunWith(AndroidJUnit4.class)
public class IORepositoryTest {
    private IORepository repo;

    @Before
    public void setUp() {
        repo = new IORepository(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void copyFromAssetToFiles_svrDbFile() {
        repo.copyFromAssetToFiles("oic_svr_db_client.dat")
                .test()
                .assertComplete();
    }

    @Test
    public void copyFromAssetToFiles_missingFileReturnsFileNotFoundException() {
        repo.copyFromAssetToFiles("dummy.file")
                .test()
                .assertError(FileNotFoundException.class);
    }

    @Test
    public void getBytesFromFile_rootCrtFile() {
        repo.getBytesFromFile("root.crt")
                .test()
                .assertComplete();
    }

    @Test
    public void getBytesFromFile_rootPrvFile() {
        repo.getBytesFromFile("root.prv")
                .test()
                .assertComplete();
    }

    @Test
    public void getBytesFromFile_missingFileReturnsFileNotFoundException() {
        repo.getBytesFromFile("dummy.file")
                .test()
                .assertError(FileNotFoundException.class);
    }
}
