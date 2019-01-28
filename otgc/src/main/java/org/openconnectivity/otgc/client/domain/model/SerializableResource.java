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

package org.openconnectivity.otgc.client.domain.model;

import android.support.annotation.NonNull;

import org.iotivity.base.OcResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableResource implements Serializable {
    private String mUri;
    private List<String> mHosts;
    private List<String> mTypes;
    private List<String> mInterfaces;
    private boolean mObservable;

    public SerializableResource(OcResource ocResource) {
        this.mUri = ocResource.getUri();
        this.mHosts = ocResource.getAllHosts();
        this.mTypes = ocResource.getResourceTypes();
        this.mInterfaces = ocResource.getResourceInterfaces();
        this.mObservable = ocResource.isObservable();
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public @NonNull List<String> getHosts() {
        if (mHosts == null) {
            this.mHosts = new ArrayList<>();
        }

        return mHosts;
    }

    public void setHosts(List<String> hosts) {
        this.mHosts = hosts;
    }

    public @NonNull List<String> getResourceTypes() {
        if (mTypes == null) {
            this.mTypes = new ArrayList<>();
        }

        return mTypes;
    }

    public void setResourceTypes(List<String> types) {
        this.mTypes = types;
    }

    public @NonNull List<String> getResourceInterfaces() {
        if (mInterfaces == null) {
            this.mInterfaces = new ArrayList<>();
        }

        return mInterfaces;
    }

    public void setResourceInterfaces(List<String> interfaces) {
        this.mInterfaces = interfaces;
    }

    public boolean isObservable() {
        return mObservable;
    }

    public void setObservable(boolean isObservable) {
        this.mObservable = isObservable;
    }
}
