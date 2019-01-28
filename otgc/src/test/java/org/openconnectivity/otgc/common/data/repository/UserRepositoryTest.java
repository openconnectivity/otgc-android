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

import android.arch.persistence.room.EmptyResultSetException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openconnectivity.otgc.common.data.entity.UserEntity;
import org.openconnectivity.otgc.common.data.persistence.dao.UserDao;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserRepositoryTest {
    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserRepository repo;
    @Captor
    private ArgumentCaptor<UserEntity> mUserArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUser() {
        repo.getUser();
        verify(userDao).select();
    }

    @Test
    public void getUser_whenNoUserSaved() {
        EmptyResultSetException ex = new EmptyResultSetException("Test");
        when(userDao.select()).thenReturn(Single.error(ex));

        repo.getUser()
                .test()
                .assertError(ex);
    }

    @Test
    public void getUser_whenUserSaved() {
        // Given that the UserDAO returns a user
        UserEntity user = new UserEntity("testUser", "testPassword");
        when(userDao.select()).thenReturn(Single.just(user));

        repo.getUser()
                .test()
                // The correct user is emitter
                .assertValue(user);
    }

    @Test
    public void putOrPostUser_insertsUser() {
        // When inserting User
        repo.putOrPostUser("testUser", "testPassword")
                .test()
                .assertComplete();

        verify(userDao).insert(mUserArgumentCaptor.capture());
        assertEquals("testUser", mUserArgumentCaptor.getValue().getUserName());
        assertEquals("testPassword", mUserArgumentCaptor.getValue().getPassword());
    }

    @Test
    public void deleteUser_clearsTable() {
        repo.deleteUser()
                .test()
                .assertComplete();
    }
}