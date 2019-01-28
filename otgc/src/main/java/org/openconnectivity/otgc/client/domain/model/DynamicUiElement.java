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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DynamicUiElement implements Serializable {

    private String mPath;
    private List<String> mResourceTypes;
    private List<String> mInterfaces;
    private List<DynamicUiProperty> mProperties;
    private List<String> mSupportedOperations;

    public DynamicUiElement() {
        this.mPath = "";
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public List<String> getResourceTypes() {
        if (mResourceTypes == null) {
            mResourceTypes = new ArrayList<>();
        }
        return mResourceTypes;
    }

    public void setResourceTypes(List<String> resourceTypes) {
        this.mResourceTypes = resourceTypes;
    }

    public List<String> getInterfaces() {
        if (mInterfaces == null) {
            mInterfaces = new ArrayList<>();
        }
        return mInterfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.mInterfaces = interfaces;
    }

    public List<DynamicUiProperty> getProperties() {
        if (mProperties == null) {
            mProperties = new ArrayList<>();
        }
        return mProperties;
    }

    public void setProperties(List<DynamicUiProperty> properties) {
        this.mProperties = properties;
    }

    public List<String> getSupportedOperations() {
        if (mSupportedOperations == null) {
            mSupportedOperations = new ArrayList<>();
        }
        return mSupportedOperations;
    }

    public void setSupportedOperations(List<String> supportedOperations) {
        this.mSupportedOperations = supportedOperations;
    }
}
