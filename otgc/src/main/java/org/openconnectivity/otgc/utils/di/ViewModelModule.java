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

package org.openconnectivity.otgc.utils.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.openconnectivity.otgc.viewmodel.AccessControlViewModel;
import org.openconnectivity.otgc.viewmodel.AceViewModel;
import org.openconnectivity.otgc.viewmodel.ResourceViewModel;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelFactory;
import org.openconnectivity.otgc.viewmodel.GenericClientViewModel;
import org.openconnectivity.otgc.viewmodel.CredViewModel;
import org.openconnectivity.otgc.viewmodel.CredentialsViewModel;
import org.openconnectivity.otgc.viewmodel.DeviceListViewModel;
import org.openconnectivity.otgc.viewmodel.SharedViewModel;
import org.openconnectivity.otgc.viewmodel.DoxsViewModel;
import org.openconnectivity.otgc.viewmodel.LinkedRolesViewModel;
import org.openconnectivity.otgc.viewmodel.LoginViewModel;
import org.openconnectivity.otgc.viewmodel.SplashViewModel;
import org.openconnectivity.otgc.viewmodel.WlanScanViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel.class)
    abstract ViewModel bindSplashViewModel(SplashViewModel splashViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(WlanScanViewModel.class)
    abstract ViewModel bindWlanScanViewModel(WlanScanViewModel wlanScanViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DeviceListViewModel.class)
    abstract ViewModel bindDeviceListViewModel(DeviceListViewModel deviceListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DoxsViewModel.class)
    abstract ViewModel bindDoxsViewModel(DoxsViewModel doxsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel.class)
    abstract ViewModel bindSharedViewModel(SharedViewModel sharedViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AccessControlViewModel.class)
    abstract ViewModel bindAccessControlViewModel(AccessControlViewModel accessControlViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AceViewModel.class)
    abstract ViewModel bindAceViewModel(AceViewModel aceViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CredentialsViewModel.class)
    abstract ViewModel bindCredentialsViewModel(CredentialsViewModel credentialsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CredViewModel.class)
    abstract ViewModel bindCredViewModel(CredViewModel credViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GenericClientViewModel.class)
    abstract ViewModel bindGenericClientViewModel(GenericClientViewModel genericClientViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LinkedRolesViewModel.class)
    abstract ViewModel bindLinkedRolesViewModel(LinkedRolesViewModel linkedRolesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ResourceViewModel.class)
    abstract ViewModel bindResourceViewModel(ResourceViewModel resourceViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
