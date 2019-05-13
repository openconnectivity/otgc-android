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

import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAce;
import org.openconnectivity.otgc.domain.usecase.UpdateDeviceTypeUseCase;
import org.openconnectivity.otgc.domain.usecase.accesscontrol.DeleteAclUseCase;
import org.openconnectivity.otgc.domain.usecase.accesscontrol.RetrieveAclUseCase;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;
import org.openconnectivity.otgc.domain.model.devicelist.Device;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class AccessControlViewModel extends ViewModel {

    private final RetrieveAclUseCase mRetrieveAclUseCase;
    private final DeleteAclUseCase mDeleteAclUseCase;

    private final UpdateDeviceTypeUseCase mUpdateDeviceTypeUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<String> mRownerUuid = new MutableLiveData<>();
    private final MutableLiveData<OcAce> mAce = new MutableLiveData<>();
    private final MutableLiveData<Long> mDeletedAceId = new MutableLiveData<>();

    @Inject
    AccessControlViewModel(
            RetrieveAclUseCase retrieveAclUseCase,
            DeleteAclUseCase deleteAclUseCase,
            SchedulersFacade schedulersFacade,
            UpdateDeviceTypeUseCase updateDeviceTypeUseCase) {
        this.mRetrieveAclUseCase = retrieveAclUseCase;
        this.mDeleteAclUseCase = deleteAclUseCase;
        this.mUpdateDeviceTypeUseCase = updateDeviceTypeUseCase;

        this.mSchedulersFacade = schedulersFacade;
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    public LiveData<Boolean> isProcessing() {
        return mProcessing;
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public LiveData<String> getResourceOwner() {
        return mRownerUuid;
    }

    public LiveData<OcAce> getAce() {
        return mAce;
    }

    public LiveData<Long> getDeletedAceId() {
        return mDeletedAceId;
    }

    public void retrieveAcl(Device device) {
        mDisposables.add(mRetrieveAclUseCase.execute(device)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        acl -> {
                            for (OcAce ace : acl.getAceList()) {
                                mAce.setValue(ace);
                            }
                            // if i can see ACL, i have some permits, so i have to update the DeviceType to OWNED_BY_OTHER_WITH_PERMITS
                            if (!device.hasACLpermit()
                                    && (device.getDeviceType() == DeviceType.OWNED_BY_OTHER
                                    || device.getDeviceType() == DeviceType.OWNED_BY_OTHER_WITH_PERMITS)) {
                                mDisposables.add(mUpdateDeviceTypeUseCase.execute(device.getDeviceId(),
                                                                                    DeviceType.OWNED_BY_OTHER_WITH_PERMITS,
                                                                                    device.getPermits() | Device.ACL_PERMITS)
                                                .subscribeOn(mSchedulersFacade.io())
                                                .observeOn(mSchedulersFacade.ui())
                                                .subscribe(
                                                        () -> {},
                                                        throwable2 ->  mError.setValue(new ViewModelError(Error.DB_ACCESS, null))
                                                ));
                            }
                        },
                        throwable -> {
                            mError.setValue(new ViewModelError(Error.RETRIEVE, null));

                            if (device.hasACLpermit()) {
                                mDisposables.add(mUpdateDeviceTypeUseCase.execute(device.getDeviceId(),
                                        DeviceType.OWNED_BY_OTHER,
                                        device.getPermits() & ~Device.ACL_PERMITS)
                                        .subscribeOn(mSchedulersFacade.io())
                                        .observeOn(mSchedulersFacade.ui())
                                        .subscribe(
                                                () -> {},
                                                throwable2 ->  mError.setValue(new ViewModelError(Error.DB_ACCESS, null))
                                        ));
                            }
                        }
                ));
    }

    public void deleteAce(Device device, long aceId) {
        mDisposables.add(mDeleteAclUseCase.execute(device, aceId)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mDeletedAceId.setValue(aceId),
                        throwable -> mError.setValue(new ViewModelError(Error.DELETE, null))
                ));
    }

    public enum Error implements ViewModelErrorType {
        RETRIEVE,
        DB_ACCESS,
        DELETE
    }
}
