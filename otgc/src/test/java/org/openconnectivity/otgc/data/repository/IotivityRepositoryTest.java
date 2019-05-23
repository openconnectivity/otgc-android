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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openconnectivity.otgc.data.persistence.dao.DeviceDao;

import static org.junit.Assert.*;

@Ignore
@RunWith(JUnit4.class)
public class IotivityRepositoryTest {

    @Mock
    private DeviceDao deviceDao;
    private Context ctx;

    @InjectMocks
    private IotivityRepository iotivityRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setRandomPinCallbackListener() {
    }

    @Test
    public void setDisplayPinListener() {
    }

    @Test
    public void scanUnownedDevices() {
    }

    @Test
    public void scanOwnedDevices() {
    }

    @Test
    public void scanHosts() {
    }

    @Test
    public void setPreferredCiphersuite() {
    }

    @Test
    public void findOcSecureResource() {
    }

    @Test
    public void findDeviceInUnicast() {
    }

    @Test
    public void findOcSecureResource1() {
    }

    @Test
    public void getDeviceCoapIpv6Host() {
    }

    @Test
    public void getDeviceCoapsIpv6Host() {
    }

    @Test
    public void getDeviceInfo() {
    }

    @Test
    public void getPlatformInfo() {
    }

    @Test
    public void findResources() {
    }

    @Test
    public void findResource() {
    }

    @Test
    public void get() {
    }

    @Test
    public void post() {
    }

    @Test
    public void put() {
    }

    @Test
    public void constructResource() {
    }

    @Test
    public void getDeviceTypes() {
    }

    @Test
    public void getResourceTypes() {
    }

    @Test
    public void getDeviceName() {
    }

    @Test
    public void setDeviceName() {
    }

    @Test
    public void getDiscoveryTimeout() {
    }
}