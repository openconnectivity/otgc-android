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
package org.openconnectivity.otgc.client.presentation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import org.openconnectivity.otgc.client.domain.model.DynamicUiElement;
import org.openconnectivity.otgc.client.domain.model.SerializableResource;
import org.openconnectivity.otgc.common.domain.usecase.GetDeviceInfoUseCase;
import org.openconnectivity.otgc.client.domain.usecase.GetPlatformInfoUseCase;
import org.openconnectivity.otgc.client.domain.usecase.GetResourcesUseCase;
import org.openconnectivity.otgc.client.domain.usecase.IntrospectUseCase;
import org.openconnectivity.otgc.client.domain.usecase.UiFromSwaggerUseCase;
import org.openconnectivity.otgc.common.domain.model.OcDevice;
import org.openconnectivity.otgc.common.domain.model.OicPlatform;
import org.openconnectivity.otgc.common.domain.usecase.GetDeviceNameUseCase;
import org.openconnectivity.otgc.common.presentation.viewmodel.BaseViewModel;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;

import java.util.List;

import javax.inject.Inject;

public class GenericClientViewModel extends BaseViewModel {

    private final GetDeviceNameUseCase mGetDeviceNameUseCase;
    private final GetDeviceInfoUseCase mGetDeviceInfoUseCase;
    private final GetPlatformInfoUseCase mGetPlatformInfoUseCase;
    private final GetResourcesUseCase mGetResourcesUseCase;
    private final IntrospectUseCase mIntrospectUseCase;
    private final UiFromSwaggerUseCase mUiFromSwaggerUseCase;

    private final SchedulersFacade schedulersFacade;

    private final MutableLiveData<String> mDeviceName = new MutableLiveData<>();
    private final MutableLiveData<OcDevice> mDeviceInfo = new MutableLiveData<>();
    private final MutableLiveData<OicPlatform> mPlatformInfo = new MutableLiveData<>();
    private final MutableLiveData<List<SerializableResource>> mResources = new MutableLiveData<>();
    private final MutableLiveData<List<DynamicUiElement>> mIntrospection = new MutableLiveData<>();

    @Inject
    GenericClientViewModel(
            GetDeviceNameUseCase getDeviceNameUseCase,
            GetDeviceInfoUseCase getDeviceInfoUseCase,
            GetPlatformInfoUseCase getPlatformInfoUseCase,
            GetResourcesUseCase getResourcesUseCase,
            IntrospectUseCase introspectUseCase,
            UiFromSwaggerUseCase uiFromSwaggerUseCase,
            SchedulersFacade schedulersFacade) {
        this.mGetDeviceNameUseCase = getDeviceNameUseCase;
        this.mGetDeviceInfoUseCase = getDeviceInfoUseCase;
        this.mGetPlatformInfoUseCase = getPlatformInfoUseCase;
        this.mGetResourcesUseCase = getResourcesUseCase;
        this.mIntrospectUseCase = introspectUseCase;
        this.mUiFromSwaggerUseCase = uiFromSwaggerUseCase;
        this.schedulersFacade = schedulersFacade;
    }

    public LiveData<String> getDeviceName() {
        return mDeviceName;
    }

    public LiveData<OcDevice> getDeviceInfo() {
        return mDeviceInfo;
    }

    public LiveData<OicPlatform> getPlatformInfo() {
        return mPlatformInfo;
    }

    public LiveData<List<SerializableResource>> getResources() {
        return mResources;
    }

    public LiveData<List<DynamicUiElement>> getIntrospection() {
        return mIntrospection;
    }

    public void loadDeviceName(String deviceId) {
        mDisposables.add(mGetDeviceNameUseCase.execute(deviceId)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        mDeviceName::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.DEVICE_NAME, null)
                        )
                ));
    }

    public void loadDeviceInfo(String deviceId) {
        mDisposables.add(mGetDeviceInfoUseCase.execute(deviceId)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mDeviceInfo::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.DEVICE_INFO, null))
                ));
    }

    public void loadPlatformInfo(String deviceId) {
        mDisposables.add(mGetPlatformInfoUseCase.execute(deviceId)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mPlatformInfo::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.PLATFORM_INFO, null))
                ));
    }

    public void introspect(String deviceId) {
        mDisposables.add(mIntrospectUseCase.execute(deviceId)
                .flatMap(mUiFromSwaggerUseCase::execute)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mIntrospection::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.INTROSPECTION, null))
                ));
    }

    public void findResources(String deviceId) {
        mDisposables.add(mGetResourcesUseCase.execute(deviceId)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mResources::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.FIND_RESOURCES, null))
                ));
    }

    public enum Error implements ViewModelErrorType {
        DEVICE_NAME,
        DEVICE_INFO,
        PLATFORM_INFO,
        INTROSPECTION,
        FIND_RESOURCES
    }
}
