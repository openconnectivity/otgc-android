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

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class UserEntityTest {

    private final String id = "1110";
    private final String userName = "pepe";
    private final String pass = "1234";

    @Test
    public void getId() {

        UserEntity ue = new UserEntity( userName, pass);
        UserEntity ue2 = new UserEntity( id, userName, pass);

        assertNotNull(ue);

        assertNotNull(ue.getId());
        assertNotNull(ue2.getId());

        assertEquals(ue2.getId(), id);
    }

    @Test
    public void getUserName() {

        UserEntity ue = new UserEntity( userName, pass);
        UserEntity ue2 = new UserEntity( id, userName, pass);

        assertNotNull(ue.getUserName());
        assertNotNull(ue2.getUserName());

        assertEquals( ue.getUserName(), userName);
        assertEquals( ue2.getUserName(), userName);
    }

    @Test
    public void getPassword() {

        UserEntity ue = new UserEntity( userName, pass);
        UserEntity ue2 = new UserEntity( id, userName, pass);

        assertNotNull(ue.getPassword());
        assertNotNull(ue2.getPassword());

        assertEquals( ue.getPassword(), pass);
        assertEquals( ue2.getPassword(), pass);
    }
}