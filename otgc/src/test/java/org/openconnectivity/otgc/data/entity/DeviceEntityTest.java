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

package org.openconnectivity.otgc.data.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class DeviceEntityTest {

    /*private final String testUUID = "ff7df72e-4fa4-11e9-8647-d663bd873d93";
    private final String testName = "Test Device";

    @Test
    public void getId() {
        final DeviceEntity device = new DeviceEntity(testUUID, null, null, null, 0);

        assertNotNull(device);
        assertNotNull(device.getId());
        assertEquals(testUUID, device.getId());
    }

    @Test
    public void getName() {
        final DeviceEntity device = new DeviceEntity(testUUID, testName, null, null, 0);

        assertNotNull(device.getName());
        assertEquals(testName, device.getName());
    }

    @Test
    public void getHosts() {
        final List<String> hostsList = Arrays.asList("coap://192.168.11.25:1234", "coaps://4091:aa06:b790:49a6:2629:6adb:7813:f840:1234");
        final DeviceEntity device = new DeviceEntity(testUUID, testName, hostsList, null, 0);

        assertNotNull(device.getHosts());
        assertEquals(hostsList, device.getHosts());
    }*/
}