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

package org.openconnectivity.otgc.credential.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.iotivity.base.OicSecCred;
import org.openconnectivity.otgc.common.presentation.viewmodel.BaseViewModel;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.credential.domain.usecase.DeleteCredentialUseCase;
import org.openconnectivity.otgc.credential.domain.usecase.RetrieveCredentialsUseCase;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;

import javax.inject.Inject;

public class CredentialsViewModel extends BaseViewModel {

    private final RetrieveCredentialsUseCase mRetrieveCredentialsUseCase;
    private final DeleteCredentialUseCase mDeleteCredentialUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final MutableLiveData<String> mRownerUuid = new MutableLiveData<>();
    private final MutableLiveData<OicSecCred> mCredential = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDeletedCredId = new MutableLiveData<>();

    @Inject
    CredentialsViewModel(
            RetrieveCredentialsUseCase retrieveCredentialsUseCase,
            DeleteCredentialUseCase deleteCredentialUseCase,
            SchedulersFacade schedulersFacade) {
        this.mRetrieveCredentialsUseCase = retrieveCredentialsUseCase;
        this.mDeleteCredentialUseCase = deleteCredentialUseCase;

        this.mSchedulersFacade = schedulersFacade;
    }

    public LiveData<String> getResourceOwner() {
        return mRownerUuid;
    }

    public LiveData<OicSecCred> getCredential() {
        return mCredential;
    }

    public LiveData<Integer> getDeletedCredId() {
        return mDeletedCredId;
    }

    public void retrieveCredentials(String deviceId) {
        mDisposables.add(mRetrieveCredentialsUseCase.execute(deviceId)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        credentials -> {
                            mRownerUuid.setValue(credentials.getRownerID());
                            for (OicSecCred cred : credentials.getOicSecCredsList()) {
                                mCredential.setValue(cred);
                            }
                        },
                        throwable -> mError.setValue(new ViewModelError(Error.RETRIEVE_CREDS, null))
                ));
    }

    public void deleteCredential(String deviceId, int credId) {
        mDisposables.add(mDeleteCredentialUseCase.execute(deviceId, credId)
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
        DELETE
    }
}
