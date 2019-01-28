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

package org.openconnectivity.otgc.accesscontrol.presentation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.iotivity.base.OicSecAce;
import org.openconnectivity.otgc.accesscontrol.domain.usecase.DeleteAclUseCase;
import org.openconnectivity.otgc.accesscontrol.domain.usecase.RetrieveAclUseCase;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class AccessControlViewModel extends ViewModel {

    private final RetrieveAclUseCase mRetrieveAclUseCase;
    private final DeleteAclUseCase mDeleteAclUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<String> mRownerUuid = new MutableLiveData<>();
    private final MutableLiveData<OicSecAce> mAce = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDeletedAceId = new MutableLiveData<>();

    @Inject
    AccessControlViewModel(
            RetrieveAclUseCase retrieveAclUseCase,
            DeleteAclUseCase deleteAclUseCase,
            SchedulersFacade schedulersFacade) {
        this.mRetrieveAclUseCase = retrieveAclUseCase;
        this.mDeleteAclUseCase = deleteAclUseCase;

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

    public LiveData<OicSecAce> getAce() {
        return mAce;
    }

    public LiveData<Integer> getDeletedAceId() {
        return mDeletedAceId;
    }

    public void retrieveAcl(String deviceId) {
        mDisposables.add(mRetrieveAclUseCase.execute(deviceId)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        acl -> {
                            mRownerUuid.setValue(acl.getRownerID());
                            for (OicSecAce ace : acl.getOicSecAcesList()) {
                                mAce.setValue(ace);
                            }
                        },
                        throwable -> mError.setValue(new ViewModelError(Error.RETRIEVE, null))
                ));
    }

    public void deleteAce(String deviceId, int aceId) {
        mDisposables.add(mDeleteAclUseCase.execute(deviceId, aceId)
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
        DELETE
    }
}
