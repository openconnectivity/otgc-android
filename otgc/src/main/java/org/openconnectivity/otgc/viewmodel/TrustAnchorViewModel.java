package org.openconnectivity.otgc.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.domain.usecase.trustanchor.GetTrustAnchorUseCase;
import org.openconnectivity.otgc.domain.usecase.trustanchor.RemoveTrustAnchorByCredidUseCase;
import org.openconnectivity.otgc.domain.usecase.trustanchor.SaveEndEntityCertificateUseCase;
import org.openconnectivity.otgc.domain.usecase.trustanchor.SaveIntermediateCertificateUseCase;
import org.openconnectivity.otgc.domain.usecase.trustanchor.StoreTrustAnchorUseCase;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelErrorType;

import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class TrustAnchorViewModel extends ViewModel {

    private CompositeDisposable disposable = new CompositeDisposable();

    private final SchedulersFacade schedulersFacade;

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    // Use cases
    private final StoreTrustAnchorUseCase storeTrustAnchorUseCase;
    private final SaveIntermediateCertificateUseCase saveIntermediateCertificateUseCase;
    private final SaveEndEntityCertificateUseCase saveEndEntityCertificateUseCase;
    private final GetTrustAnchorUseCase getTrustAnchorUseCase;
    private final RemoveTrustAnchorByCredidUseCase removeTrustAnchorByCredidUseCase;

    // Observable values
    private final MutableLiveData<OcCredential> credential = new MutableLiveData<>();
    private final MutableLiveData<Long> deleteCredid = new MutableLiveData<>();

    @Inject
    public TrustAnchorViewModel(SchedulersFacade schedulersFacade,
                                StoreTrustAnchorUseCase storeTrustAnchorUseCase,
                                SaveIntermediateCertificateUseCase saveIntermediateCertificateUseCase,
                                SaveEndEntityCertificateUseCase saveEndEntityCertificateUseCase,
                                GetTrustAnchorUseCase getTrustAnchorUseCase,
                                RemoveTrustAnchorByCredidUseCase removeTrustAnchorByCredidUseCase) {
        this.schedulersFacade = schedulersFacade;
        this.storeTrustAnchorUseCase = storeTrustAnchorUseCase;
        this.saveIntermediateCertificateUseCase = saveIntermediateCertificateUseCase;
        this.saveEndEntityCertificateUseCase = saveEndEntityCertificateUseCase;
        this.getTrustAnchorUseCase = getTrustAnchorUseCase;
        this.removeTrustAnchorByCredidUseCase = removeTrustAnchorByCredidUseCase;
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

    public LiveData<OcCredential> getCredential() {
        return credential;
    }

    public LiveData<Long> getDeletedCredid() {
        return deleteCredid;
    }

    public void retrieveCertificates() {
        disposable.add(getTrustAnchorUseCase.execute()
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe(
                    trustAnchors -> {
                        credential.setValue(null);
                        for (OcCredential trustAnchor : trustAnchors) {
                            credential.setValue(trustAnchor);
                        }
                    },
                    throwable -> {}
            ));
    }

    public void addTrustAnchor(InputStream is) {
        disposable.add(storeTrustAnchorUseCase.execute(is)
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe(
                    () -> retrieveCertificates(),
                    throwable -> mError.setValue(new ViewModelError(Error.ADD_ROOT_CERT, throwable.getMessage()))
            ));
    }

    public void saveIntermediateCertificate(Integer credid, InputStream is) {
        disposable.add(saveIntermediateCertificateUseCase.execute(credid, is)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () -> retrieveCertificates(),
                        throwable -> mError.setValue(new ViewModelError(Error.ADD_ROOT_CERT, throwable.getMessage()))
                ));
    }

    public void saveEndEntityCertificate(InputStream fileIs, InputStream keyIs) {
        disposable.add(saveEndEntityCertificateUseCase.execute(fileIs, keyIs)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () -> retrieveCertificates(),
                        throwable -> mError.setValue(new ViewModelError(Error.ADD_ROOT_CERT, throwable.getMessage()))
                ));
    }

    public void removeCertificateByCredid(long credid) {
        disposable.add(removeTrustAnchorByCredidUseCase.execute(credid)
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe(
                    () -> deleteCredid.setValue(credid),
                    throwable -> {}
            ));
    }

    public enum Error implements ViewModelErrorType {
        ADD_ROOT_CERT
    }
}
