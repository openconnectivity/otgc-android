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

import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.domain.usecase.UpdateDeviceTypeUseCase;
import org.openconnectivity.otgc.utils.viewmodel.BaseViewModel;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.domain.usecase.credential.DeleteCredentialUseCase;
import org.openconnectivity.otgc.domain.usecase.credential.RetrieveCredentialsUseCase;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;
import org.openconnectivity.otgc.domain.model.devicelist.Device;

import javax.inject.Inject;

public class CredentialsViewModel extends BaseViewModel {

    private final RetrieveCredentialsUseCase mRetrieveCredentialsUseCase;
    private final DeleteCredentialUseCase mDeleteCredentialUseCase;
    private final UpdateDeviceTypeUseCase mUpdateDeviceTypeUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final MutableLiveData<String> mRownerUuid = new MutableLiveData<>();
    private final MutableLiveData<OcCredential> mCredential = new MutableLiveData<>();
    private final MutableLiveData<Long> mDeletedCredId = new MutableLiveData<>();

    @Inject
    CredentialsViewModel(
            RetrieveCredentialsUseCase retrieveCredentialsUseCase,
            DeleteCredentialUseCase deleteCredentialUseCase,
            UpdateDeviceTypeUseCase updateDeviceTypeUseCase,
            SchedulersFacade schedulersFacade) {
        this.mRetrieveCredentialsUseCase = retrieveCredentialsUseCase;
        this.mDeleteCredentialUseCase = deleteCredentialUseCase;
        this.mUpdateDeviceTypeUseCase = updateDeviceTypeUseCase;

        this.mSchedulersFacade = schedulersFacade;
    }

    public LiveData<String> getResourceOwner() {
        return mRownerUuid;
    }

    public LiveData<OcCredential> getCredential() {
        return mCredential;
    }

    public LiveData<Long> getDeletedCredId() {
        return mDeletedCredId;
    }

    public void retrieveCredentials(Device device) {
        if (device.getDeviceType() != DeviceType.CLOUD) {
            mDisposables.add(mRetrieveCredentialsUseCase.execute(device)
                    .subscribeOn(mSchedulersFacade.io())
                    .observeOn(mSchedulersFacade.ui())
                    .doOnSubscribe(__ -> mProcessing.setValue(true))
                    .doFinally(() -> mProcessing.setValue(false))
                    .subscribe(
                            credentials -> {
                                for (OcCredential cred : credentials.getCredList()) {
                                    mCredential.setValue(cred);
                                }

                                if (!device.hasCREDpermit()
                                        && (device.getDeviceType() == DeviceType.OWNED_BY_OTHER
                                        || device.getDeviceType() == DeviceType.OWNED_BY_OTHER_WITH_PERMITS)) {
                                    mDisposables.add(mUpdateDeviceTypeUseCase.execute(device.getDeviceId(),
                                            DeviceType.OWNED_BY_OTHER_WITH_PERMITS,
                                            device.getPermits() | Device.CRED_PERMITS)
                                            .subscribeOn(mSchedulersFacade.io())
                                            .observeOn(mSchedulersFacade.ui())
                                            .subscribe(
                                                    () -> {
                                                    },
                                                    throwable -> mError.setValue(new ViewModelError(Error.DB_ERROR, null))
                                            ));
                                }
                            },
                            throwable -> {
                                mError.setValue(new ViewModelError(Error.RETRIEVE_CREDS, null));
                                if (device.hasCREDpermit()) {
                                    mDisposables.add(mUpdateDeviceTypeUseCase.execute(device.getDeviceId(),
                                            DeviceType.OWNED_BY_OTHER,
                                            device.getPermits() & ~Device.CRED_PERMITS)
                                            .subscribeOn(mSchedulersFacade.io())
                                            .observeOn(mSchedulersFacade.ui())
                                            .subscribe(
                                                    () -> {
                                                    },
                                                    throwable2 -> mError.setValue(new ViewModelError(Error.DB_ERROR, null))
                                            ));
                                }
                            }
                    ));
        }
    }

    public void deleteCredential(Device device, long credId) {
        mDisposables.add(mDeleteCredentialUseCase.execute(device, credId)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mDeletedCredId.setValue(credId),
                        throwable -> mError.setValue(new ViewModelError(Error.DELETE, null))
                ));
    }

    public enum Error implements ViewModelErrorType {
        RETRIEVE_CREDS,
        DB_ERROR,
        DELETE
    }
}
