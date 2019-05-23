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

import org.iotivity.OCRandomPinHandler;
import org.openconnectivity.otgc.domain.model.exception.NetworkDisconnectedException;
import org.openconnectivity.otgc.domain.usecase.CloseIotivityUseCase;
import org.openconnectivity.otgc.domain.usecase.wifi.CheckConnectionUseCase;
import org.openconnectivity.otgc.domain.usecase.InitializeIotivityUseCase;
import org.openconnectivity.otgc.domain.usecase.login.LogoutUseCase;
import org.openconnectivity.otgc.utils.handler.OCSetRandomPinHandler;
import org.openconnectivity.otgc.utils.viewmodel.CommonError;
import org.openconnectivity.otgc.utils.viewmodel.Response;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.domain.usecase.GetDeviceIdUseCase;
import org.openconnectivity.otgc.domain.usecase.SetDisplayPinListenerUseCase;
import org.openconnectivity.otgc.domain.usecase.SetRandomPinListenerUseCase;
import org.openconnectivity.otgc.domain.usecase.SetRfnopModeUseCase;
import org.openconnectivity.otgc.domain.usecase.SetRfotmModeUseCase;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class DeviceListViewModel extends ViewModel {

    private final InitializeIotivityUseCase mInitializeIotivityUseCase;
    private final CloseIotivityUseCase closeIotivityUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SetRandomPinListenerUseCase setRandomPinListenerUseCase;
    private final SetDisplayPinListenerUseCase setDisplayPinListenerUseCase;
    private final SetRfotmModeUseCase setRfotmModeUseCase;
    private final SetRfnopModeUseCase setRfnopModeUseCase;
    private final CheckConnectionUseCase mCheckConnectionUseCase;
    private final GetDeviceIdUseCase mGetDeviceIdUseCase;

    private final SchedulersFacade schedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mInit = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> rfotmResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> rfnopResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> logoutResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Boolean>> connectedResponse = new MutableLiveData<>();
    private final MutableLiveData<String> mDeviceId = new MutableLiveData<>();

    @Inject
    DeviceListViewModel(
            InitializeIotivityUseCase initializeIotivityUseCase,
            CloseIotivityUseCase closeIotivityUseCase,
            LogoutUseCase logoutUseCase,
            SetRandomPinListenerUseCase setRandomPinListenerUseCase,
            SetDisplayPinListenerUseCase setDisplayPinListenerUseCase,
            SetRfotmModeUseCase setRfotmModeUseCase,
            SetRfnopModeUseCase setRfnopModeUseCase,
            CheckConnectionUseCase checkConnectionUseCase,
            GetDeviceIdUseCase getDeviceIdUseCase,
            SchedulersFacade schedulersFacade) {
        this.mInitializeIotivityUseCase = initializeIotivityUseCase;
        this.closeIotivityUseCase = closeIotivityUseCase;
        this.logoutUseCase = logoutUseCase;
        this.setRandomPinListenerUseCase = setRandomPinListenerUseCase;
        this.setDisplayPinListenerUseCase = setDisplayPinListenerUseCase;
        this.setRfotmModeUseCase = setRfotmModeUseCase;
        this.setRfnopModeUseCase = setRfnopModeUseCase;
        this.mCheckConnectionUseCase = checkConnectionUseCase;
        this.mGetDeviceIdUseCase = getDeviceIdUseCase;

        this.schedulersFacade = schedulersFacade;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public LiveData<Boolean> getInit() {
        return mInit;
    }

    public LiveData<Response<Void>> getRfotmResponse() {
        return rfotmResponse;
    }

    public LiveData<Response<Void>> getRfnopResponse() {
        return rfnopResponse;
    }

    public LiveData<Response<Void>> getLogoutResponse() {
        return logoutResponse;
    }

    public LiveData<Response<Boolean>> getConnectedResponse() {
        return connectedResponse;
    }

    public LiveData<String> getDeviceId() {
        return mDeviceId;
    }

    public void initializeIotivityStack() {
        disposables.add(mInitializeIotivityUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () -> mInit.setValue(true),
                        throwable -> {
                            // mError.setValue()
                            mInit.setValue(false);
                        }
                ));
    }

    public void closeIotivityStack() {
        closeIotivityUseCase.execute();
    }

    public void logout() {
        disposables.add(logoutUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () -> logoutResponse.setValue(Response.success(null)),
                        throwable -> logoutResponse.setValue(Response.error(throwable))
                ));
    }

    public void setRandomPinListener(OCSetRandomPinHandler randomPinCallbackListener) {
        setRandomPinListenerUseCase.execute(randomPinCallbackListener);
    }

    public void setDisplayPinListener(OCRandomPinHandler displayPinListener) {
        setDisplayPinListenerUseCase.execute(displayPinListener);
    }

    public void setRfotmMode() {
        disposables.add(mCheckConnectionUseCase.executeCompletable()
                .andThen(setRfotmModeUseCase.execute())
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> rfotmResponse.setValue(Response.loading()))
                .subscribe(
                        () -> rfotmResponse.setValue(Response.success(null)),
                        throwable -> {
                            if (throwable instanceof NetworkDisconnectedException) {
                                mError.setValue(new ViewModelError(CommonError.NETWORK_DISCONNECTED, null));
                            } else {
                                rfotmResponse.setValue(Response.error(throwable));
                            }
                        }
                ));
    }

    public void setRfnopMode() {
        disposables.add(setRfnopModeUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> rfnopResponse.setValue(Response.loading()))
                .subscribe(
                        () -> rfnopResponse.setValue(Response.success(null)),
                        throwable -> {
                            if (throwable instanceof NetworkDisconnectedException) {
                                mError.setValue(new ViewModelError(CommonError.NETWORK_DISCONNECTED, null));
                            } else {
                                rfnopResponse.setValue(Response.error(throwable));
                            }
                        }
                ));
    }

    public void checkIfIsConnectedToWifi() {
        disposables.add(mCheckConnectionUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        isConnected -> connectedResponse.setValue(Response.success(isConnected)),
                        throwable -> {
                            if (throwable instanceof NetworkDisconnectedException) {
                                mError.setValue(new ViewModelError(CommonError.NETWORK_DISCONNECTED, null));
                            } else {
                                connectedResponse.setValue(Response.error(throwable));
                            }
                        }
                ));
    }

    public void retrieveDeviceId() {
        disposables.add(mGetDeviceIdUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        mDeviceId::setValue,
                        throwable -> {}
                ));
    }
}
