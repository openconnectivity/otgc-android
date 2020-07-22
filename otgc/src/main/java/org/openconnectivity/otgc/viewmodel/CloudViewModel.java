package org.openconnectivity.otgc.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.openconnectivity.otgc.domain.model.resource.cloud.OcCloudConfiguration;
import org.openconnectivity.otgc.domain.usecase.cloud.ProvisionCloudConfUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.RetrieveCloudConfigurationUseCase;
import org.openconnectivity.otgc.domain.usecase.cloud.RetrieveStatusUseCase;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelErrorType;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CloudViewModel extends ViewModel {

    private CompositeDisposable disposable = new CompositeDisposable();

    private final SchedulersFacade schedulersFacade;

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mSuccess = new MutableLiveData<>();

    // Use cases
    private final RetrieveStatusUseCase retrieveStatusUseCase;
    private final RetrieveCloudConfigurationUseCase retrieveCloudConfigurationUseCase;
    private final ProvisionCloudConfUseCase provisionCloudConfUseCase;

    // Observable values
    private final MutableLiveData<Integer> status = new MutableLiveData<>();
    private final MutableLiveData<OcCloudConfiguration> cloudConf = new MutableLiveData<>();

    @Inject
    public CloudViewModel(SchedulersFacade schedulersFacade,
                          RetrieveStatusUseCase retrieveStatusUseCase,
                          RetrieveCloudConfigurationUseCase retrieveCloudConfigurationUseCase,
                          ProvisionCloudConfUseCase provisionCloudConfUseCase) {
        this.schedulersFacade = schedulersFacade;
        this.retrieveStatusUseCase = retrieveStatusUseCase;
        this.retrieveCloudConfigurationUseCase = retrieveCloudConfigurationUseCase;
        this.provisionCloudConfUseCase = provisionCloudConfUseCase;
    }

    @Override
    protected void onCleared() {
        disposable.clear();
    }

    public LiveData<Boolean> isProcessing() {
        return mProcessing;
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public LiveData<Boolean> getSuccess() {
        return mSuccess;
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public LiveData<OcCloudConfiguration> getCloudConfiguration() {
        return cloudConf;
    }

    public void retrieveStatus() {
        disposable.add(retrieveStatusUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        status::setValue,
                        throwable -> mError.setValue(new ViewModelError(Error.RETRIEVE_STATUS, throwable.getMessage()))
                ));
    }

    public void retrieveCloudConfiguration() {
        disposable.add(retrieveCloudConfigurationUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        cloudConf::setValue,
                        throwable -> mError.setValue(new ViewModelError(Error.RETRIEVE_CONFIGURATION, throwable.getMessage()))
                ));
    }

    public void provisionCloudConfiguration(String authProvider, String cloudUrl, String accessToken, String cloudUuid) {
        disposable.add(provisionCloudConfUseCase.execute(authProvider, cloudUrl, accessToken, cloudUuid)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> mError.setValue(new ViewModelError(Error.PROVISION_CONFIGURATION, throwable.getMessage()))
                ));
    }

    public enum Error implements ViewModelErrorType {
        RETRIEVE_STATUS,
        RETRIEVE_CONFIGURATION,
        PROVISION_CONFIGURATION
    }
}
