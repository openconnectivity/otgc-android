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

package org.openconnectivity.otgc.common.domain.usecase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openconnectivity.otgc.common.data.repository.WlanRepository;
import org.openconnectivity.otgc.common.domain.model.NetworkDisconnectedException;

import io.reactivex.Single;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CheckConnectionUseCaseTest {
    @Mock
    private WlanRepository wlanRepository;
    @InjectMocks
    private CheckConnectionUseCase useCase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void execute_connectionReturnsTrue() {
        when(wlanRepository.isConnectedToWiFi()).thenReturn(Single.just(true));

        useCase.execute()
                .test()
                .assertValue(true);
    }

    @Test
    public void execute_noConnectionReturnsFalse() {
        when(wlanRepository.isConnectedToWiFi()).thenReturn(Single.just(false));

        useCase.execute()
                .test()
                .assertValue(false);
    }

    @Test
    public void executeCompletable_connectionReturnsComplete() {
        when(wlanRepository.isConnectedToWiFi()).thenReturn(Single.just(true));

        useCase.execute()
                .test()
                .assertComplete();
    }

    @Test
    public void executeCompletable_noConnectionThrowsException() {
        when(wlanRepository.isConnectedToWiFi()).thenReturn(Single.just(false));

        useCase.executeCompletable()
                .test()
                .assertError(NetworkDisconnectedException.class);
    }
}
