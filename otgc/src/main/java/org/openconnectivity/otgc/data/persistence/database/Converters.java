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

package org.openconnectivity.otgc.data.persistence.database;

import androidx.room.TypeConverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;

import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    private Converters() {
        throw new IllegalStateException("Utility class");
    }

    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeReference<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static String fromDeviceType(DeviceType type) {
        return type == null ? null : type.toString();
    }

    @TypeConverter
    public static DeviceType toDeviceType(String type) {
        return type == null ? DeviceType.UNOWNED : DeviceType.valueOf(type);
    }
}
