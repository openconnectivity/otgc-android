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

package org.openconnectivity.otgc.common.data.persistence.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.openconnectivity.otgc.common.data.entity.DeviceEntity;

import io.reactivex.Maybe;

/**
 * Interface for database access for Device related operations.
 */
@Dao
public interface DeviceDao {

    /**
     * Get a device from the table using its UUID to filter.
     *
     * @param id device UUID.
     * @return the device from the table.
     */
    @Query("SELECT * FROM Devices WHERE deviceid = :id LIMIT 1")
    Maybe<DeviceEntity> findById(String id);

    /**
     * Insert a device in the database. If the device already exists, replace it.
     *
     * @param device the device to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceEntity device);

    /**
     * Update a device in the database.
     *
     * @param device the device to be updated.
     */
    @Update
    void update(DeviceEntity device);

    /**
     * Update the name of a device in the database.
     *
     * @param deviceId the ID of the device to be updated.
     * @param deviceName the name of the device to update.
     */
    @Query("UPDATE Devices SET name = :deviceName WHERE deviceid = :deviceId")
    void updateDeviceName(String deviceId, String deviceName);

    /**
     * Delete all devices.
     */
    @Query("DELETE FROM Devices")
    void deleteAll();
}
