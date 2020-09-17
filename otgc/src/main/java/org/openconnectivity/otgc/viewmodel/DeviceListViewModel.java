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

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.iotivity.OCRandomPinHandler;
import org.openconnectivity.otgc.domain.model.exception.NetworkDisconnectedException;
import org.openconnectivity.otgc.domain.usecase.CloseIotivityUseCase;
import org.openconnectivity.otgc.domain.usecase.GetModeUseCase;
import org.openconnectivity.otgc.domain.usecase.ResetClientModeUseCase;
import org.openconnectivity.otgc.domain.usecase.ResetObtModeUseCase;
import org.openconnectivity.otgc.domain.usecase.SetClientModeUseCase;
import org.openconnectivity.otgc.domain.usecase.SetObtModeUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.CloudDeregisterUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.CloudLoginUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.CloudLogoutUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.CloudRefreshTokenUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.CloudRegisterUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.RetrieveStatusUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.RetrieveTokenExpiryUseCase;
import org.openconnectivity.otgc.domain.usecase.wifi.CheckConnectionUseCase;
import org.openconnectivity.otgc.domain.usecase.InitializeIotivityUseCase;
import org.openconnectivity.otgc.domain.usecase.login.LogoutUseCase;
import org.openconnectivity.otgc.utils.handler.DisplayNotValidCertificateHandler;
import org.openconnectivity.otgc.utils.handler.OCSetRandomPinHandler;
import org.openconnectivity.otgc.utils.viewmodel.CommonError;
import org.openconnectivity.otgc.utils.viewmodel.Response;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.domain.usecase.GetDeviceIdUseCase;
import org.openconnectivity.otgc.domain.usecase.SetDisplayPinListenerUseCase;
import org.openconnectivity.otgc.domain.usecase.SetRandomPinListenerUseCase;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelErrorType;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class DeviceListViewModel extends ViewModel {

    private final InitializeIotivityUseCase mInitializeIotivityUseCase;
    private final GetModeUseCase mGetModeUseCase;
    private final CloseIotivityUseCase closeIotivityUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SetRandomPinListenerUseCase setRandomPinListenerUseCase;
    private final SetDisplayPinListenerUseCase setDisplayPinListenerUseCase;
    private final SetClientModeUseCase setClientModeUseCase;
    private final ResetClientModeUseCase resetClientModeUseCase;
    private final SetObtModeUseCase setObtModeUseCase;
    private final ResetObtModeUseCase resetObtModeUseCase;
    private final CheckConnectionUseCase mCheckConnectionUseCase;
    private final GetDeviceIdUseCase mGetDeviceIdUseCase;
    private final RetrieveStatusUseCase retrieveStatusUseCase;
    private final CloudRegisterUseCase cloudRegisterUseCase;
    private final CloudDeregisterUseCase cloudDeregisterUseCase;
    private final CloudLoginUseCase cloudLoginUseCase;
    private final CloudLogoutUseCase cloudLogoutUseCase;
    private final CloudRefreshTokenUseCase refreshTokenUseCase;
    private final RetrieveTokenExpiryUseCase retrieveTokenExpiryUseCase;

    private final SchedulersFacade schedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mInit = new MutableLiveData<>();
    private final MutableLiveData<String> mMode = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> clientModeResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> obtModeResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> logoutResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Boolean>> connectedResponse = new MutableLiveData<>();
    private final MutableLiveData<String> mDeviceId = new MutableLiveData<>();
    private final MutableLiveData<Response<Integer>> statusResponse = new MutableLiveData<>();

    @Inject
    DeviceListViewModel(
            InitializeIotivityUseCase initializeIotivityUseCase,
            GetModeUseCase getModeUseCase,
            CloseIotivityUseCase closeIotivityUseCase,
            LogoutUseCase logoutUseCase,
            SetRandomPinListenerUseCase setRandomPinListenerUseCase,
            SetDisplayPinListenerUseCase setDisplayPinListenerUseCase,
            SetClientModeUseCase setClientModeUseCase,
            ResetClientModeUseCase resetClientModeUseCase,
            SetObtModeUseCase setObtModeUseCase,
            ResetObtModeUseCase resetObtModeUseCase,
            CheckConnectionUseCase checkConnectionUseCase,
            GetDeviceIdUseCase getDeviceIdUseCase,
            RetrieveStatusUseCase retrieveStatusUseCase,
            CloudRegisterUseCase cloudRegisterUseCase,
            CloudDeregisterUseCase cloudDeregisterUseCase,
            CloudLoginUseCase cloudLoginUseCase,
            CloudLogoutUseCase cloudLogoutUseCase,
            CloudRefreshTokenUseCase refreshTokenUseCase,
            RetrieveTokenExpiryUseCase retrieveTokenExpiryUseCase,
            SchedulersFacade schedulersFacade) {
        this.mInitializeIotivityUseCase = initializeIotivityUseCase;
        this.mGetModeUseCase = getModeUseCase;
        this.closeIotivityUseCase = closeIotivityUseCase;
        this.logoutUseCase = logoutUseCase;
        this.setRandomPinListenerUseCase = setRandomPinListenerUseCase;
        this.setDisplayPinListenerUseCase = setDisplayPinListenerUseCase;
        this.setClientModeUseCase = setClientModeUseCase;
        this.resetClientModeUseCase = resetClientModeUseCase;
        this.setObtModeUseCase = setObtModeUseCase;
        this.resetObtModeUseCase = resetObtModeUseCase;
        this.mCheckConnectionUseCase = checkConnectionUseCase;
        this.mGetDeviceIdUseCase = getDeviceIdUseCase;
        this.retrieveStatusUseCase = retrieveStatusUseCase;
        this.cloudRegisterUseCase = cloudRegisterUseCase;
        this.cloudDeregisterUseCase = cloudDeregisterUseCase;
        this.cloudLoginUseCase = cloudLoginUseCase;
        this.cloudLogoutUseCase = cloudLogoutUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.retrieveTokenExpiryUseCase = retrieveTokenExpiryUseCase;

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

    public LiveData<String> getMode() {
        return mMode;
    }

    public LiveData<Response<Void>> getClientModeResponse() {
        return clientModeResponse;
    }

    public LiveData<Response<Void>> getObtModeResponse() {
        return obtModeResponse;
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

    public LiveData<Response<Integer>> getStatusResponse() {
        return statusResponse;
    }

    public void initializeIotivityStack(Context context, DisplayNotValidCertificateHandler displayNotValidCertificateHandler) {
        disposables.add(mInitializeIotivityUseCase.execute(context, displayNotValidCertificateHandler)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () ->  {
                            mInit.setValue(true);
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(schedulersFacade.io())
                                    .observeOn(schedulersFacade.ui())
                                    .subscribe(
                                        mode -> mMode.setValue(mode),
                                        throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> {
                            // mError.setValue()
                            mInit.setValue(false);
                        }
                ));
    }

    public void retrieveCloudStatus() {
        disposables.add(retrieveStatusUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void cloudRegister() {
        disposables.add(cloudRegisterUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void cloudDeregister() {
        disposables.add(cloudDeregisterUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void cloudLogin() {
        disposables.add(cloudLoginUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void cloudLogout() {
        disposables.add(cloudLogoutUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void retrieveTokenExpiry() {
        disposables.add(retrieveTokenExpiryUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () -> {},
                        throwable -> {}
                ));
    }

    public void refreshToken() {
        disposables.add(refreshTokenUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
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

    public void setClientMode() {
        disposables.add(setClientModeUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> clientModeResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {
                            clientModeResponse.setValue(Response.success(null));
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(schedulersFacade.io())
                                    .observeOn(schedulersFacade.ui())
                                    .subscribe(
                                            mode -> mMode.setValue(mode),
                                            throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> clientModeResponse.setValue(Response.error(throwable))
                ));
    }

    public void resetClientMode() {
        disposables.add(resetClientModeUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> clientModeResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {
                            clientModeResponse.setValue(Response.success(null));
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(schedulersFacade.io())
                                    .observeOn(schedulersFacade.ui())
                                    .subscribe(
                                            mode -> mMode.setValue(mode),
                                            throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> clientModeResponse.setValue(Response.error(throwable))
                ));
    }

    public void setObtMode() {
        disposables.add(setObtModeUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> obtModeResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {
                            obtModeResponse.setValue(Response.success(null));
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(schedulersFacade.io())
                                    .observeOn(schedulersFacade.ui())
                                    .subscribe(
                                            mode -> mMode.setValue(mode),
                                            throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> obtModeResponse.setValue(Response.error(throwable))
                ));
    }

    public void resetObtMode() {
        disposables.add(resetObtModeUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> obtModeResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {
                            obtModeResponse.setValue(Response.success(null));
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(schedulersFacade.io())
                                    .observeOn(schedulersFacade.ui())
                                    .subscribe(
                                            mode -> mMode.setValue(mode),
                                            throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> obtModeResponse.setValue(Response.error(throwable))
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

    public enum Error implements ViewModelErrorType {
        GET_MODE
    }
}
