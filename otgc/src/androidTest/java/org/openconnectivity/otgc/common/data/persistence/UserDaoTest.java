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

package org.openconnectivity.otgc.common.data.persistence;

import androidx.room.EmptyResultSetException;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openconnectivity.otgc.data.entity.UserEntity;
import org.openconnectivity.otgc.data.persistence.dao.UserDao;
import org.openconnectivity.otgc.data.persistence.database.OtgcDb;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class UserDaoTest {
    private OtgcDb db;
    private UserDao dao;

    @Before
    public void setUp() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(), OtgcDb.class).build();
        dao = db.userDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void deleteUsersTest() {
        dao.select().test()
                .assertError(EmptyResultSetException.class);

        final UserEntity user = new UserEntity("FooUser", "FooPass");

        assertNotNull(user.getId());
        assertNotEquals(0, user.getId().length());
        dao.insert(user);

        assertUser(dao, user);

        dao.deleteAll();
        dao.select().test()
                .assertError(EmptyResultSetException.class);
    }

    private void assertUser(UserDao dao, UserEntity user) {
        UserEntity u = dao.select().blockingGet();

        assertNotNull(u);
        assertTrue(areIdentical(user, u));
    }

    private boolean areIdentical(UserEntity one, UserEntity two) {
        return (one.getId().equals(two.getId()) &&
                one.getUserName().equals(two.getUserName()));
    }
}