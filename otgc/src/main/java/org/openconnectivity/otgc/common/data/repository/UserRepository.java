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

import org.openconnectivity.otgc.common.data.entity.UserEntity;
import org.openconnectivity.otgc.common.data.persistence.dao.UserDao;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

@Singleton
public class UserRepository {
    private final UserDao userDao;

    @Inject
    UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public Single<UserEntity> getUser() {
        return userDao.select();
    }

    public Completable putOrPostUser(String userName, String password) {
        return Completable.fromAction(() -> userDao.insert(new UserEntity(userName, password)));
    }

    public Completable deleteUser() {
        return Completable.fromAction(userDao::deleteAll);
    }
}
