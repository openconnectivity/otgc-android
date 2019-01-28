package org.openconnectivity.otgc.common.presentation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected final CompositeDisposable mDisposables = new CompositeDisposable();

    protected final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    protected final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

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
}
