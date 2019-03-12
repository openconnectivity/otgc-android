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

package org.openconnectivity.otgc.client.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.iotivity.base.OcRepresentation;
import org.openconnectivity.otgc.client.domain.usecase.CancelObservation;
import org.openconnectivity.otgc.client.domain.usecase.GetRequestUseCase;
import org.openconnectivity.otgc.client.domain.usecase.ObserveResource;
import org.openconnectivity.otgc.client.domain.usecase.PostRequestUseCase;
import org.openconnectivity.otgc.client.domain.usecase.PutRequestUseCase;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ResourceViewModel extends ViewModel {

    private final GetRequestUseCase mGetRequestUseCase;
    private final PostRequestUseCase mPostRequestUseCase;
    private final PutRequestUseCase mPutRequestUseCase;
    private final ObserveResource mObserveResource;
    private final CancelObservation mCancelObservation;

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<OcRepresentation> mResponse = new MutableLiveData<>();

    @Inject
    ResourceViewModel(
            GetRequestUseCase getRequestUseCase,
            PostRequestUseCase postRequestUseCase,
            PutRequestUseCase putRequestUseCase,
            ObserveResource observeResource,
            CancelObservation cancelObservation,
            SchedulersFacade schedulersFacade) {
        this.mGetRequestUseCase = getRequestUseCase;
        this.mPostRequestUseCase = postRequestUseCase;
        this.mPutRequestUseCase = putRequestUseCase;
        this.mObserveResource = observeResource;
        this.mCancelObservation = cancelObservation;

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

    public LiveData<OcRepresentation> getResponse() {
        return mResponse;
    }

    public void getRequest(String deviceId, String uri, List<String> resourceTypes, List<String> interfacesList) {
        disposables.add(mGetRequestUseCase.execute(deviceId, uri, resourceTypes, interfacesList)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        mResponse::setValue,
                        throwable -> mError.setValue(new ViewModelError(Error.GET, null))
                ));
    }

    public void observeRequest(String deviceId, String uri, List<String> resourceTypes, List<String> interfaces) {
        disposables.add(mObserveResource.execute(deviceId, uri, resourceTypes, interfaces)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        mResponse::setValue,
                        throwable -> mError.setValue(new ViewModelError(Error.OBSERVE, null))
                ));
    }

    public void cancelObserveRequest(String uri) {
        disposables.add(mCancelObservation.execute(uri)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () -> {},
                        throwable -> mError.setValue(new ViewModelError(Error.CANCEL_OBSERVE, null))
                ));
    }

    public void postRequest(String deviceId, String uri, List<String> resourceTypes, List<String> interfacesList, OcRepresentation rep) {
        disposables.add(mPostRequestUseCase.execute(deviceId, uri, resourceTypes, interfacesList, rep)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        response -> {},//mResponse::setValue,
                        throwable -> mError.setValue(new ViewModelError(Error.POST, null))
                ));
    }

    public void putRequest(String deviceId, String uri, List<String> resourceTypes, List<String> interfacesList, OcRepresentation rep) {
        disposables.add(mPutRequestUseCase.execute(deviceId, uri, resourceTypes, interfacesList, rep)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        response -> {},//mResponse::setValue,
                        throwable -> mError.setValue(new ViewModelError(Error.PUT, null))
                ));
    }

    public enum Error implements ViewModelErrorType {
        GET,
        POST,
        PUT,
        OBSERVE,
        CANCEL_OBSERVE
    }
}
