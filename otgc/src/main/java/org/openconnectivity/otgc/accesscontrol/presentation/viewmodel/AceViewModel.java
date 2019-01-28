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

import org.openconnectivity.otgc.accesscontrol.domain.usecase.CreateAclUseCase;
import org.openconnectivity.otgc.accesscontrol.domain.usecase.RetrieveVerticalResourcesUseCase;
import org.openconnectivity.otgc.accesscontrol.domain.usecase.UpdateAclUseCase;
import org.openconnectivity.otgc.client.domain.model.SerializableResource;
import org.openconnectivity.otgc.client.domain.usecase.GetResourcesUseCase;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class AceViewModel extends ViewModel {

    private final CreateAclUseCase mCreateAclUseCase;
    private final UpdateAclUseCase mUpdateAclUseCase;
    private final RetrieveVerticalResourcesUseCase mRetrieveVerticalResourcesUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<List<String>> mResources = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mSuccess = new MutableLiveData<>();

    @Inject
    AceViewModel(
            CreateAclUseCase createAclUseCase,
            UpdateAclUseCase updateAclUseCase,
            RetrieveVerticalResourcesUseCase retrieveVerticalResourcesUseCase,
            SchedulersFacade schedulersFacade) {
        this.mCreateAclUseCase = createAclUseCase;
        this.mUpdateAclUseCase = updateAclUseCase;
        this.mRetrieveVerticalResourcesUseCase = retrieveVerticalResourcesUseCase;

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

    public LiveData<List<String>> getResources() {
        return mResources;
    }

    public LiveData<Boolean> getSuccess() {
        return mSuccess;
    }

    public void retrieveResources(String targetDeviceId) {
        mDisposables.add(mRetrieveVerticalResourcesUseCase.execute(targetDeviceId)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        mResources::setValue,
                        throwable -> {
                            mError.setValue(new ViewModelError(Error.RETRIEVE_RESOURCES, null));
                        }
                ));
    }

    public void createAce(String targetDeviceId, String subjectId, int permission, List<String> resources) {
        mDisposables.add(mCreateAclUseCase.execute(targetDeviceId, subjectId, permission, resources)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> {
                            mSuccess.setValue(false);
                            mError.setValue(new ViewModelError(Error.CREATE, null));
                        }
                ));
    }

    public void createAce(String targetDeviceId, String roleId, String roleAuthority, int permission, List<String> resources) {
        mDisposables.add(mCreateAclUseCase.execute(targetDeviceId, roleId, roleAuthority, permission, resources)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> {
                            mSuccess.setValue(false);
                            mError.setValue(new ViewModelError(Error.CREATE, null));
                        }
                ));
    }
    public void createAce(String targetDeviceId, boolean isAuthCrypt, int permission, List<String> resources) {
        mDisposables.add(mCreateAclUseCase.execute(targetDeviceId, isAuthCrypt, permission, resources)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> {
                            mSuccess.setValue(false);
                            mError.setValue(new ViewModelError(Error.CREATE, null));
                        }
                ));
    }

    public enum Error implements ViewModelErrorType {
        CREATE,
        UPDATE,
        RETRIEVE_RESOURCES
    }
}
