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

package org.openconnectivity.otgc.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.openconnectivity.otgc.accesscontrol.presentation.viewmodel.AccessControlViewModel;
import org.openconnectivity.otgc.accesscontrol.presentation.viewmodel.AceViewModel;
import org.openconnectivity.otgc.client.presentation.viewmodel.ResourceViewModel;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelFactory;
import org.openconnectivity.otgc.client.presentation.viewmodel.GenericClientViewModel;
import org.openconnectivity.otgc.credential.presentation.viewmodel.CredViewModel;
import org.openconnectivity.otgc.credential.presentation.viewmodel.CredentialsViewModel;
import org.openconnectivity.otgc.devicelist.presentation.viewmodel.DeviceListViewModel;
import org.openconnectivity.otgc.devicelist.presentation.viewmodel.SharedViewModel;
import org.openconnectivity.otgc.devicelist.presentation.viewmodel.DoxsViewModel;
import org.openconnectivity.otgc.linkedroles.presentation.viewmodel.LinkedRolesViewModel;
import org.openconnectivity.otgc.login.presentation.viewmodel.LoginViewModel;
import org.openconnectivity.otgc.splash.presentation.viewmodel.SplashViewModel;
import org.openconnectivity.otgc.wlanscan.presentation.viewmodel.WlanScanViewModel;

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
