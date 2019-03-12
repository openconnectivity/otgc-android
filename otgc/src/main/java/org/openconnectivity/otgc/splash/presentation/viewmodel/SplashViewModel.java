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

package org.openconnectivity.otgc.splash.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.EmptyResultSetException;

import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.splash.domain.usecase.GrantPermissionsUseCase;
import org.openconnectivity.otgc.splash.domain.usecase.IsAuthenticatedUseCase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class SplashViewModel extends ViewModel {

    private final IsAuthenticatedUseCase mIsAuthenticatedUseCase;
    private final GrantPermissionsUseCase mGrantPermissionsUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mAuthenticated = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mMissedPermissions = new MutableLiveData<>();

    @Inject
    SplashViewModel(
            IsAuthenticatedUseCase isAuthenticatedUseCase,
            GrantPermissionsUseCase grantPermissionsUseCase,
            SchedulersFacade schedulersFacade) {
        this.mIsAuthenticatedUseCase = isAuthenticatedUseCase;
        this.mGrantPermissionsUseCase = grantPermissionsUseCase;

        this.mSchedulersFacade = schedulersFacade;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public LiveData<Boolean> isProcessing() {
        return mProcessing;
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public LiveData<Boolean> isAuthenticated() {
        return mAuthenticated;
    }

    public LiveData<List<String>> getMissedPermissions() {
        return mMissedPermissions;
    }

    public void checkIfIsAuthenticated() {
        disposables.add(mIsAuthenticatedUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        mAuthenticated::setValue,
                        throwable -> {
                            if (throwable instanceof EmptyResultSetException) {
                                mAuthenticated.setValue(false);
                            } else {
                                mError.setValue(
                                        new ViewModelError(
                                                Error.AUTHENTICATION_ERROR,
                                                throwable.getLocalizedMessage()
                                        )
                                );
                            }
                        }
                ));
    }

    public void checkIfPermissionsAreGranted() {
        disposables.add(mGrantPermissionsUseCase.getMissedPermissions()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        mMissedPermissions::setValue,
                        throwable ->
                            mError.setValue(
                                    new ViewModelError(
                                            Error.MISSED_PERMISSIONS,
                                            throwable.getLocalizedMessage()
                                    )
                            )
                ));
    }

    public enum Error implements ViewModelErrorType {
        AUTHENTICATION_ERROR,
        MISSED_PERMISSIONS
    }
}
