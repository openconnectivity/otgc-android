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

import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.usecase.accesscontrol.CreateAclUseCase;
import org.openconnectivity.otgc.domain.usecase.RetrieveVerticalResourcesUseCase;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class AceViewModel extends ViewModel {

    private final CreateAclUseCase mCreateAclUseCase;
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
            RetrieveVerticalResourcesUseCase retrieveVerticalResourcesUseCase,
            SchedulersFacade schedulersFacade) {
        this.mCreateAclUseCase = createAclUseCase;
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

    public void retrieveResources(Device device) {
        mDisposables.add(mRetrieveVerticalResourcesUseCase.execute(device)
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

    public void createAce(Device device, String subjectId, int permission, List<String> resources) {
        mDisposables.add(mCreateAclUseCase.execute(device, subjectId, resources, permission)
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

    public void createAce(Device device, String roleId, String roleAuthority, int permission, List<String> resources) {
        mDisposables.add(mCreateAclUseCase.execute(device, roleId, roleAuthority, resources, permission)
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
    public void createAce(Device device, boolean isAuthCrypt, int permission, List<String> resources) {
        mDisposables.add(mCreateAclUseCase.execute(device, isAuthCrypt, resources, permission)
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
