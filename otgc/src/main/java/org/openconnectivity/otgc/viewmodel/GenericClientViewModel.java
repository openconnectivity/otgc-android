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

import org.openconnectivity.otgc.domain.model.client.DynamicUiElement;
import org.openconnectivity.otgc.domain.model.client.SerializableResource;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;
import org.openconnectivity.otgc.domain.model.resource.virtual.p.OcPlatformInfo;
import org.openconnectivity.otgc.domain.usecase.GetDeviceInfoUseCase;
import org.openconnectivity.otgc.domain.usecase.client.GetPlatformInfoUseCase;
import org.openconnectivity.otgc.domain.usecase.GetResourcesUseCase;
import org.openconnectivity.otgc.domain.usecase.client.IntrospectUseCase;
import org.openconnectivity.otgc.domain.usecase.client.UiFromSwaggerUseCase;
import org.openconnectivity.otgc.domain.usecase.GetDeviceNameUseCase;
import org.openconnectivity.otgc.utils.viewmodel.BaseViewModel;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;

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
    private final MutableLiveData<OcDeviceInfo> mDeviceInfo = new MutableLiveData<>();
    private final MutableLiveData<OcPlatformInfo> mPlatformInfo = new MutableLiveData<>();
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

    public LiveData<OcDeviceInfo> getDeviceInfo() {
        return mDeviceInfo;
    }

    public LiveData<OcPlatformInfo> getPlatformInfo() {
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

    public void loadDeviceInfo(Device device) {
        mDisposables.add(mGetDeviceInfoUseCase.execute(device)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mDeviceInfo::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.DEVICE_INFO, null))
                ));
    }

    public void loadPlatformInfo(Device device) {
        mDisposables.add(mGetPlatformInfoUseCase.execute(device)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mPlatformInfo::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.PLATFORM_INFO, null))
                ));
    }

    public void introspect(Device device) {
        mDisposables.add(mIntrospectUseCase.execute(device)
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

    public void findResources(Device device) {
        mDisposables.add(mGetResourcesUseCase.execute(device)
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
