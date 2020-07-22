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

import org.openconnectivity.otgc.view.accesscontrol.AccessControlActivity;
import org.openconnectivity.otgc.view.accesscontrol.AceActivity;
import org.openconnectivity.otgc.view.client.ClientBuildersModule;
import org.openconnectivity.otgc.view.client.GenericClientActivity;
import org.openconnectivity.otgc.view.cloud.CloudActivity;
import org.openconnectivity.otgc.view.credential.CredActivity;
import org.openconnectivity.otgc.view.credential.CredentialsActivity;
import org.openconnectivity.otgc.view.devicelist.DeviceListBuildersModule;
import org.openconnectivity.otgc.view.devicelist.DeviceListActivity;
import org.openconnectivity.otgc.view.link.LinkedRolesActivity;
import org.openconnectivity.otgc.view.login.LoginActivity;
import org.openconnectivity.otgc.view.splash.SplashActivity;
import org.openconnectivity.otgc.view.trustanchor.CertificateActivity;
import org.openconnectivity.otgc.view.trustanchor.TrustAnchorActivity;
import org.openconnectivity.otgc.view.wlanscan.WlanScanActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface BuildersModule {

    @ContributesAndroidInjector
    abstract SplashActivity bindSplashActivity();

    @ContributesAndroidInjector
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector
    abstract WlanScanActivity bindWlanScanActivity();

    @ContributesAndroidInjector(modules = DeviceListBuildersModule.class)
    abstract DeviceListActivity bindDevicesActivity();

    @ContributesAndroidInjector
    abstract AccessControlActivity bindAccessControlActivity();

    @ContributesAndroidInjector
    abstract AceActivity bindAceActivity();

    @ContributesAndroidInjector
    abstract CredentialsActivity bindCredentialsActivity();

    @ContributesAndroidInjector
    abstract CredActivity bindCredActivity();

    @ContributesAndroidInjector(modules = ClientBuildersModule.class)
    abstract GenericClientActivity bindGenericClientActivity();

    @ContributesAndroidInjector
    abstract LinkedRolesActivity bindLinkedRolesActivity();

    @ContributesAndroidInjector
    abstract TrustAnchorActivity bindTrustAnchorActivity();

    @ContributesAndroidInjector
    abstract CertificateActivity bindCertificateActivity();

    @ContributesAndroidInjector
    abstract CloudActivity bindCloudActivity();
}
