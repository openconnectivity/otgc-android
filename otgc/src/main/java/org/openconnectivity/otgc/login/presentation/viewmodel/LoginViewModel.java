/* ******************************************************************
 *
 * Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 * ******************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ******************************************************************/
package org.openconnectivity.otgc.login.presentation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.login.domain.usecase.SaveCredentialsUseCase;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class LoginViewModel extends ViewModel {

    private final SaveCredentialsUseCase saveCredentialsUseCase;

    private final SchedulersFacade schedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mAuthenticated = new MutableLiveData<>();

    @Inject
    LoginViewModel(
            SaveCredentialsUseCase saveCredentialsUseCase,
            SchedulersFacade schedulersFacade) {
        this.saveCredentialsUseCase = saveCredentialsUseCase;

        this.schedulersFacade = schedulersFacade;
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

    public void authenticate(String username, String password) {
        disposables.add(saveCredentialsUseCase.execute(username, password)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mAuthenticated.setValue(true),
                        throwable -> mError.setValue(new ViewModelError(Error.AUTHENTICATE, null))
                ));
    }

    public enum Error implements ViewModelErrorType {
        AUTHENTICATE
    }
}
