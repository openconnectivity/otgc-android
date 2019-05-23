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

package org.openconnectivity.otgc.common.data.persistence.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openconnectivity.otgc.data.entity.DeviceEntity;
import org.openconnectivity.otgc.data.persistence.database.OtgcDb;
import org.openconnectivity.otgc.data.persistence.dao.DeviceDao;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import io.reactivex.Single;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DeviceDaoTest {

    private OtgcDb db;
    private DeviceDao dao;

    private final List<String> hostsList = Arrays.asList("coap://192.168.11.25:1234", "coaps://4091:aa06:b790:49a6:2629:6adb:7813:f840:1234");

    private final String testUUID = "ff7df72e-4fa4-11e9-8647-d663bd873d93";
    private final String testUUID2 = "ff7df72e-4fa4-11e9-8647-d663bd873d93";

    @Before
    public void setUp(){
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(), OtgcDb.class).build();
        dao = db.deviceDao();
    }

    @Test
    public void findByIdTest() {

        final DeviceEntity device = new DeviceEntity(testUUID, "Test Device", hostsList, null, 0);
        final DeviceEntity device2 = new DeviceEntity(testUUID, "Test Device 2", hostsList, null, 0);

        // Trying to find where db is empty or the device hasn't been inserted
        DeviceEntity entity = dao.findById("12345678-1234-1234-1234-123456789012").blockingGet();
        assertNull(entity);

        // INSERT without conflict
        dao.insert(device);
        assertDevice(dao, device);

        // INSERT with conflict
        dao.insert(device2);
        assertDevice(dao, device2);
    }

    @Test
    public void findByIdTestRx() {

        dao.findById("12345678-1234-1234-1234-123456789012")
                .toSingle()
                .onErrorResumeNext(Single::error)
                .map(Object::toString)
                .test()
                .assertError(NoSuchElementException.class);
    }

    @Test
    public void updateTest() {

        final List<String> hostsList2 = Arrays.asList("coap://192.168.11.25:4321", "coaps://4091:aa06:b790:49a6:2629:6adb:7813:f840:1234");

        final DeviceEntity device = new DeviceEntity(testUUID2, "Test Device 3", hostsList, null, 0);
        final DeviceEntity device2 = new DeviceEntity(testUUID, "Test Device", hostsList, null, 0);
        final DeviceEntity device3 = new DeviceEntity(testUUID, "Test Device 4", hostsList2, null, 0);

        // not UPDATE
        dao.update(device);
        DeviceEntity entity = dao.findById(device.getId()).blockingGet();
        assertNull(entity);

        // UPDATE
        dao.insert(device2);
        dao.update(device3);
        assertDevice(dao, device3);

    }

    @Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    public void deleteTest() {

        final DeviceEntity device = new DeviceEntity(testUUID2, "Test Device 3", hostsList, null, 0);
        final DeviceEntity device2 = new DeviceEntity(testUUID, "Test Device", hostsList, null, 0);

        DeviceEntity entity = device, entity2 = device2;

        // DELETE ALL
        dao.insert(device);
        dao.insert(device2);

        dao.deleteAll();

        entity = dao.findById(device.getId()).blockingGet();
        entity2 = dao.findById(device2.getId()).blockingGet();

        assertNull(entity);
        assertNull(entity2);
    }

    /**
     * When test finishes, the database must be closed
     */
    @After
    public void tearDown() {
        db.close();
    }

    /**
     * Check if a Device was correctly added to Data Base
     * @param dao Data Base DAO
     * @param device The device that was inserted
     */
    private void assertDevice(DeviceDao dao, DeviceEntity device) {
        DeviceEntity de = dao.findById(device.getId()).blockingGet();

        assertNotNull(de);
        assertTrue(areIdentical(device, de));

    }

    /**
     * Compare two DeviceEntity
     * @param one
     * @param two
     * @return true if are the same
     */
    private boolean areIdentical(DeviceEntity one, DeviceEntity two) {
        return (one.getId().equals(two.getId()))
                && (one.getName().equals(two.getName()))
                && (one.getHosts().equals(two.getHosts()));
    }
}