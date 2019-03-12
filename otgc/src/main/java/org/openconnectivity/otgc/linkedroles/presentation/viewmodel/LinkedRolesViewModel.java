/*
 * *****************************************************************
 *
 *  Copyright 2019 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
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

package org.openconnectivity.otgc.linkedroles.presentation.viewmodel;

import org.openconnectivity.otgc.accesscontrol.presentation.viewmodel.AccessControlViewModel;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.devicelist.domain.model.Role;
import org.openconnectivity.otgc.linkedroles.domain.usecase.LinkRolesForClientUseCase;
import org.openconnectivity.otgc.linkedroles.domain.usecase.LinkRolesForServerUseCase;
import org.openconnectivity.otgc.linkedroles.domain.usecase.RetrieveLinkedRolesForClientUseCase;
import org.openconnectivity.otgc.linkedroles.domain.usecase.RetrieveLinkedRolesForServerUseCase;
import org.openconnectivity.otgc.linkedroles.domain.usecase.UnlinkRoleForClientUseCase;
import org.openconnectivity.otgc.linkedroles.domain.usecase.UnlinkRoleForServerUseCase;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

public class LinkedRolesViewModel extends ViewModel {

    private final RetrieveLinkedRolesForClientUseCase mRetrieveRolesForClientUseCase;
    private final RetrieveLinkedRolesForServerUseCase mRetrieveRolesForServerUseCase;
    private final LinkRolesForClientUseCase mLinkRolesForClientUseCase;
    private final LinkRolesForServerUseCase mLinkRolesForServerUseCase;
    private final UnlinkRoleForClientUseCase mUnlinkRoleForClientUseCase;
    private final UnlinkRoleForServerUseCase mUnlinkRoleForServerUseCase;

    private final SchedulersFacade mSchedulersFacade;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<String> mLinkedRoles = new MutableLiveData<>();
    private final MutableLiveData<String> mDeletedRoleId = new MutableLiveData<>();

    @Inject
    LinkedRolesViewModel(SchedulersFacade schedulersFacade,
                         RetrieveLinkedRolesForClientUseCase retrieveLinkedRolesForClientUseCase,
                         RetrieveLinkedRolesForServerUseCase retrieveLinkedRolesForServerUseCase,
                         LinkRolesForClientUseCase linkRolesForClientUseCase,
                         LinkRolesForServerUseCase linkRolesForServerUseCase,
                         UnlinkRoleForClientUseCase unlinkRoleForClientUseCase,
                         UnlinkRoleForServerUseCase unlinkRoleForServerUseCase) {
        this.mSchedulersFacade = schedulersFacade;

        this.mRetrieveRolesForClientUseCase = retrieveLinkedRolesForClientUseCase;
        this.mRetrieveRolesForServerUseCase = retrieveLinkedRolesForServerUseCase;
        this.mLinkRolesForClientUseCase = linkRolesForClientUseCase;
        this.mLinkRolesForServerUseCase = linkRolesForServerUseCase;
        this.mUnlinkRoleForClientUseCase = unlinkRoleForClientUseCase;
        this.mUnlinkRoleForServerUseCase = unlinkRoleForServerUseCase;
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

    public LiveData<String> getLinkedRoles() {
        return mLinkedRoles;
    }

    public LiveData<String> getDeletedRoleId() {
        return mDeletedRoleId;
    }


    public void retrieveLinkedRoles(String deviceId, Role deviceRole) {
        Single<List<String>> useCase;

        if (deviceRole.equals(Role.CLIENT)) {
            useCase = mRetrieveRolesForClientUseCase.execute(deviceId);
        } else {
            useCase = mRetrieveRolesForServerUseCase.execute(deviceId);
        }
        mDisposables.add(useCase
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        linkedRoles -> {
                            for (String role : linkedRoles) {
                                mLinkedRoles.setValue(role);
                            }
                        },
                        throwable -> mError.setValue(new ViewModelError(LinkedRolesViewModel.Error.RETRIEVE, null))
                ));
    }

    public void addLinkedRole(String deviceId, Role deviceRole, String roleId, String roleAuthority) {
        Completable useCase;
        if (deviceRole.equals(Role.CLIENT)) {
            useCase = mLinkRolesForClientUseCase.execute(deviceId, roleId, roleAuthority);
        } else {
            useCase = mLinkRolesForServerUseCase.execute(deviceId, roleId, roleAuthority);
        }

        mDisposables.add(useCase
            .subscribeOn(mSchedulersFacade.io())
            .observeOn(mSchedulersFacade.ui())
            .doOnSubscribe(__ -> mProcessing.setValue(true))
            .doFinally(() -> mProcessing.setValue(false))
            .subscribe(
                    () -> retrieveLinkedRoles(deviceId, deviceRole),
                    throwable -> mError.setValue(new ViewModelError(Error.CREATE, null))
            ));
    }

    public void deleteLinkedRole(String deviceId, Role deviceRole, String roleId) {
        Completable useCase;
        if (deviceRole.equals(Role.CLIENT)) {
            useCase = mUnlinkRoleForClientUseCase.execute(deviceId, roleId);
        } else {
            useCase = mUnlinkRoleForServerUseCase.execute(deviceId, roleId);
        }

        mDisposables.add(useCase
            .subscribeOn(mSchedulersFacade.io())
            .observeOn(mSchedulersFacade.ui())
            .doOnSubscribe(__ -> mProcessing.setValue(true))
            .doFinally(() -> mProcessing.setValue(false))
            .subscribe(
                    () -> mDeletedRoleId.setValue(roleId),
                    throwable -> mError.setValue(new ViewModelError(Error.DELETE, null))
            ));
    }

    public enum Error implements ViewModelErrorType {
        CREATE,
        RETRIEVE,
        DELETE
    }
}
