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

package org.openconnectivity.otgc.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.openconnectivity.otgc.utils.constant.OtgcMode;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AndroidPreferencesRepository implements PreferencesRepository {
    private static final String DISCOVERY_TIMEOUT_DEFAULT = "5";
    private static final String REQUESTS_DELAY_DEFAULT = "1";
    private static final String DISCOVERY_SCOPE_DEFAULT = "Link-Local";

    private final SharedPreferences mPreferences;

    @Inject
    AndroidPreferencesRepository(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getDiscoveryTimeout() {
        //return mPreferences.getInt("discovery_timeout", DISCOVERY_TIMEOUT_DEFAULT);
        return Integer.parseInt(
                mPreferences.getString("discovery_timeout", DISCOVERY_TIMEOUT_DEFAULT));
    }

    @Override
    public int getRequestsDelay() {
        return Integer.parseInt(
                mPreferences.getString("requests_delay", REQUESTS_DELAY_DEFAULT));
    }

    @Override
    public boolean isFirstRun() {
        return mPreferences.getBoolean("FIRSTRUN", true);
    }

    @Override
    public void setFirstRun(boolean firstRun) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("FIRSTRUN", false);
        editor.apply();
    }

    @Override
    public String getPiid() {
        return mPreferences.getString("Piid", "");
    }

    @Override
    public void setPiid(String piid) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("Piid", piid);
        editor.apply();
    }

    @Override
    public String getMode() {
        return mPreferences.getString("Mode", OtgcMode.OBT);
    }

    @Override
    public void setMode(String mode) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("Mode", mode);
        editor.apply();
    }

    @Override
    public String getDiscoveryScope() {
        return mPreferences.getString("discovery_scope", DISCOVERY_SCOPE_DEFAULT);
    }
}
