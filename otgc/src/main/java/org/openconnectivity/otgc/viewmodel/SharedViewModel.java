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
package org.openconnectivity.otgc.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import javax.inject.Inject;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDisconnected = new MutableLiveData<>();

    @Inject
    public SharedViewModel() {
        // Required empty public constructor
    }

    public void setLoading(@NonNull Boolean loading) {
        this.mLoading.setValue(loading);
    }

    public LiveData<Boolean> getLoading() {
        return mLoading;
    }

    public void setDisconnected(@NonNull Boolean disconnected) {
        this.mDisconnected.setValue(disconnected);
    }

    public LiveData<Boolean> getDisconnected() {
        return mDisconnected;
    }
}
