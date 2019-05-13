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
package org.openconnectivity.otgc.utils.di;

import android.app.Application;
import androidx.room.Room;
import android.content.Context;

import org.openconnectivity.otgc.data.persistence.dao.DeviceDao;
import org.openconnectivity.otgc.data.persistence.database.OtgcDb;
import org.openconnectivity.otgc.data.persistence.dao.UserDao;
import org.openconnectivity.otgc.data.repository.AndroidPreferencesRepository;
import org.openconnectivity.otgc.data.repository.PreferencesRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    OtgcDb provideDb(Application application) {
        return Room.databaseBuilder(application, OtgcDb.class, "otgc.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    UserDao provideUserDao(OtgcDb db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    DeviceDao provideDeviceDao(OtgcDb db) {
        return db.deviceDao();
    }

    @Provides
    PreferencesRepository providePreferencesRepository(AndroidPreferencesRepository repo) {
        return repo;
    }
}
